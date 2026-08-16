from pathlib import Path
from random import Random

import nbtlib
import typer
from nbtlib import Byte, Compound, Double, Int, List, String

DEFAULT_SOURCE = Path("forge/run/saves/Structures/generated/immersive_pillagers/structures")
DEFAULT_DEST = Path("common/src/main/resources/data/immersive_pillagers/structures")

STRUCTURE_LOOT_TABLES = {
    "camp": {
        "minecraft:chest": "immersive_pillagers:chests/camp_chest",
        "minecraft:barrel": "immersive_pillagers:chests/camp_barrel",
        "immersive_pillagers:reinforced_chest": "immersive_pillagers:chests/camp_reinforced",
    },
    "lab": {
        "minecraft:chest": "immersive_pillagers:chests/camp_chest",
        "minecraft:barrel": "immersive_pillagers:chests/lab_barrel",
        "immersive_pillagers:reinforced_chest": "immersive_pillagers:chests/lab_reinforced",
    },
}

REMAINS_LOOT_TABLES = {
    "minecraft:chest": "immersive_pillagers:chests/remains_container",
    "minecraft:barrel": "immersive_pillagers:chests/remains_container",
    "minecraft:brushable_block": "immersive_pillagers:archaeology/remains",
}

RANDOM_ENTITY_SPAWNS = {
    "camp": {
        "air": {
            "minecraft:pillager": 5,
            "minecraft:vindicator": 3,
            "minecraft:evoker": 1,
        }
    },
    "lab": {
        "air": {
            "immersive_pillagers:undead_pillager": 10,
        },
        "water": {
            "minecraft:elder_guardian": 1,
        },
    }
}

STRUCTURE_BLOCK_ID = "minecraft:structure_block"
AIR_BLOCK_IDS = {"minecraft:air", "minecraft:cave_air", "minecraft:void_air"}
AIR_BLOCK_ID = "minecraft:air"
CAVE_AIR_BLOCK_ID = "minecraft:cave_air"
WATER_BLOCK_ID = "minecraft:water"
STONE_BRICKS_ID = "minecraft:stone_bricks"
CRACKED_STONE_BRICKS_ID = "minecraft:cracked_stone_bricks"
MOSSY_STONE_BRICKS_ID = "minecraft:mossy_stone_bricks"

app = typer.Typer()


def add_loot_tables(structure: Compound, loot_tables: dict[str, str]) -> None:
    for block in structure.get("blocks", []):
        nbt = block.get("nbt")
        if not isinstance(nbt, Compound):
            continue
        loot_table = loot_tables.get(str(nbt.get("id", "")))
        if loot_table is not None:
            nbt["LootTable"] = String(loot_table)
            nbt.pop("Items", None)


def remove_structure_blocks(structure: Compound) -> None:
    palette = structure.get("palette", [])
    structure["blocks"] = List[Compound](
        block
        for block in structure.get("blocks", [])
        if str(palette[int(block["state"])].get("Name", "")) != STRUCTURE_BLOCK_ID
    )


def palette_index(palette: List[Compound], block_id: str) -> int:
    for index, entry in enumerate(palette):
        if str(entry.get("Name", "")) == block_id:
            return index
    palette.append(Compound({"Name": String(block_id)}))
    return len(palette) - 1


def randomize_lab_bricks(structure: Compound) -> None:
    palette = structure["palette"]
    normal_indices = {index for index, entry in enumerate(palette) if str(entry.get("Name", "")) == STONE_BRICKS_ID}
    cracked_index = palette_index(palette, CRACKED_STONE_BRICKS_ID)
    mossy_index = palette_index(palette, MOSSY_STONE_BRICKS_ID)
    random = Random()
    for block in structure["blocks"]:
        if int(block["state"]) not in normal_indices:
            continue
        roll = random.random()
        if roll < 0.15:
            block["state"] = Int(cracked_index)
        elif roll < 0.45:
            block["state"] = Int(mossy_index)


def carve_lab_air(structure: Compound) -> None:
    palette = structure["palette"]
    cave_air_index = palette_index(palette, CAVE_AIR_BLOCK_ID)
    for block in structure["blocks"]:
        if str(palette[int(block["state"])].get("Name", "")) == AIR_BLOCK_ID:
            block["state"] = Int(cave_air_index)


