Using Garmin FIT SDK to merge in data from manual laps file for cardio activities or Concept2 file and CIQ data for SkiErg activities to analyze garmin FIT file. For instance to get correct distance data in native distance field.
After that create a new FIT file that can be imported to Garmin Connect (GC).

**Some general features**
- Adding a configurable number of minutes to file to be able to import it again to GC.
- Correcting cadence unrealistic peaks
- Correcting empty start values
- Can be configured with args or conf.txt file.

**Sport apps handled**
- SkiErg (merges CIQ app data and Concept2 FIT file)
- Elliptical (merges from manual laps file)
- Gymbike (merges from manual laps file)
- Treadmill (merges from manual laps file)

**Example analyze file:**<br>
---- ACTIVE LAPS ----<br>
Lap2 HRmin90>st120+20->max140 end139 5min 62spm 2:18min/500m 134W 1.1km df111 sl0.6<br>
Lap4 HRmin113>st121+22->max143 end141 4min 63spm 2:16min/500m 139W 0.9km df111 sl0.64<br>
Lap6 HRmin117>st125+20->max145 end145 3min 64spm 2:12min/500m 148W 0.7km df111 sl0.65<br>
Lap8 HRmin121>st129+16->max145 end144 2min 65spm 2:11min/500m 151W 0.5km df111 sl0.63<br>
Lap10 HRmin129>st130+12->max142 end142 1min 68spm 2:09min/500m 167W 0.2km df112 sl0.66<br>
Lap12 HRmin139>st140+9->max148 end148 1min 69spm 2:06min/500m 173W 0.2km df111 sl0.68<br>
Lap14 HRmin145>st145+4->max147 end146 2min 64spm 2:13min/500m 147W 0.5km df111 sl0.65<br>
Lap16 HRmin132>st132+14->max146 end146 3min 64spm 2:14min/500m 144W 0.7km df111 sl0.65<br>
Lap18 HRmin123>st127+21->max148 end148 4min 65spm 2:14min/500m 143W 0.9km df111 sl0.63<br>
Lap20 HRmin119>st121+25->max146 end146 5min 65spm 2:17min/500m 135W 1.1km df110 sl0.62<br>
avgCad: 64spm, avgPace: 2:15min/500m, avgPow: 143W, lapDist: 6.7km<br>
---- REST LAPS ----<br>
Lap3 HRst139>max140-27->min113 end121 2min 56spm 3:06min/500m 60W 0.3km<br>
Lap5 HRst141>max141-24->min117 end125 2min 55spm 3:05min/500m 64W 0.3km<br>
Lap7 HRst145>max145-24->min121 end129 1:30min 58spm 2:51min/500m 79W 0.3km<br>
Lap9 HRst145>max145-16->min129 end130 1min 50spm 3:19min/500m 50W 0.2km<br>
Lap11 HRst142>max143-4->min139 end139 0:30min 47spm 3:04min/500m 59W 0.1km<br>
Lap13 HRst148>max149-4->min145 end145 0:30min 51spm 3:19min/500m 60W 0.1km<br>
Lap15 HRst146>max147-15->min132 end132 1min 49spm 3:23min/500m 44W 0.1km<br>
Lap17 HRst146>max147-24->min123 end126 1:30min 53spm 3:32min/500m 47W 0.2km<br>
Lap19 HRst148>max148-29->min119 end121 2min 52spm 3:52min/500m 33W 0.3km<br>
Lap21 HRst146>max146-25->min121 end123 2min 50spm 3:41min/500m 43W 0.3km<br>
avgCad: 53spm, avgPace: 3:19min/500m, avgPow: 53W, lapDist: 2.1km<br>
<br>
**Example manual laps.txt fil (level (space) 10th-of-meter (space) comment)**<br>
9 080<br>
10 148 1<br>
9 166<br>
10 235 2<br>
9 252<br>
11 323 3<br>
9 340<br>
11 410<br>
9 426<br>
8 521<br>
