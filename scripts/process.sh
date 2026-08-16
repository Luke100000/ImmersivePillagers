#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
uv run --with pillow --with typer python "$script_dir/process_scribbles.py"
uv run --with nbtlib --with typer python "$script_dir/process_structures.py" "$@"
