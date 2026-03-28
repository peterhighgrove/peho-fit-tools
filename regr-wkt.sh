#!/usr/bin/env bash
set -uo pipefail

# Regression runner for fit-wkt-merge-analyze (non-interactive, conf-driven).
#
# Case layout (default root: ./regression/wkt):
#   skierg/ | treadmill/ | elliptical/
#     input.zip          required, primary FIT/ZIP (copied to run dir as aaa.zip)
#     extra.fit          required for skierg
#     extra.txt          required for treadmill / elliptical
#     master.fit         required unless --bless
#     master-laps.txt    required unless --bless
#     conf.txt           optional; deterministic conf generated if absent
#
# Usage:
#   ./regr-wkt.sh {ski|s|tread|t|ellipt|e|all} [--version 2.30] [--bless] [--debug]
#
# Migrate existing masters from Downloads:
#   mkdir -p regression/wkt/skierg regression/wkt/treadmill regression/wkt/elliptical
#
#   cp ~/Downloads/test-skierg.zip                regression/wkt/skierg/input.zip
#   cp ~/Downloads/test-skierg-extrafile.fit      regression/wkt/skierg/extra.fit
#   cp ~/Downloads/test-skierg-master.fit         regression/wkt/skierg/master.fit
#   cp ~/Downloads/test-skierg-master.txt         regression/wkt/skierg/master-laps.txt
#
#   cp ~/Downloads/test-treadmill.zip             regression/wkt/treadmill/input.zip
#   cp ~/Downloads/test-treadmill-extrafile.txt   regression/wkt/treadmill/extra.txt
#   cp ~/Downloads/test-treadmill-master.fit      regression/wkt/treadmill/master.fit
#   cp ~/Downloads/test-treadmill-master.txt      regression/wkt/treadmill/master-laps.txt
#
#   cp ~/Downloads/test-elliptical.zip            regression/wkt/elliptical/input.zip
#   cp ~/Downloads/test-elliptical-extrafile.txt  regression/wkt/elliptical/extra.txt
#   cp ~/Downloads/test-elliptical-master.fit     regression/wkt/elliptical/master.fit
#   cp ~/Downloads/test-elliptical-master.txt     regression/wkt/elliptical/master-laps.txt

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CASES_DIR="$ROOT_DIR/regression/wkt"
VERSION=""
JAR_PATH=""
BLESS=false
DEBUG=false
SPORT_ARG=""

# ---------------------------------------------------------------------------
debug() { [[ "$DEBUG" == true ]] && echo "[DEBUG] $*" >&2; }

print_usage() {
    cat <<'EOF'
Usage: ./regr-wkt.sh {ski|s|tread|t|ellipt|e|all} [options]

Sports:
  ski|s       Run skierg case
  tread|t     Run treadmill case
  ellipt|e    Run elliptical case
  all         Run all three cases

Options:
  --version <ver>   Use ./jars/fit-wkt-<ver>.jar  (also accepted as 2nd positional arg)
  --jar <path>      Use explicit jar path
  --bless           Create/update master.fit and master-laps.txt from current output
  --debug           Verbose output
  -h, --help
EOF
}

# ---------------------------------------------------------------------------
parse_args() {
    if [[ $# -eq 0 ]]; then
        SPORT_ARG="all"
        return
    fi

    # Allow --help/-h as first arg
    if [[ "$1" == "--help" || "$1" == "-h" ]]; then print_usage; exit 0; fi

    # Default to all when no explicit sport arg is provided.
    if [[ "$1" == --* ]]; then
        SPORT_ARG="all"
    else
        SPORT_ARG="$1"
        shift
    fi

    while [[ $# -gt 0 ]]; do
        case "$1" in
            --version) VERSION="${2:-}"; shift 2 ;;
            --jar)     JAR_PATH="${2:-}"; shift 2 ;;
            --bless)   BLESS=true; shift ;;
            --debug)   DEBUG=true; shift ;;
            -h|--help) print_usage; exit 0 ;;
            *)
                # Accept bare version number as second positional arg (backward compat)
                if [[ "$1" =~ ^[0-9]+\.[0-9]+ ]]; then
                    VERSION="$1"; shift
                else
                    echo "ERROR: unknown argument: $1" >&2; print_usage; exit 2
                fi
                ;;
        esac
    done
}

# ---------------------------------------------------------------------------
resolve_jar_path() {
    if [[ -n "$JAR_PATH" ]]; then
        [[ ! -f "$JAR_PATH" ]] && { echo "ERROR: --jar not found: $JAR_PATH" >&2; exit 2; }
        return
    fi
    if [[ -n "$VERSION" ]]; then
        JAR_PATH="$ROOT_DIR/jars/fit-wkt-${VERSION}.jar"
        [[ ! -f "$JAR_PATH" ]] && { echo "ERROR: jar not found: $JAR_PATH" >&2; exit 2; }
        return
    fi
    JAR_PATH="$(ls "$ROOT_DIR"/jars/fit-wkt-*.jar 2>/dev/null | sort -V | tail -n 1)"
    [[ -z "$JAR_PATH" || ! -f "$JAR_PATH" ]] && {
        echo "ERROR: could not auto-detect fit-wkt jar in $ROOT_DIR/jars" >&2
        echo "       pass --version <ver> or --jar <path>" >&2
        exit 2
    }
}

