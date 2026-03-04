#!/bin/bash
set -e
# ---- Validate argument count ----
if [[ $# -lt 1 || $# -gt 1 ]]; then
    echo "Usage: $0 <version>"
    exit 1
fi

# -------------------------------
# 0. Set project root and destination folder
# -------------------------------

root=$(pwd)
dest="$root/jars"
dest2="$HOME/Downloads"
dest3="$HOME/Nextcloud/dev/jars"

# -------------------------------
# 1. Run Maven commands
# -------------------------------
echo "=== Setting new version to $1 ==="
mvn versions:set -DnewVersion=$1

echo "=== Committing version changes ==="
mvn versions:commit

echo "=== Clean and package all modules ==="
mvn clean package

# -------------------------------
# 2. Ensure destination folder exists
# -------------------------------
mkdir -p "$dest"
mkdir -p "$dest2"
mkdir -p "$dest3"

# -------------------------------
# 3. Define rename map
# -------------------------------
declare -A renameMap
renameMap=(
    ["fit-fix-gps-activities"]="fit-fix.jar"
    ["fit-wkt-merge-analyze"]="fit-wkt.jar"
    ["fit-zip-renamer"]="fit-ren.jar"
    ["garmin-wkt-fit-merge-analyze"]="garmin-wkt.jar"
)

# -------------------------------
# 4. Collect and rename JARs
# -------------------------------
# Find all JARs under target directories, skip original-*.jar and fit-core*.jar
find "$root" -type f -path "*/target/*.jar" \
    ! -name "original-*.jar" ! -name "fit-core*.jar" | while read -r jar; do

    filename=$(basename "$jar")
    dirname=$(dirname "$jar")

    # Skip if it's already in the destination folder
    if [[ "$dirname" == "$dest" ]]; then
        continue
    fi

    # Extract artifactId and version
    artifactId=$(echo "$filename" | sed -E 's/-[0-9]+(\.[0-9]+)*(\.jar)?$//')
    versionPart=$(echo "$filename" | sed -E 's/.*-([0-9]+(\.[0-9]+)*)\.jar$/\1/')

    if [[ -n "${renameMap[$artifactId]}" ]]; then
        newName=$(echo "${renameMap[$artifactId]}" | sed "s/\.jar$/-$versionPart.jar/")
    else
        newName="$filename"
    fi

    cp -f "$jar" "$dest/$newName"
    cp -f "$jar" "$dest2/$newName"
    cp -f "$jar" "$dest3/$newName"
    echo "Copied $jar -> $newName"

done

echo "-- All JARs collected and renamed in $dest"
