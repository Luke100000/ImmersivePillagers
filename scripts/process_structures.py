from pathlib import Path
from random import Random

import nbtlib
import typer
from nbtlib import Byte, Compound, Double, Int, List, String

DEFAULT_SOURCE = Path("forge/run/saves/Structures/generated/immersive_pillagers/structures")
DEFAULT_DEST = Path("common/src/main/resources/data/immersive_pillagers/structures")

LOOT_TABLES = {
    "minecraft:chest": "immersive_pillagers:chests/camp_chest",
    "minecraft:barrel": "immersive_pillagers:chests/camp_barrel",
    "immersive_pillagers:reinforced_chest": "immersive_pillagers:chests/camp_reinforced",
}

RANDOM_ENTITY_SPAWNS = {
    "camp": {
        "minecraft:pillager": 5,
        "minecraft:vindicator": 3,
        "minecraft:evoker": 1,
    }
}

STRUCTURE_BLOCK_ID = "minecraft:structure_block"
AIR_BLOCK_IDS = {"minecraft:air", "minecraft:cave_air", "minecraft:void_air"}

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


def spawn_entity(structure: Compound, entity_id: str, x: float, y: float, z: float) -> None:
    entities = structure.setdefault("entities", List[Compound]())
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


def spawn_random_entities(structure: Compound, entity_counts: dict[str, int]) -> None:
    palette = structure["palette"]
    states = {
        tuple(int(coordinate) for coordinate in block["pos"]): str(palette[int(block["state"])].get("Name", ""))
        for block in structure["blocks"]
    }
    positions = [
        (x + 0.5, float(y), z + 0.5)
        for (x, y, z), state in states.items()
        if state in AIR_BLOCK_IDS
        and states.get((x, y + 1, z)) in AIR_BLOCK_IDS
        and states.get((x, y - 1, z)) not in AIR_BLOCK_IDS
    ]
    count = sum(entity_counts.values())
    if len(positions) < count:
        raise ValueError(f"Structure has only {len(positions)} valid air positions for {count} entities")

    for (x, y, z), entity_id in zip(Random().sample(positions, count), (entity_id for entity_id, amount in entity_counts.items() for _ in range(amount))):
        spawn_entity(structure, entity_id, x, y, z)


def transform_file(source_path: Path, dest_path: Path) -> None:
    structure = nbtlib.load(source_path)
    remove_structure_blocks(structure)
    add_loot_tables(structure, LOOT_TABLES)
    entity_counts = RANDOM_ENTITY_SPAWNS.get(source_path.stem)
    if entity_counts is not None:
        spawn_random_entities(structure, entity_counts)
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
