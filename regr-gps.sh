#!/usr/bin/env bash
set -uo pipefail

# Regression runner for fit-fix-gps-activities (interactive menu via scripted stdin).
#
# Case layout (default root: ./regression/gps):
#   <case>/
#     input.fit | input.zip      # required, source file copied to run dir as aaa.<ext>
#     master.fit                 # required, expected output for binary compare
#     inputs.txt                 # required, scripted console input (one entry per line)
#     conf.txt                   # optional, if missing a deterministic conf is generated
#
# Usage:
#   ./regr-gps.sh [--version 2.30] [--jar /abs/path/fit-fix-2.30.jar] [--cases-dir ./regression/gps]

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CASES_DIR="$ROOT_DIR/regression/gps"
VERSION=""
JAR_PATH=""
BLESS=false

print_usage() {
    cat <<'EOF'
Usage: ./regr-gps.sh [options]

Options:
    --version <ver>      Use jar at ./jars/fit-fix-<ver>.jar
    --jar <path>         Use explicit jar path
    --cases-dir <path>   Regression cases directory (default: ./regression/gps)
    --bless              Create/update master.fit from current output
  -h, --help           Show this help

Case requirements per folder:
  input.fit OR input.zip
    master.fit (required unless --bless)
  inputs.txt
  conf.txt (optional)
EOF
}

resolve_jar_path() {
    if [[ -n "$JAR_PATH" ]]; then
        if [[ ! -f "$JAR_PATH" ]]; then
            echo "ERROR: --jar path not found: $JAR_PATH" >&2
            exit 2
        fi
        return
    fi

    if [[ -n "$VERSION" ]]; then
        JAR_PATH="$ROOT_DIR/jars/fit-fix-${VERSION}.jar"
        if [[ ! -f "$JAR_PATH" ]]; then
            echo "ERROR: jar not found for version ${VERSION}: $JAR_PATH" >&2
            exit 2
        fi
        return
    fi

    JAR_PATH="$(ls "$ROOT_DIR"/jars/fit-fix-*.jar 2>/dev/null | sort -V | tail -n 1)"
    if [[ -z "$JAR_PATH" || ! -f "$JAR_PATH" ]]; then
        echo "ERROR: could not auto-detect fit-fix jar in $ROOT_DIR/jars" >&2
        echo "       pass --version <ver> or --jar <path>" >&2
        exit 2
    fi
}

parse_args() {
    while [[ $# -gt 0 ]]; do
        case "$1" in
            --version)
                VERSION="${2:-}"
                shift 2
                ;;
            --jar)
                JAR_PATH="${2:-}"
                shift 2
                ;;
            --cases-dir)
                CASES_DIR="${2:-}"
                shift 2
                ;;
            --bless)
                BLESS=true
                shift
                ;;
            -h|--help)
                print_usage
                exit 0
                ;;
            *)
                echo "ERROR: unknown argument: $1" >&2
                print_usage
                exit 2
                ;;
        esac
    done
}

validate_case_files() {
    local case_dir="$1"

    local in_fit="$case_dir/input.fit"
    local in_zip="$case_dir/input.zip"
    local master="$case_dir/master.fit"
    local inputs="$case_dir/inputs.txt"

    if [[ ! -f "$master" && "$BLESS" != "true" ]]; then
        echo "missing master.fit"
        return 1
    fi
    if [[ ! -f "$inputs" ]]; then
        echo "missing inputs.txt"
        return 1
    fi
    if [[ ! -f "$in_fit" && ! -f "$in_zip" ]]; then
        echo "missing input.fit or input.zip"
        return 1
    fi

    return 0
}

