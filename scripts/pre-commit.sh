#!/bin/bash

set -euo pipefail

usage() {
  echo "Usage: $0 [major|minor|patch|none]" >&2
  echo "  major  -> BREAKING CHANGE (MAJOR)" >&2
  echo "  minor  -> feat: (MINOR)" >&2
  echo "  patch  -> fix: (PATCH)" >&2
  echo "  none   -> sem alterar versao (docs, refactor, test, etc.)" >&2
  exit 1
}

bump_type="${1:-none}" || true

case "$bump_type" in
  major|minor|patch|none)
    ;;
  *)
    usage
    ;;
esac

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="${SCRIPT_DIR}/.."

cd "$PROJECT_DIR"

if [[ "$bump_type" != "none" ]]; then
  echo "[pre-commit] Bumping version ($bump_type)..."
  "$SCRIPT_DIR/version-bump.sh" "$bump_type"
  echo "[pre-commit] Version bump done."
else
  echo "[pre-commit] Skipping version bump (none)."
fi

echo "[pre-commit] Running Sonar + clean build via sonar-local.sh..."
"$SCRIPT_DIR/sonar-local.sh"
echo "[pre-commit] Sonar + build finished successfully."