# ---------------------------------------------------------------------------
sport_to_case_dir() {
    case "$1" in
        ski|s)    echo "$CASES_DIR/skierg" ;;
        tread|t)  echo "$CASES_DIR/treadmill" ;;
        ellipt|e) echo "$CASES_DIR/elliptical" ;;
        *) echo "ERROR: unknown sport: $1" >&2; return 1 ;;
    esac
}

# ---------------------------------------------------------------------------
validate_case() {
    local case_dir="$1"
    local extra
    extra="$(find "$case_dir" -maxdepth 1 -type f \( -name 'extra.fit' -o -name 'extra.txt' \) | head -n 1)"

    [[ ! -f "$case_dir/input.zip" ]] && { echo "missing input.zip"; return 1; }
    [[ -z "$extra" ]]                && { echo "missing extra.fit or extra.txt"; return 1; }
    if [[ "$BLESS" != "true" ]]; then
        [[ ! -f "$case_dir/master.fit" ]]      && { echo "missing master.fit"; return 1; }
        [[ ! -f "$case_dir/master-laps.txt" ]] && { echo "missing master-laps.txt"; return 1; }
    fi
    return 0
}

# ---------------------------------------------------------------------------
run_case() {
    local case_dir="$1"
    local case_name
    case_name="$(basename "$case_dir")"

    # ------------------------------------------------------------------
    # Set up isolated run directory
    # ------------------------------------------------------------------
    local run_dir="$case_dir/.run"
    rm -rf "$run_dir"
    mkdir -p "$run_dir"

    cp "$case_dir/input.zip" "$run_dir/aaa.zip"

    local extra
    extra="$(find "$case_dir" -maxdepth 1 -type f \( -name 'extra.fit' -o -name 'extra.txt' \) | head -n 1)"
    local extra_ext="${extra##*.}"
    cp "$extra" "$run_dir/bbb.${extra_ext}"

    # ------------------------------------------------------------------
    # Determine minutes (used both for conf and filename lookup)
    # ------------------------------------------------------------------
    local minutes="2"
    if [[ -f "$case_dir/conf.txt" ]]; then
        local conf_min
        conf_min="$(grep '^timeOffsetMin ' "$case_dir/conf.txt" | awk '{print $2}' || true)"
        [[ -n "$conf_min" ]] && minutes="$conf_min"
    fi

    # ------------------------------------------------------------------
    # Create or copy conf.txt into run dir
    # ------------------------------------------------------------------
    if [[ -f "$case_dir/conf.txt" ]]; then
        cp "$case_dir/conf.txt" "$run_dir/conf.txt"
    else
        cat > "$run_dir/conf.txt" <<EOF
filePathPrefix ${run_dir}/
inputFilePath ${run_dir}/aaa.zip
profileNameSuffix Friskis Orminge
timeOffsetMin ${minutes}
extraFilename ${run_dir}/bbb.${extra_ext}
EOF
    fi

    # ------------------------------------------------------------------
    # Run the jar, capture output and parse generated filenames
    # ------------------------------------------------------------------
    local log_file="$run_dir/run.log"
    echo "[RUN ] $case_name"

    local Sanitized1="" Sanitized2="" Sanitized3=""
    local Sanitized1_count=0 Sanitized2_count=0 Sanitized3_count=0
    local filenamebase="" orgdatetime="" datetime=""

    while IFS= read -r line; do
        debug "$line"
        echo "$line" >> "$log_file"

        if [[ "$line" =~ Sanitized:\'([^\']+)\' ]]; then
            ((Sanitized1_count++))
            [[ $Sanitized1_count -eq 1 ]] && Sanitized1="${BASH_REMATCH[1]}"
        fi
        if [[ "$line" =~ Sanitized:\'([^\']+)\' ]]; then
            ((Sanitized2_count++))
            [[ $Sanitized2_count -eq 2 ]] && Sanitized2="${BASH_REMATCH[1]}"
        fi
        if [[ "$line" =~ Sanitized:\'([^\']+)\' ]]; then
            ((Sanitized3_count++))
            [[ $Sanitized3_count -eq 3 ]] && Sanitized3="${BASH_REMATCH[1]}"
        fi
        [[ "$line" =~ FilenameBase:\ ([^\']+) ]]                 && filenamebase="${BASH_REMATCH[1]}"
        [[ "$line" =~ Org\ activity\ dateTime\ Local:([^\']+) ]] && orgdatetime="${BASH_REMATCH[1]}"
        [[ "$line" =~ New\ activity\ dateTime\ Local:([^\']+) ]] && datetime="${BASH_REMATCH[1]}"

    done < <(cd "$run_dir" && java -jar "$JAR_PATH" 2>&1)

    debug "== Sanitized1:   ${Sanitized1}"
    debug "== Sanitized2:   ${Sanitized2}"
    debug "== Sanitized3:   ${Sanitized3}"
    debug "== filenamebase: ${filenamebase}"
    debug "== orgdatetime:  ${orgdatetime}"
    debug "== datetime:     ${datetime}"

    # ------------------------------------------------------------------
    # Locate generated output files (search in run_dir to avoid path prefix issues)
    # ------------------------------------------------------------------
    local new_fit new_laps
    new_fit="$(find "$run_dir" -maxdepth 1 -type f -name '*-merged*min.fit' | sort | tail -n 1)"
    new_laps="$(find "$run_dir" -maxdepth 1 -type f -name '*-merged*min-laps.txt' | sort | tail -n 1)"

    debug "== new_fit:  ${new_fit}"
    debug "== new_laps: ${new_laps}"

    if [[ -z "$new_fit" || ! -f "$new_fit" ]]; then
        echo "[FAIL] $case_name (no merged output fit found)"
        echo "       run log:  $log_file"
        return 1
    fi
    if [[ -z "$new_laps" || ! -f "$new_laps" ]]; then
        echo "[FAIL] $case_name (no laps txt found)"
        echo "       run log:  $log_file"
        return 1
    fi

    # ------------------------------------------------------------------
    # Bless or compare
    # ------------------------------------------------------------------
    if [[ "$BLESS" == "true" ]]; then
        cp -f "$new_fit"  "$case_dir/master.fit"
        cp -f "$new_laps" "$case_dir/master-laps.txt"
        echo "[PASS] $case_name (blessed master.fit + master-laps.txt)"
        return 0
    fi

    local case_ok=true

    if ! cmp -s "$new_fit" "$case_dir/master.fit"; then
        case_ok=false
        echo "[FAIL] $case_name (fit differs)"
        if command -v sha256sum >/dev/null 2>&1; then
            sha256sum "$new_fit" "$case_dir/master.fit" | sed 's/^/         /'
        fi
    fi

    if ! cmp -s "$new_laps" "$case_dir/master-laps.txt"; then
        case_ok=false
        echo "[FAIL] $case_name (laps.txt differs)"
        echo "       --- diff (first 30 lines) ---"
        diff -y --suppress-common-lines "$new_laps" "$case_dir/master-laps.txt" \
            | head -n 30 | sed 's/^/         /'
    fi

    if [[ "$case_ok" == "true" ]]; then
        echo "[PASS] $case_name"
        return 0
    fi

    echo "       run log: $log_file"
    return 1
}

# ---------------------------------------------------------------------------
main() {
    parse_args "$@"
    resolve_jar_path

    local sports=()
    case "$SPORT_ARG" in
        ski|s)    sports=("ski") ;;
        tread|t)  sports=("tread") ;;
        ellipt|e) sports=("ellipt") ;;
        all)      sports=("ski" "tread" "ellipt") ;;
        *)
            echo "ERROR: sport must be ski|s, tread|t, ellipt|e, or all" >&2
            print_usage; exit 2
            ;;
    esac

    echo "============================================"
    echo "WKT Regression"
    echo "Jar      : $JAR_PATH"
    echo "Cases dir: $CASES_DIR"
    echo "Bless    : $BLESS"
    echo "Sports   : ${sports[*]}"
    echo "============================================"

    local pass_count=0 fail_count=0 invalid_count=0

    for sport in "${sports[@]}"; do
        local case_dir
        case_dir="$(sport_to_case_dir "$sport")"

        if [[ ! -d "$case_dir" ]]; then
            echo "[SKIP] $(basename "$case_dir") (case dir not found: $case_dir)"
            invalid_count=$((invalid_count + 1))
            continue
        fi

        local invalid_reason=""
        if ! invalid_reason="$(validate_case "$case_dir" 2>&1)"; then
            echo "[SKIP] $(basename "$case_dir") (invalid: ${invalid_reason})"
            invalid_count=$((invalid_count + 1))
            continue
        fi

        if run_case "$case_dir"; then
            pass_count=$((pass_count + 1))
        else
            fail_count=$((fail_count + 1))
        fi
    done

    echo ""
    echo "================ Summary ================"
    echo "PASS   : $pass_count"
    echo "FAIL   : $fail_count"
    echo "INVALID: $invalid_count"
    echo "========================================="

    [[ $fail_count -gt 0 || $invalid_count -gt 0 ]] && exit 1
    exit 0
}

main "$@"
