$folder = "C:\Users\peter\Downloads"
# Regressiontest 1
#$f1 = "2025-12-26-14-57-28-Elliptical 2x(3-2-1min) 6.5km Friskis Orminge-mergedJava4min-4test.fit"
$f1 = "2025-12-26-14-57-28-Elliptical 2x(3-2-1min) 6.5km Friskis Orminge-mergedJava4min-laps-4test.txt"
#$f2 = "2025-12-26-14-57-28-Elliptical-2x(3-2-1min)-6.5km-31.47min-Friskis Orminge-garmin-e2pro-v25.03-merged4min.fit"
$f2 = "2025-12-26-14-57-28-Elliptical-2x(3-2-1min)-6.5km-31.47min-Friskis Orminge-garmin-e2pro-v25.03-merged4min-laps.txt"
#$f2 = "2025-12-26-14-57-28-Elliptical 2x(3-2-1min) 6.5km Friskis Orminge-mergedJava4min.fit"
# Regressiontest 2
#$f1 = "2025-12-27-14-53-00-SkiErg-bike 2x(3-2-1min) 6.7km Friskis Orminge-mergedJava2min-4test.fit"
#$f2 = "2025-12-27-14-53-00-SkiErg-bike 2x(3-2-1min) 6.7km Friskis Orminge-mergedJava2min.fit"
# Regressiontest 3
#$f1 = "2025-12-01-17-15-19-Löpband 3x1!2min 3.0km gym jobbet-mergedJava6min-4test.fit"
#$f2 = "2025-12-01-17-15-19-Löpband 3x1!2min 3.0km gym jobbet-mergedJava6min.fit"


if ((Get-FileHash "$folder\$f1").Hash -eq (Get-FileHash "$folder\$f2").Hash) {
    "Files are identical"
} else {
    "Files differ"
}
