#!/bin/bash
#set -x
#set -euo pipefail

#!/bin/bash

# ---- Defaults ----
DEBUG=false

# ---- Validate argument count ----
if [[ $# -lt 2 || $# -gt 3 ]]; then
    echo "Usage: $0 {ski|s|tread|t|ellipt|e} <version> [--debug]"
    exit 1
fi

# ---- Required arguments ----
inputSport="$1"
version="$2"

# ---- Validate sport ----
case "$inputSport" in
    ski|s)
        sport="skierg"
        extrafileextension="fit"
        ;;
    tread|t)
        sport="treadmill"
        extrafileextension="txt"
        ;;
    ellipt|e)
        sport="elliptical"
        extrafileextension="txt"
        ;;
    *)
        echo "Error: sport must be ski|s, tread|t, or ellipt|e"
        exit 1
        ;;
esac

# ---- Optional debug flag ----
if [[ "$3" == "--debug" ]]; then
    DEBUG=true
fi

# ---- Debug function ----
debug() {
    [[ "$DEBUG" == true ]] && echo "[DEBUG] $*" >&2
}

# ---- Example usage ----
echo "Sport: $sport"
echo "Version: $version"
debug "Debug mode enabled"

folder="$HOME/Downloads"
debug $folder

# Save current dir and move (only if exists)
if [ -d "$folder" ]; then
    pushd "$folder" > /dev/null
    debug "Now in: $(pwd)"

    j1="fit-wkt-${version}.jar"
    c1="conf.txt"

    minutes="2"
    profileNameSuffix="Friskis Orminge"

    testFile="test-${sport}.zip"
    testExtraFile="test-${sport}-extrafile.${extrafileextension}"

    masterFitFileToCompareWith="test-${sport}-master.fit"
    masterTextFileToCompareWith="test-${sport}-master.txt"

    if [ -f "$j1" ]; then
        debug "$j1 exists"
    else
        echo "$j1 does not exist"
        exit
    fi

    if [ -f "$testFile" ]; then
        debug "TestFile exists"
    else
        echo "TestFile does not exist"
        echo "$testFile"
        exit
    fi

    if [ -f "$testExtraFile" ]; then
        debug "TestExtraFile exists"
    else
        echo "TestExtraFile does not exist"
        echo "$testExtraFile"
        exit
    fi

    if [ -f "$masterFitFileToCompareWith" ]; then
        debug "MasterFitFile exists"
    else
        echo "MasterFitFile does not exist"
        echo "$masterFitFileToCompareWith"
        exit
    fi

    if [ -f "$masterTextFileToCompareWith" ]; then
        debug "MasterTextFile exists"
    else
        echo "MasterTextFile does not exist"
        echo "$masterTextFileToCompareWith"
        exit
    fi

    if [ -f "$c1" ]; then
        debug "$c1 exists"
        noConfFile=false

        debug cp "$c1" "${c1}-backup.txt"
        cp "$c1" "${c1}-backup.txt"
    else
        echo "$c1 does not exist"
        noConfFile=true
    fi

    debug cp "$testExtraFile" "${testExtraFile}-backup.t${extrafileextension}"
    cp "$testExtraFile" "${testExtraFile}-backup.${extrafileextension}"

    cat > "$c1" <<EOF
