Single-file GPS regression case using XC Classic Ski 17.1 km input.

Flow:
1. Open file
2. Save immediately with configured start-time offset and profile suffix
3. Compare output FIT with master.fit

Run baseline generation:
  ./regr-gps.sh --version 2.30 --bless

Run regression check:
  ./regr-gps.sh --version 2.30