def spawn_entity(structure: Compound, entity_id: str, x: float, y: float, z: float) -> None:
    entities = structure.get("entities")
    if not entities:
        entities = List[Compound]()
        structure["entities"] = entities
    entities.append(
        Compound(
            {
                "pos": List[Double]([Double(x), Double(y), Double(z)]),
                "blockPos": List[Int]([Int(int(x)), Int(int(y)), Int(int(z))]),
                "nbt": Compound(
                    {
                        "id": String(entity_id),
                        "PersistenceRequired": Byte(1),
                        "CanPickUpLoot": Byte(0),
                    }
                ),
            }
        )
    )


def block_states(structure: Compound) -> dict[tuple[int, int, int], str]:
    palette = structure["palette"]
    return {
        tuple(int(coordinate) for coordinate in block["pos"]): str(palette[int(block["state"])].get("Name", ""))
        for block in structure["blocks"]
    }


def spawn_random_entities(structure: Compound, entity_counts: dict[str, int], positions: list[tuple[float, float, float]]) -> None:
    count = sum(entity_counts.values())
    if len(positions) < count:
        raise ValueError(f"Structure has only {len(positions)} valid positions for {count} entities")

    entity_ids = (entity_id for entity_id, amount in entity_counts.items() for _ in range(amount))
    for (x, y, z), entity_id in zip(Random().sample(positions, count), entity_ids):
        spawn_entity(structure, entity_id, x, y, z)


def air_spawn_positions(states: dict[tuple[int, int, int], str]) -> list[tuple[float, float, float]]:
    return [
        (x + 0.5, float(y), z + 0.5)
        for (x, y, z), state in states.items()
        if state in AIR_BLOCK_IDS
        and states.get((x, y + 1, z)) in AIR_BLOCK_IDS
        and states.get((x, y - 1, z), "minecraft:air") not in AIR_BLOCK_IDS
        and states.get((x, y - 1, z)) != WATER_BLOCK_ID
    ]


def water_spawn_positions(states: dict[tuple[int, int, int], str]) -> list[tuple[float, float, float]]:
    return [
        (x + 0.5, float(y), z + 0.5)
        for (x, y, z), state in states.items()
        if state == WATER_BLOCK_ID
        and all(states.get((x + dx, y + dy, z + dz)) == WATER_BLOCK_ID for dx in (0, 1) for dy in (0, 1) for dz in (0, 1))
    ]


def transform_file(source_path: Path, dest_path: Path) -> None:
    structure = nbtlib.load(source_path)
    remove_structure_blocks(structure)
    if source_path.stem == "lab":
        randomize_lab_bricks(structure)
        carve_lab_air(structure)
    loot_tables = REMAINS_LOOT_TABLES if source_path.stem.startswith("remains_") else STRUCTURE_LOOT_TABLES.get(source_path.stem)
    if loot_tables is not None:
        add_loot_tables(structure, loot_tables)
    entity_spawns = RANDOM_ENTITY_SPAWNS.get(source_path.stem)
    if entity_spawns is not None:
        states = block_states(structure)
        spawn_random_entities(structure, entity_spawns.get("air", {}), air_spawn_positions(states))
        spawn_random_entities(structure, entity_spawns.get("water", {}), water_spawn_positions(states))
    dest_path.parent.mkdir(parents=True, exist_ok=True)
    structure.save(dest_path)


@app.command()
def run(
        source: Path = typer.Option(DEFAULT_SOURCE, "--source", "-s"),
        dest: Path = typer.Option(DEFAULT_DEST, "--dest", "-d"),
) -> None:
    if not source.exists():
        raise typer.BadParameter(f"Source directory does not exist: {source}")

    files = [path for path in source.rglob("*.nbt") if path.is_file()]
    if not files:
        typer.echo(f"No .nbt files found under {source}")
        return

    for source_path in files:
        transform_file(source_path, dest / source_path.relative_to(source))
    typer.echo(f"Processed {len(files)} structure NBT files.")


if __name__ == "__main__":
    app()