filePathPrefix ${folder}/
inputFilePath ${testFile}
profileNameSuffix ${profileNameSuffix}
timeOffsetMin ${minutes}
extraFilename ${testExtraFile}
EOF
    
    #read -n 1 -s -r -p "1.Press any key to continue..."
    debug

    debug ---------------------------------------------
    debug ---------------------------------------------
    debug ---------------------------------------------

    while IFS= read -r line; do
        debug "$line"   # always print
        if [[ "$line" =~ Sanitized:\'([^\']+)\' ]]; then
            ((Sanitized1_count++))
            if [[ $Sanitized1_count -eq 1 ]]; then   # Pick secon occurance
                Sanitized1="${BASH_REMATCH[1]}"
            fi
        fi
        if [[ "$line" =~ Sanitized:\'([^\']+)\' ]]; then
            ((Sanitized2_count++))
            if [[ $Sanitized2_count -eq 2 ]]; then   # Pick secon occurance
                Sanitized2="${BASH_REMATCH[1]}"
            fi
        fi
        if [[ "$line" =~ Sanitized:\'([^\']+)\' ]]; then
            ((Sanitized3_count++))
            if [[ $Sanitized3_count -eq 3 ]]; then   # Pick secon occurance
                Sanitized3="${BASH_REMATCH[1]}"
            fi
        fi
        if [[ "$line" =~ FilenameBase:\ ([^\']+) ]]; then
            filenamebase="${BASH_REMATCH[1]}"   # overwrite each match
        fi
        if [[ "$line" =~ Org\ activity\ dateTime\ Local:([^\']+) ]]; then
            orgdatetime="${BASH_REMATCH[1]}"   # overwrite each match
        fi
        if [[ "$line" =~ New\ activity\ dateTime\ Local:([^\']+) ]]; then
            datetime="${BASH_REMATCH[1]}"   # overwrite each match
        fi

    done < <(java -jar "$j1" 2>&1)

    debug ---------------------------------------------
    debug ---------------------------------------------
    debug ---------------------------------------------
    debug "== Found Sanitized1: ${Sanitized1}"
    debug "== Found Sanitized2: ${Sanitized2}"
    debug "== Found Sanitized3: ${Sanitized3}"
    debug "== Found filenamebase: ${filenamebase}"
    debug "== Found orgdatetime: ${orgdatetime}"
    debug "== Found newdatetime: ${datetime}"

    if [[ "$Sanitized2" ]]; then
        newCreatedFitFile="$Sanitized2-merged${minutes}min.fit"
        newCreatedOrgFitFile="$Sanitized1-org.fit"
        newCreatedOrgC2FitFile="$Sanitized3-org.fit"
        newCreatedManualLapsFile="$Sanitized1-manualLaps.txt"
    else
        newCreatedFitFile="${datetime}${filenamebase}-mergedJava${minutes}min.fit"
        newCreatedOrgFitFile="${orgdatetime}${filenamebase}-watch.fit"
        newCreatedOrgC2FitFile="${orgdatetime}${filenamebase}-c2.fit"
        newCreatedManualLapsFile="${orgdatetime}${filenamebase}-manualLaps.txt"
    fi
    debug "== newCreatedFitFile: ${newCreatedFitFile}"
    debug "== newCreatedOrgFitFile: ${newCreatedOrgFitFile}"
    debug "== newCreatedOrgC2FitFile: ${newCreatedOrgC2FitFile}"
    debug "== newCreatedManualLapsFile: ${newCreatedManualLapsFile}"
    
    if [ -f "$newCreatedFitFile" ]; then
        debug "NewCreatedFitFile exists"
    else
        echo "NewCreatedFitFile does not exist"
        echo "$newCreatedFitFile"
    fi

    echo ""
    debug "==== TESTING FIT FILES ====="
    if cmp -s "$newCreatedFitFile" "$masterFitFileToCompareWith"; then
        echo "====> FIT Files are identical"
    else
        echo "====> FIT Files differ"
    fi
    debug "==== DONE ====="
    echo ""
    #====================================================

    if [[ "$Sanitized2" ]]; then
        newCreatedTextFile="${Sanitized2}-merged${minutes}min-laps.txt"
    else
        newCreatedTextFile="${datetime}${filenamebase}-mergedJava${minutes}min-laps.txt"
    fi
    debug "== newCreatedTextFile: ${newCreatedTextFile}"
    
    if [ -f "$newCreatedTextFile" ]; then
        debug "newCreatedTextFile exists"
    else
        echo "newCreatedTextFile does not exist"
        echo "$newCreatedTextFile"
    fi

    echo ""
    echo "==== TESTING TEXT FILES ====="
    diff -y --suppress-common-lines "$newCreatedTextFile" "$masterTextFileToCompareWith"
    echo "==== TESTING TEXT FILES DONE ====="
    echo ""

    debug cp "${testExtraFile}-backup.${extrafileextension}" "$testExtraFile"
    cp "${testExtraFile}-backup.${extrafileextension}" "$testExtraFile"

    #read -n 1 -s -r -p "2.Press any key to continue..."
    debug

    if [ -f "${c1}-backup.txt" ]; then
        debug cp "${c1}-backup.txt" "$c1"
        cp "${c1}-backup.txt" "$c1"
        debug rm "${c1}-backup.txt"
        rm "${c1}-backup.txt"
    fi
    if  [ "$noConfFile" = true ] && [ -f "$c1" ]; then
        debug rm "$c1"
        rm "$c1"
    fi
    if [ -f "${testExtraFile}-backup.${extrafileextension}" ]; then
        debug rm "${testExtraFile}-backup.${extrafileextension}"
        rm "${testExtraFile}-backup.${extrafileextension}"
    fi
    if [ -f "${newCreatedFitFile}" ]; then
        debug rm "${newCreatedFitFile}"
        rm "${newCreatedFitFile}"
    fi
    if [ -f "${newCreatedTextFile}" ]; then
        debug rm "${newCreatedTextFile}"
        rm "${newCreatedTextFile}"
    fi

    if [ -f "${newCreatedOrgFitFile}" ]; then
        debug rm "${newCreatedOrgFitFile}"
        rm "${newCreatedOrgFitFile}"
    fi
    if [ -f "${newCreatedOrgFitFile}-backup" ]; then
        debug rm "${newCreatedOrgFitFile}-backup"
        rm "${newCreatedOrgFitFile}-backup"
    fi

    if [ -f "${newCreatedOrgC2FitFile}" ]; then
        debug rm "${newCreatedOrgC2FitFile}"
        rm "${newCreatedOrgC2FitFile}"
    fi
    if [ -f "${newCreatedOrgC2FitFile}-backup" ]; then
        debug rm "${newCreatedOrgC2FitFile}-backup"
        rm "${newCreatedOrgC2FitFile}-backup"
    fi

    if [ -f "${newCreatedManualLapsFile}" ]; then
        debug rm "${newCreatedManualLapsFile}"
        rm "${newCreatedManualLapsFile}"
    fi
    if [ -f "${newCreatedManualLapsFile}-backup" ]; then
        debug rm "${newCreatedManualLapsFile}-backup"
        rm "${newCreatedManualLapsFile}-backup"
    fi

    popd > /dev/null
    debug "Back in: $(pwd)"
else
    echo "Directory does not exist: $folder"
fi