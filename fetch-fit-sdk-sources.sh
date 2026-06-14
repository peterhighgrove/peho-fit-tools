#!/usr/bin/env bash
set -euo pipefail

# Fetch and unpack Garmin FIT SDK sources from Maven Central.
# Defaults:
#   - Version from root pom.xml property <fit.sdk.version>
#   - Output directory: ./libs/fit-sdk-sources/<version>
#
# Usage:
#   ./fetch-fit-sdk-sources.sh
#   ./fetch-fit-sdk-sources.sh 21.205.0
#   ./fetch-fit-sdk-sources.sh 21.205.0 ./libs/fit-sdk-sources-custom

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
POM_FILE="$ROOT_DIR/pom.xml"

if [[ ! -f "$POM_FILE" ]]; then
    echo "Error: root pom.xml not found at $POM_FILE" >&2
    exit 1
fi

extract_version_from_pom() {
    sed -n 's:.*<fit.sdk.version>\(.*\)</fit.sdk.version>.*:\1:p' "$POM_FILE" | head -n 1
}

VERSION="${1:-}"
if [[ -z "$VERSION" ]]; then
    VERSION="$(extract_version_from_pom)"
fi

if [[ -z "$VERSION" ]]; then
    echo "Error: could not resolve FIT SDK version." >&2
    echo "Set <fit.sdk.version> in pom.xml or pass version as arg 1." >&2
    exit 1
fi

OUT_DIR="${2:-$ROOT_DIR/libs/fit-sdk-sources/$VERSION}"

echo "==> FIT SDK version: $VERSION"
echo "==> Output directory: $OUT_DIR"

cd "$ROOT_DIR"

# Force metadata check with -U and fetch both binary and sources jars.
./mvnw -U -q dependency:get -Dartifact=com.garmin:fit:${VERSION}
./mvnw -U -q dependency:get -Dartifact=com.garmin:fit:${VERSION}:jar:sources

SRC_JAR="$HOME/.m2/repository/com/garmin/fit/${VERSION}/fit-${VERSION}-sources.jar"
if [[ ! -f "$SRC_JAR" ]]; then
    echo "Error: sources jar not found at $SRC_JAR" >&2
    exit 1
fi

mkdir -p "$OUT_DIR"
(
    cd "$OUT_DIR"
    jar xf "$SRC_JAR"
)

echo "==> Extracted: $SRC_JAR"
echo "==> Done: $OUT_DIR"
