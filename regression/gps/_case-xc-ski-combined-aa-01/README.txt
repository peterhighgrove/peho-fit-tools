Combined-file GPS regression case using ActivityAddAnother (aa).

Primary input: 2026-03-09-10-40-18...
Added input   : 2026-03-09-13-14-52...

Flow:
1. Open primary file
2. Run aa and add second file by absolute path rendered from {{RUN_DIR}}
3. Save and compare against master.fit

Run baseline generation:
  ./regr-gps.sh --version 2.30 --bless

Run regression check:
  ./regr-gps.sh --version 2.30
