#!/bin/bash

set -euo pipefail

usage() {
  echo "Usage: $0 [major|minor|patch]" >&2
  exit 1
}

bump_type="${1:-}" || true
if [[ "$bump_type" != "major" && "$bump_type" != "minor" && "$bump_type" != "patch" ]]; then
  usage
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="${SCRIPT_DIR}/.."
BUILD_FILE="${PROJECT_DIR}/app/build.gradle"

if [[ ! -f "$BUILD_FILE" ]]; then
  echo "build.gradle not found at $BUILD_FILE" >&2
  exit 1
fi

current_line="$(grep "^version\\s*=\\s*'" "$BUILD_FILE" || true)"
if [[ -z "$current_line" ]]; then
  echo "Could not find version line in $BUILD_FILE" >&2
  exit 1
fi

current_version="$(echo "$current_line" | sed "s/.*'\(.*\)'.*/\1/")"

snapshot_suffix=""
base_version="$current_version"
if [[ "$current_version" == *"-SNAPSHOT" ]]; then
  snapshot_suffix="-SNAPSHOT"
  base_version="${current_version%-SNAPSHOT}"
fi

IFS='.' read -r major minor patch <<< "$base_version"

case "$bump_type" in
  major)
    major=$((major + 1))
    minor=0
    patch=0
    ;;
  minor)
    minor=$((minor + 1))
    patch=0
    ;;
  patch)
    patch=$((patch + 1))
    ;;
esac

new_version="${major}.${minor}.${patch}${snapshot_suffix}"

tmp_file="${BUILD_FILE}.tmp.$$"
escaped_current="$(printf "%s\n" "$current_version" | sed -e 's/[&/]/\\&/g')"
escaped_new="$(printf "%s\n" "$new_version" | sed -e 's/[&/]/\\&/g')"

sed "s/^version = '${escaped_current}'/version = '${escaped_new}'/" "$BUILD_FILE" > "$tmp_file"
mv "$tmp_file" "$BUILD_FILE"

echo "Version updated: ${current_version} -> ${new_version}"
