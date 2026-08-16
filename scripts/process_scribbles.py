import hashlib
import random
from pathlib import Path

from PIL import Image
import typer

DEFAULT_SOURCE = Path("scripts/scribblesRaw")
DEFAULT_DESTINATION = Path("common/src/main/resources/assets/immersive_pillagers/textures/gui/scribbles")
DISTORTION_LAYERS = ((32, 1.5), (24, 1.2), (16, 0.9), (8, 0.6))
VARIANT_COUNT = 3
PROCESSED_ALPHA = 0.75
DARK_BROWN = (70, 45, 29)

app = typer.Typer()


def clamp(value: float, maximum: int) -> float:
    return max(0.0, min(value, maximum - 1.0))


def distort(image: Image.Image, cell_size: int, displacement: float, randomizer: random.Random) -> Image.Image:
    width, height = image.size
    mesh = []
    for top in range(0, height, cell_size):
        for left in range(0, width, cell_size):
            right = min(left + cell_size, width)
            bottom = min(top + cell_size, height)
            source_quad = tuple(
                coordinate
                for x, y in ((left, top), (left, bottom), (right, bottom), (right, top))
                for coordinate in (clamp(x + randomizer.uniform(-displacement, displacement), width), clamp(y + randomizer.uniform(-displacement, displacement), height))
            )
            mesh.append(((left, top, right, bottom), source_quad))
    return image.transform(image.size, Image.Transform.MESH, mesh, Image.Resampling.NEAREST)


def seed_for(source: Path, variant: int) -> int:
    digest = hashlib.sha256(source.read_bytes() + variant.to_bytes(1)).digest()
    return int.from_bytes(digest[:8])


def process(source: Path, destination: Path) -> None:
    image = Image.open(source).convert("RGBA")
    variants = []
    for variant in range(VARIANT_COUNT):
        distorted = image
        randomizer = random.Random(seed_for(source, variant))
        for cell_size, displacement in DISTORTION_LAYERS:
            distorted = distort(distorted, cell_size, displacement, randomizer)
        variants.append(distorted)

    averaged = Image.blend(Image.blend(variants[0], variants[1], 0.5), variants[2], 1.0 / 3.0)
    alpha = averaged.getchannel("A").point(lambda value: round(value * PROCESSED_ALPHA))
    averaged = Image.new("RGBA", averaged.size, (*DARK_BROWN, 0))
    averaged.putalpha(alpha)
    destination.parent.mkdir(parents=True, exist_ok=True)
    averaged.save(destination)


@app.command()
def run(
        source: Path = typer.Option(DEFAULT_SOURCE, "--source", "-s"),
        dest: Path = typer.Option(DEFAULT_DESTINATION, "--dest", "-d"),
) -> None:
    files = sorted(path for path in source.rglob("*.png") if path.is_file())
    if not files:
        raise typer.BadParameter(f"No PNG scribbles found under {source}")
    for source_path in files:
        process(source_path, dest / source_path.relative_to(source))
    print(f"Processed {len(files)} scribbles.")


if __name__ == "__main__":
    app()
