#!/usr/bin/env bash
set -euo pipefail

usage() {
    cat <<'EOF'
Usage: ./diff-fit-messages.sh <fit-a> <fit-b> [output-dir] [fit-fix-jar]

Exports activity-file-structure.txt, activity-mesg-list.csv and full-mesg-dump.txt
for both FIT files using fit-fix's non-interactive menu (af/ac/ad), then prints
focused diffs.

Arguments:
  fit-a         First FIT file path (for example new output)
  fit-b         Second FIT file path (for example master)
  output-dir    Optional. Where artifacts are written.
                Default: ./tmp/fit-diff-<timestamp>
  fit-fix-jar   Optional. Explicit fit-fix jar path.
                Default: latest ./jars/fit-fix-*.jar
EOF
}

abs_path() {
    local p="$1"
    if command -v realpath >/dev/null 2>&1; then
        realpath "$p"
    else
        readlink -f "$p"
    fi
}

ensure_file() {
    local p="$1"
    [[ -f "$p" ]] || {
        echo "ERROR: file not found: $p" >&2
        exit 2
    }
}

resolve_jar() {
    local explicit="${1:-}"
    if [[ -n "$explicit" ]]; then
        ensure_file "$explicit"
        echo "$(abs_path "$explicit")"
        return
    fi

    local latest
    latest="$(ls ./jars/fit-fix-[0-9]*.jar 2>/dev/null | sort -V | tail -n 1 || true)"
    if [[ -z "$latest" ]]; then
        echo "ERROR: could not find fit-fix jar in ./jars" >&2
        exit 2
    fi
    echo "$(abs_path "$latest")"
}

run_export() {
    local fit_file="$1"
    local out_dir="$2"
    local jar="$3"

    mkdir -p "$out_dir"

    cat > "$out_dir/conf.txt" <<EOF
filePathPrefix ${out_dir}/
inputFilePath ${fit_file}
profileNameSuffix fit-diff
timeOffsetMin 0
EOF

    cat > "$out_dir/inputs.txt" <<'EOF'
m
af
ac
ad
x
EOF

    (
        cd "$out_dir"
        java -jar "$jar" < inputs.txt > run.log 2>&1
    )

    [[ -f "$out_dir/activity-file-structure.txt" ]] || {
        echo "ERROR: missing activity-file-structure.txt in $out_dir" >&2
        exit 1
    }
    [[ -f "$out_dir/activity-mesg-list.csv" ]] || {
        echo "ERROR: missing activity-mesg-list.csv in $out_dir" >&2
        exit 1
    }
}

preview_diff() {
    local label="$1"
    local file_a="$2"
    local file_b="$3"

    echo
    echo "===== ${label} ====="
    if cmp -s "$file_a" "$file_b"; then
        echo "No differences"
        return
    fi

    diff -u "$file_a" "$file_b" | sed -n '1,140p' || true
}

build_full_dump_tool() {
    local build_dir="$1"
    local jar="$2"

    cat > "$build_dir/FitDump.java" <<'EOF'
import com.garmin.fit.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FitDump {
    private static String sval(Object v) {
        if (v == null) return "";
        if (v instanceof byte[]) return Arrays.toString((byte[]) v);
        return String.valueOf(v);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: FitDump <fit-file>");
            System.exit(2);
        }

        List<Mesg> all = new ArrayList<>();

        Decode decode = new Decode();
        MesgBroadcaster broadcaster = new MesgBroadcaster(decode);
        broadcaster.addListener((MesgListener) mesg -> all.add(new Mesg(mesg)));

        try (FileInputStream in = new FileInputStream(args[0])) {
            decode.read(in, broadcaster, broadcaster);
        }

        for (int i = 0; i < all.size(); i++) {
            Mesg m = all.get(i);
            String mesgName = MesgNum.getStringFromValue(m.getNum());
            if (mesgName == null || mesgName.isBlank()) {
                mesgName = "UNKNOWN";
            }

            System.out.println("M|" + i + "|num=" + m.getNum() + "|name=" + mesgName + "|local=" + m.getLocalNum());

            for (Field f : m.getFields()) {
                StringBuilder sb = new StringBuilder();
                int n = f.getNumValues();
                for (int vi = 0; vi < n; vi++) {
                    if (vi > 0) sb.append("~");
                    sb.append(sval(f.getValue(vi)));
                }
                String fname = f.getName() != null ? f.getName() : "";
                System.out.println("F|" + f.getNum() + "|" + fname + "|" + sb);
            }

            for (DeveloperField df : m.getDeveloperFields()) {
                StringBuilder sb = new StringBuilder();
                int n = df.getNumValues();
                for (int vi = 0; vi < n; vi++) {
                    if (vi > 0) sb.append("~");
                    sb.append(sval(df.getValue(vi)));
                }
                String dname = df.getName() != null ? df.getName() : "";
                System.out.println("D|ddi=" + df.getDeveloperDataIndex() + "|num=" + df.getNum() + "|" + dname + "|" + sb);
            }
        }
    }
}
EOF

    javac -cp "$jar" -d "$build_dir" "$build_dir/FitDump.java"
}

