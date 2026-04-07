#!/usr/bin/env bash
set -euo pipefail

print_usage() {
    cat <<'EOF'
Usage: ./collect-laps.sh [search-string] [folder]

Arguments:
  search-string   Optional text filter matched against the lap result file path.
  folder          Optional folder to search. Defaults to the current directory.

The script finds lap result files matching *laps*.txt, sorts them in reverse
filename order, prints each filename between dashes, and then prints the
ACTIVE/REST lap sections to both console and a new output file.
EOF
}

sanitize_name() {
    local value="$1"

    value="$(printf '%s' "$value" | tr -cs '[:alnum:]._-' '_')"
    value="${value#_}"
    value="${value%_}"
    [[ -n "$value" ]] || value="all"

    printf '%s\n' "$value"
}

display_path() {
    local file_path="$1"
    local file_name
    local parent_name

    file_name="$(basename "$file_path")"
    parent_name="$(basename "$(dirname "$file_path")")"

    printf '%s --- %s\n' "$file_name" "$parent_name"
}

print_file_banner() {
    local label="$1"
    local border

    printf -v border '%*s' "${#label}" ''
    border="${border// /=}"

    printf '%s\n' "$label"
    printf '%s\n' "$border"
}

matches_filter() {
    local path="$1"
    local filter="$2"
    local name

    name="$(basename "$path")"

    [[ -z "$filter" ]] && return 0

    # Treat wildcard patterns as "contains" globs; otherwise use plain substring match.
    if [[ "$filter" == *'*'* || "$filter" == *'?'* || "$filter" == *'['* ]]; then
        [[ "$name" == *$filter* ]]
    else
        [[ "$name" == *"$filter"* ]]
    fi
}

extract_lap_sections() {
    local file_path="$1"

    awk '
        /---- ACTIVE LAPS ----/ { capture=1 }
        capture { print }
    ' "$file_path"
}

main() {
    if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
        print_usage
        exit 0
    fi

    if [[ $# -gt 2 ]]; then
        echo "ERROR: expected at most 2 arguments." >&2
        print_usage >&2
        exit 2
    fi

    local search_string="${1:-}"
    local search_dir="${2:-.}"

    if [[ ! -d "$search_dir" ]]; then
        echo "ERROR: folder not found: $search_dir" >&2
        exit 2
    fi

    local timestamp safe_search output_file
    timestamp="$(date +%Y%m%d-%H%M%S)"
    safe_search="$(sanitize_name "${search_string:-all}")"
    output_file="$PWD/lap-results-${safe_search}-${timestamp}.txt"

    local -a matched_files=()
    while IFS= read -r file_path; do
        if matches_filter "$file_path" "$search_string"; then
            matched_files+=("$file_path")
        fi
    done < <(
        find "$search_dir" -type d -name '.*' ! -path "$search_dir" -prune -o -type f -name '*laps*.txt' -print \
            | while IFS= read -r file_path; do
                printf '%s\t%s\n' "$(basename "$file_path")" "$file_path"
            done \
            | sort -t $'\t' -k1,1r -k2,2r \
            | cut -f2-
    )

    if [[ ${#matched_files[@]} -eq 0 ]]; then
        echo "ERROR: no lap result files matched search string '${search_string:-<empty>}' in $search_dir" >&2
        exit 1
    fi

    {
        for file_path in "${matched_files[@]}"; do
            local section_output
            local label
            section_output="$(extract_lap_sections "$file_path")"
            label="$(display_path "$file_path")"

            print_file_banner "$label"
            if [[ -n "$section_output" ]]; then
                printf '%s\n' "$section_output"
            else
                printf '%s\n' "[WARN] No ACTIVE/REST lap section found."
            fi
            printf '\n'
        done
    } | tee "$output_file"

    printf 'Saved output to %s\n' "$output_file"
}

main "$@"