Template GPS regression case for Pause Shorten flow.

This folder is ignored by regr-gps.sh because its name starts with "_".
Copy it to a real case folder name before running, for example:

  cp -r _template-pause-shorten-01 case-pause-shorten-01

Then add these required files in the new case folder:
  input.fit or input.zip
  master.fit

Files in this template:
  conf.txt   : deterministic runtime config
  inputs.txt : scripted menu input sequence (stdin)

Expected menu flow encoded in inputs.txt:
  m          -> show full menu
  ps         -> shorten pause command
  1          -> pause number
  30         -> new pause time (sec)
  y          -> accept gap
  save       -> save and exit
  3          -> change start time in minutes
  Friskis Orminge -> base activity description

Adjust pause number/time and save values to your test scenario.