run_full_dump() {
    local fit_file="$1"
    local out_file="$2"
    local build_dir="$3"
    local jar="$4"

    java -cp "$jar:$build_dir" FitDump "$fit_file" > "$out_file"
}

ensure_full_dump_with_fallback() {
    local fit_file="$1"
    local out_dir="$2"
    local tool_dir="$3"
    local jar="$4"
    local dump_file="$out_dir/full-mesg-dump.txt"

    if [[ -f "$dump_file" ]]; then
        return
    fi

    echo "WARN: full-mesg-dump.txt missing in $out_dir, falling back to legacy FitDump build" >&2
    mkdir -p "$tool_dir"
    build_full_dump_tool "$tool_dir" "$jar"
    run_full_dump "$fit_file" "$dump_file" "$tool_dir" "$jar"
}

mesg_histogram() {
    local csv_file="$1"
    local out_file="$2"

    awk -F',' 'NR>1 {gsub(/^\"|\"$/, "", $8); if ($8!="") c[$8]++} END {for (k in c) printf "%8d  %s\n", c[k], k}' "$csv_file" \
        | sort -nr > "$out_file"
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
    usage
    exit 0
fi

if [[ $# -lt 2 || $# -gt 4 ]]; then
    usage >&2
    exit 2
fi

fit_a="$(abs_path "$1")"
fit_b="$(abs_path "$2")"
output_root="${3:-./tmp/fit-diff-$(date +%Y%m%d-%H%M%S)}"
jar="$(resolve_jar "${4:-}")"

ensure_file "$fit_a"
ensure_file "$fit_b"
mkdir -p "$output_root"
output_root="$(abs_path "$output_root")"

out_a="$output_root/a"
out_b="$output_root/b"
tool_dir="$output_root/tool"

printf 'Using jar: %s\n' "$jar"
printf 'Output  : %s\n' "$output_root"
printf 'A file  : %s\n' "$fit_a"
printf 'B file  : %s\n' "$fit_b"

run_export "$fit_a" "$out_a" "$jar"
run_export "$fit_b" "$out_b" "$jar"

ensure_full_dump_with_fallback "$fit_a" "$out_a" "$tool_dir" "$jar"
ensure_full_dump_with_fallback "$fit_b" "$out_b" "$tool_dir" "$jar"

mesg_histogram "$out_a/activity-mesg-list.csv" "$out_a/mesg-histogram.txt"
mesg_histogram "$out_b/activity-mesg-list.csv" "$out_b/mesg-histogram.txt"

preview_diff "message type histogram" "$out_a/mesg-histogram.txt" "$out_b/mesg-histogram.txt"
preview_diff "activity-file-structure" "$out_a/activity-file-structure.txt" "$out_b/activity-file-structure.txt"
preview_diff "activity-mesg-list (first 140 diff lines)" "$out_a/activity-mesg-list.csv" "$out_b/activity-mesg-list.csv"
preview_diff "full-mesg-dump (first 140 diff lines)" "$out_a/full-mesg-dump.txt" "$out_b/full-mesg-dump.txt"

echo
echo "Artifacts:"
echo "  $out_a"
echo "  $out_b"

echo
echo "Suffixed exports found:"
for dir in "$out_a" "$out_b"; do
    echo "  $dir"
    suffixed_files="$(find "$dir" -maxdepth 1 -type f -name '*_activity.*_now.*' | sort || true)"
    if [[ -z "$suffixed_files" ]]; then
        echo "    (none)"
    else
        while IFS= read -r f; do
            [[ -n "$f" ]] && echo "    $(basename "$f")"
        done <<< "$suffixed_files"
    fi
done
