# Set project root and destination folder
$root = Get-Location
$dest = Join-Path $root "jars"

# -------------------------------
# 1. Run Maven commands
# -------------------------------
Write-Host "=== Setting new version to 2.3 ==="
mvn versions:set "-DnewVersion=2.3"

Write-Host "=== Committing version changes ==="
mvn versions:commit

Write-Host "=== Clean and package all modules ==="
mvn clean package

# -------------------------------
# 2. Ensure destination folder exists
# -------------------------------
New-Item -ItemType Directory -Force -Path $dest | Out-Null

# -------------------------------
# 3. Define rename map
# -------------------------------
$renameMap = @{
    "fit-fix-gps-activities" = "fit-fix.jar"
    "fit-wkt-merge-analyze" = "fit-wkt.jar"
    "fit-zip-renamer" = "fit-ren.jar"
    "garmin-wkt-fit-merge-analyze" = "garmin-wkt.jar"
}

# -------------------------------
# 4. Collect and rename JARs
# -------------------------------
Get-ChildItem -Path $root -Recurse -Filter "*.jar" |
    Where-Object {
        $_.FullName -match "\\target\\" `
        -and ($_.DirectoryName -ne $dest) `
        -and ($_.Name -notlike "original-*.jar") `
        -and ($_.BaseName -notlike "fit-core*")  # skip fit-core
    } |
    ForEach-Object {
        $artifactId = $_.BaseName -replace "-\d+(\.\d+)*$",""
        $versionPart = $_.BaseName -replace ".*?-",""

        if ($renameMap.ContainsKey($artifactId)) {
            # Insert version before .jar
            $newName = $renameMap[$artifactId] -replace "\.jar$", "-$versionPart.jar"
        } else {
            $newName = $_.Name
        }

        # Copy and overwrite only if same name exists
        Copy-Item $_.FullName -Destination (Join-Path $dest $newName) -Force

        Write-Host "Copied $($_.FullName) -> $newName"
    }

Write-Host "-- All JARs collected and renamed in $dest"