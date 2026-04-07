#!/usr/bin/env bash
set -euo pipefail

print_usage() {
    cat <<'EOF'
Usage: ./collect-laps-grouped.sh [search-string] [folder]

Arguments:
  search-string   Optional text filter matched against the lap result file path.
  folder          Optional folder to search. Defaults to the current directory.

The script finds lap result files matching *laps*.txt, sorts them in reverse
filename order, groups output as active-lapN/rest-lapN, suppresses the LapN
prefix on each lap line, and writes to both console and a new output file.
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

print_banner() {
    local label="$1"
    local border

    printf -v border '%*s' "${#label}" ''
    border="${border// /=}"

    printf '%s\n' "$label"
    printf '%s\n' "$border"
}

extract_date_label() {
    local file_path="$1"
    local base

    base="$(basename "$file_path")"

    if [[ "$base" =~ ([0-9]{4}-[0-9]{2}-[0-9]{2}[-_][0-9]{2}[-_][0-9]{2}[-_][0-9]{2}) ]]; then
        printf '%s\n' "${BASH_REMATCH[1]//_/-}"
        return
    fi

    if [[ "$base" =~ ([0-9]{4}-[0-9]{2}-[0-9]{2}) ]]; then
        printf '%s\n' "${BASH_REMATCH[1]}"
        return
    fi

    printf '%s\n' "${base%.txt}"
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
    output_file="$PWD/lap-results-grouped-${safe_search}-${timestamp}.txt"

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

    declare -A grouped_lines=()
    declare -A seen_active=()
    declare -A seen_rest=()
    local -a active_laps=()
    local -a rest_laps=()

    local file_path
    for file_path in "${matched_files[@]}"; do
        local date_label section line lap_no lap_text key
        date_label="$(extract_date_label "$file_path")"
        section=""

        while IFS= read -r line; do
            if [[ "$line" == "---- ACTIVE LAPS ----" ]]; then
                section="active"
                continue
            fi
            if [[ "$line" == "---- REST LAPS ----" ]]; then
                section="rest"
                continue
            fi

            if [[ "$line" =~ ^Lap([0-9]+)[[:space:]]+(.*)$ ]]; then
                lap_no="${BASH_REMATCH[1]}"
                lap_text="${BASH_REMATCH[2]}"
                key="${section}-lap${lap_no}"
                grouped_lines["$key"]+="${date_label} ${lap_text}"$'\n'

                if [[ "$section" == "active" && -z "${seen_active[$lap_no]:-}" ]]; then
                    seen_active["$lap_no"]=1
                    active_laps+=("$lap_no")
                fi
                if [[ "$section" == "rest" && -z "${seen_rest[$lap_no]:-}" ]]; then
                    seen_rest["$lap_no"]=1
                    rest_laps+=("$lap_no")
                fi
            fi
        done < "$file_path"
    done

    {
        local lap_no key

        while IFS= read -r lap_no; do
            [[ -n "$lap_no" ]] || continue
            key="active-lap${lap_no}"
            print_banner "$key"
            printf '%s' "${grouped_lines[$key]}"
            printf '\n'
        done < <(printf '%s\n' "${active_laps[@]}" | sort -n)

        while IFS= read -r lap_no; do
            [[ -n "$lap_no" ]] || continue
            key="rest-lap${lap_no}"
            print_banner "$key"
            printf '%s' "${grouped_lines[$key]}"
            printf '\n'
        done < <(printf '%s\n' "${rest_laps[@]}" | sort -n)
    } | tee "$output_file"

    printf 'Saved output to %s\n' "$output_file"
}

main "$@"