run_case() {
    local case_dir="$1"
    local case_name
    case_name="$(basename "$case_dir")"

    local in_fit="$case_dir/input.fit"
    local in_zip="$case_dir/input.zip"
    local master="$case_dir/master.fit"
    local inputs="$case_dir/inputs.txt"
    local conf="$case_dir/conf.txt"

    local run_dir="$case_dir/.run"
    rm -rf "$run_dir"
    mkdir -p "$run_dir"

    local ext="fit"
    if [[ -f "$in_fit" ]]; then
        cp "$in_fit" "$run_dir/aaa.fit"
        ext="fit"
    else
        cp "$in_zip" "$run_dir/aaa.zip"
        ext="zip"
    fi

    if [[ -f "$conf" ]]; then
        cp "$conf" "$run_dir/conf.txt"
    else
        cat > "$run_dir/conf.txt" <<EOF
filePathPrefix ${run_dir}/
inputFilePath aaa.${ext}
profileNameSuffix regression
timeOffsetMin 2
EOF
    fi

    # Stage optional extra assets used by interactive flows (e.g. aa command).
    # Any regular file at case root except known control files is copied as-is.
    while IFS= read -r extra_file; do
        cp "$extra_file" "$run_dir/$(basename "$extra_file")"
    done < <(find "$case_dir" -maxdepth 1 -type f \
        ! -name 'input.fit' \
        ! -name 'input.zip' \
        ! -name 'master.fit' \
        ! -name 'inputs.txt' \
        ! -name 'conf.txt' \
        ! -name 'README*')

    # Render stdin inputs so case files can use runtime-aware paths.
    local rendered_inputs="$run_dir/inputs.rendered.txt"
    sed \
        -e "s|{{RUN_DIR}}|$run_dir|g" \
        -e "s|{{DOWNLOADS}}|$HOME/Downloads|g" \
        "$inputs" > "$rendered_inputs"

    local log_file="$run_dir/run.log"

    echo "[RUN ] $case_name"
    (cd "$run_dir" && java -jar "$JAR_PATH" < "$rendered_inputs" > "$log_file" 2>&1)
    local run_exit=$?

    if [[ $run_exit -ne 0 ]]; then
        echo "[FAIL] $case_name (java exit=$run_exit)"
        echo "       log: $log_file"
        return 1
    fi

    local new_fit
    new_fit="$(find "$run_dir" -maxdepth 1 -type f -name '*-mergedJava*min.fit' | sort | tail -n 1)"

    if [[ -z "$new_fit" || ! -f "$new_fit" ]]; then
        echo "[FAIL] $case_name (no merged output fit found)"
        echo "       log: $log_file"
        return 1
    fi

    if [[ "$BLESS" == "true" ]]; then
        cp -f "$new_fit" "$master"
        echo "[PASS] $case_name (blessed master.fit)"
        return 0
    fi

    if cmp -s "$new_fit" "$master"; then
        echo "[PASS] $case_name"
        return 0
    fi

    echo "[FAIL] $case_name (fit differs)"
    echo "       new:    $new_fit"
    echo "       master: $master"
    if command -v sha256sum >/dev/null 2>&1; then
        echo "       hashes:"
        sha256sum "$new_fit" "$master" | sed 's/^/         /'
    fi
    echo "       log: $log_file"
    return 1
}

main() {
    parse_args "$@"
    resolve_jar_path

    if [[ ! -d "$CASES_DIR" ]]; then
        echo "ERROR: cases directory not found: $CASES_DIR" >&2
        exit 2
    fi

    # Ignore template/helper directories prefixed with "_".
    mapfile -t case_dirs < <(find "$CASES_DIR" -mindepth 1 -maxdepth 1 -type d ! -name '_*' | sort)

    if [[ ${#case_dirs[@]} -eq 0 ]]; then
        echo "ERROR: no case directories found in $CASES_DIR" >&2
        exit 2
    fi

    echo "============================================"
    echo "GPS Regression"
    echo "Jar      : $JAR_PATH"
    echo "Cases dir: $CASES_DIR"
    echo "Bless    : $BLESS"
    echo "Cases    : ${#case_dirs[@]}"
    echo "============================================"

    local pass_count=0
    local fail_count=0
    local invalid_count=0

    for case_dir in "${case_dirs[@]}"; do
        local invalid_reason=""
        if ! invalid_reason="$(validate_case_files "$case_dir")"; then
            echo "[SKIP] $(basename "$case_dir") (invalid case: ${invalid_reason})"
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

    if [[ $fail_count -gt 0 || $invalid_count -gt 0 ]]; then
        exit 1
    fi
    exit 0
}

main "$@"
