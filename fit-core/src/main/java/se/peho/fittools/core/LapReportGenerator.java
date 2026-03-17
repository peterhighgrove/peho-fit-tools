package se.peho.fittools.core;

import com.garmin.fit.Intensity;
import com.garmin.fit.Mesg;
import com.garmin.fit.FitRuntimeException;

public class LapReportGenerator {
    private final FitFile fitFile;

    public LapReportGenerator(FitFile fitFile) {
        this.fitFile = fitFile;
    }

    public void printLapRecords0() {
        try {
            System.out.println();
            System.out.println("===================================================================");
            System.out.println("LAPS IN FILE (lap1)");
            System.out.println("Start datetime: " + FitDateTime.toString(fitFile.getLapMesg().get(0).getFieldLongValue(FitFile.LAP_STIME), fitFile.getDiffMinutesLocalUTC()));
            System.out.println("-------------------------------------------------------------------");
            System.out.println("No  Ix   Start           Time  Dist Speed Pace   Cad Intensity");
            System.out.println("         timer  clock          km   km/h  min/km ");
            System.out.println("--- --- ------ --------  ----- ---- ----- ------ --- --------");
            int lapNo = 1;
            for (Mesg mesg : fitFile.getLapMesg()) {
                String lapIxStr = "-";
                Integer lapIx = mesg.getFieldIntegerValue(FitFile.LAP_IX);
                if (lapIx != null) lapIxStr = String.format("%d", lapIx);

                String startTimeStr = "-";
                Long startTime = mesg.getFieldLongValue(FitFile.LAP_STIME);
                if (startTime != null) startTimeStr = FitDateTime.toStringTime(startTime, fitFile.getDiffMinutesLocalUTC());

                String lapTimerStr = "-";
                if (startTime != null) lapTimerStr = PehoUtils.sec2minSecLong(fitFile.findTimerBasedOnTime(startTime));

                String lapTimeStr = "-";
                Float totalTimer = mesg.getFieldFloatValue(FitFile.LAP_TIMER);
                if (totalTimer != null) lapTimeStr = PehoUtils.sec2minSecShort(totalTimer);

                String lapDistStr = "-";
                Float totalDistance = mesg.getFieldFloatValue(FitFile.LAP_DIST);
                if (totalDistance != null) lapDistStr = PehoUtils.m2km2(totalDistance);

                String speedKmhStr = "-";
                String paceStr = "-";
                Float avgSpeed = mesg.getFieldFloatValue(FitFile.LAP_ESPEED);
                if (avgSpeed != null) {
                    speedKmhStr = PehoUtils.mps2kmph1(avgSpeed);
                    paceStr = PehoUtils.mps2minpkm(avgSpeed);
                }

                String lapCadStr = "-";
                Short avgCadence = mesg.getFieldShortValue(FitFile.LAP_CAD);
                if (avgCadence != null) lapCadStr = String.format("%d", avgCadence);

                String intensityStr = "-";
                Long intensity = mesg.getFieldLongValue(FitFile.LAP_INTENSITY);
                if (intensity != null) intensityStr = PehoUtils.getLabel(Intensity.class, intensity);

                System.out.printf("%-3d %-3s %6s %-7s %6s %-4s %-5s %-6s %-3s %-8s%n", lapNo, lapIxStr, lapTimerStr, startTimeStr, lapTimeStr, lapDistStr, speedKmhStr, paceStr, lapCadStr, intensityStr);
                lapNo++;
            }
            Integer sesLaps = null;
            for (Mesg ses : fitFile.getSessionMesg()) {
                sesLaps = ses.getFieldIntegerValue(FitFile.SES_LAPS);
                if (sesLaps != null) break;
            }
            System.out.println("-------------------------------------------------------------------");
            System.out.println("Number of laps: " + fitFile.getNumberOfLaps() + ", Session laps: " + (sesLaps != null ? sesLaps : "-"));
        }
        catch (FitRuntimeException e) {
            System.out.println("LAP ERROR!!!!");
        }
    }

    public void printLapRecord(int ix) {
        Mesg lapRecord = fitFile.getLapMesg().get(ix);
        int lapNo = ix + 1;
        System.out.print("Lap:" + lapNo);

        // Level from extra records
        // if (lapExtraRecords.get(i).level != null) {
        //     System.out.print(" lv" + lapExtraRecords.get(i).level);
        // }

        // Timer
        Float totalTimer = lapRecord.getFieldFloatValue(FitFile.LAP_TIMER);
        if (totalTimer != null) System.out.print(" " + PehoUtils.sec2minSecShort(totalTimer) + "min");

        // Distance
        Float lapDist = lapRecord.getFieldFloatValue(FitFile.LAP_DIST);
        if (lapDist != null) System.out.print(" " + PehoUtils.m2km2(lapDist) + "km");

        // DistFrom / DistTo from secRecords
        //System.out.print(" DistFrom: " + secRecords.get(lapExtraRecords.get(i).recordIxStart).getDistance());
        //System.out.print(" DistTo: " + secRecords.get(lapExtraRecords.get(i).recordIxEnd).getDistance());

        // Enhanced average speed
        Float enhAvgSpeed = lapRecord.getFieldFloatValue(FitFile.LAP_ESPEED);
        if (enhAvgSpeed != null) System.out.print(" " + PehoUtils.mps2minpkm(enhAvgSpeed)+ "min/km");

        // Cadence
        Short avgCadence = lapRecord.getFieldShortValue(FitFile.LAP_CAD);
        if (avgCadence != null) System.out.print(" " + avgCadence + "spm");

        // Intensity
        Short intensityRaw = lapRecord.getFieldShortValue(FitFile.LAP_INTENSITY);
        if (intensityRaw != null) {
            Intensity intensityEnum = Intensity.getByValue(intensityRaw.shortValue());
            String intensityLabel = intensityEnum != null ? Intensity.getStringFromValue(intensityEnum) : "unknown";
            System.out.print(" WktIntensity:" + intensityLabel);
        }

        // Workout Step Index
        Integer wktStepIx = lapRecord.getFieldIntegerValue(FitFile.LAP_WKT_STEP_IDX);
        if (wktStepIx != null) System.out.print(" WktStepIx:" + wktStepIx);

        // Start Time
        Long startTime = lapRecord.getFieldLongValue(FitFile.LAP_STIME);
        if (startTime != null) {
            System.out.print(" start@:" + PehoUtils.sec2minSecLong(fitFile.findTimerBasedOnTime(startTime)) + ", " + FitDateTime.toString(startTime, fitFile.getDiffMinutesLocalUTC()));
        }

        // Start Timer WRONG VALUE!!!!!
        Long startTimer = lapRecord.getFieldLongValue(FitFile.LAP_TIMER);
        if (startTimer != null) {
            //System.out.print(" " + FitDateTime.toTimerString(startTimer));
        }

        /* // Timestamp
        Long timestamp = mesg.getFieldLongValue(LAP_TIME);
        if (timestamp != null) {
            System.out.print(" Timestamp: " + FitDateTime.toString(timestamp, diffMinutesLocalUTC));
        } */

        /*/ Extra record fields
        if (lapExtraRecords.get(i).timeEnd != null) System.out.print(" TimeEnd: " + lapExtraRecords.get(i).timeEnd);
        if (lapExtraRecords.get(i).stepLen != null) System.out.print(" StepLen: " + lapExtraRecords.get(i).stepLen);
        if (lapExtraRecords.get(i).hrStart != 0) System.out.print(" hrStart: " + lapExtraRecords.get(i).hrStart);
        if (lapExtraRecords.get(i).hrEnd != 0) System.out.print(" hrEnd: " + lapExtraRecords.get(i).hrEnd);
        if (lapExtraRecords.get(i).recordIxEnd != 0) System.out.print(" recordIxEnd: " + lapExtraRecords.get(i).recordIxEnd);
        if (lapExtraRecords.get(i).hrMin != 0) System.out.print(" hrMin: " + lapExtraRecords.get(i).hrMin);
        */
        System.out.println();
    }

    public void printLapRecords() {
        int ix = 0;

        try {
            System.out.println();
            System.out.println("================================================");
            System.out.println("====LAPS IN FILE (lap2)");

            for (Mesg mesg : fitFile.getLapMesg()) {
                printLapRecord(ix);
                ix++;
            }

            System.out.println("------------------------------------------------");
        } catch (FitRuntimeException e) {
            System.out.println("LAP ERROR!!!!");
        }
    }

    public void printLapAvgMaxSpeed(Float avgSpeed, Float maxSpeed) {
        if (avgSpeed != null) {
            if (fitFile.isSkiErgFile()) {
                System.out.print("--Sp avg:" + PehoUtils.mps2minp500m(avgSpeed));
                System.out.print(" max:" + PehoUtils.mps2minp500m(maxSpeed));
            } else {
                System.out.print("--Sp avg:" + PehoUtils.mps2minpkm(avgSpeed));
                System.out.print(" max:" + PehoUtils.mps2minpkm(maxSpeed));
            }
        }
    }

    public void printLapAvgSpeed(Float avgSpeed) {
        if (avgSpeed != null) {
            if (fitFile.isSkiErgFile()) {
                System.out.print(" " + PehoUtils.mps2minp500m(avgSpeed) + "min/500m");
            } else {
                System.out.print(" " + PehoUtils.mps2minpkm(avgSpeed) + "min/km");
                System.out.print(" " + PehoUtils.mps2kmph1(avgSpeed) + "km/h");
            }
        }
    }

    public String lapAvgSpeed(Float avgSpeed) {
        String tempString = "";
        if (avgSpeed != null) {
            if (fitFile.isSkiErgFile()) {
                tempString += " " + PehoUtils.mps2minp500m(avgSpeed) + "min/500m";
            } else {
                tempString += " " + PehoUtils.mps2minpkm(avgSpeed) + "min/km";
                tempString += " " + PehoUtils.mps2kmph1(avgSpeed) + "km/h";
            }
        }
        return tempString;
    }

    public String lapEndSum2String(Float avgCad, Float avgSpeed, Float avgPower, Float dist) {
        String tempString = "";
        tempString += "avgCad: " + (int) Math.round(avgCad) + "spm";
        if (fitFile.isSkiErgFile()) {
            tempString += ", avgPace: " + PehoUtils.mps2minp500m(avgSpeed) + "min/500m";
            tempString += ", avgPow: " + (int) Math.round(avgPower) + "W";
        } else {
            tempString += ", avgPace: " + PehoUtils.mps2minpkm(avgSpeed) + "min/km";
            tempString += String.format(", avgSp: %.1fkm/h", avgSpeed * 3.60);
        }
        tempString += String.format(", sumDist: %.1fkm", + dist / 1000);
        tempString += System.lineSeparator();
        return tempString;
    }

    public void printLapAllSummary() {
        int i = 0;
        int lapNo = 1;
        System.out.println();
        System.out.println("================================================");
        System.out.println("====LAPS IN FILE (lap4-LapAllSummary)");
        for (Mesg mesg : fitFile.getLapMesg()) {
            System.out.print("Lap:" + lapNo);

            // Start time
            Long startTime = mesg.getFieldLongValue(FitFile.LAP_STIME);
            if (startTime != null) {
                System.out.print(" StartTime:" + FitDateTime.toString(startTime, fitFile.getDiffMinutesLocalUTC()));
            }

            /*
            // Extra fields: level and step length (skip if SkiErg)
            if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
            }
            if (lapExtraRecords.get(i).stepLen != null && !isSkiErgFile()) {
                System.out.print(" steplen" + (int) (lapExtraRecords.get(i).stepLen * 100) + "cm");
            }
            */

            // Total timer
            Float totalTimer = mesg.getFieldFloatValue(FitFile.LAP_TIMER);
            if (totalTimer != null) System.out.print(" LapTime: " + totalTimer);

            // Intensity
            Short intensityVal = (Short) mesg.getFieldValue(FitFile.LAP_INTENSITY);
            String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

            System.out.print(" WktIntensity: " + intensity);

            /*
            // Heart rate logic
            Integer maxHr = mesg.getFieldIntegerValue(LAP_MAX_HR);
            if ("ACTIVE".equals(intensityLabel) || "WARMUP".equals(intensityLabel)) {
                System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                System.out.print(" min:" + lapExtraRecords.get(i).hrMin);
                System.out.print("+" + ((maxHr != null ? maxHr : 0) - lapExtraRecords.get(i).hrMin));
                System.out.print("-->max:" + (maxHr != null ? maxHr : "N/A"));
                System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
            } else {
                System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                System.out.print(" max:" + (maxHr != null ? maxHr : "N/A"));
                System.out.print("" + (lapExtraRecords.get(i).hrMin - (maxHr != null ? maxHr : 0)));
                System.out.print("-->min:" + lapExtraRecords.get(i).hrMin);
                System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
            }
            */

            // Distance
            Float totalDist = mesg.getFieldFloatValue(FitFile.LAP_DIST);
            if (totalDist != null) System.out.print("--Dist:" + totalDist);

            // Speed
            Float enhAvgSpeed = mesg.getFieldFloatValue(FitFile.LAP_ESPEED);
            Float enhMaxSpeed = mesg.getFieldFloatValue(FitFile.LAP_EMSPEED);
            printLapAvgMaxSpeed(enhAvgSpeed, enhMaxSpeed);

            // Cadence
            Short avgCadence = mesg.getFieldShortValue(FitFile.LAP_CAD);
            Short maxCadence = mesg.getFieldShortValue(FitFile.LAP_MCAD);
            if (avgCadence != null) {
                System.out.print("--Cad avg:" + avgCadence);
                System.out.print(" max:" + (maxCadence != null ? maxCadence : "N/A"));
            }

            // Power
            Integer avgPower = mesg.getFieldIntegerValue(FitFile.LAP_POW);
            Integer maxPower = mesg.getFieldIntegerValue(FitFile.LAP_MPOW);
            if (avgPower != null) {
                System.out.print("--Pow avg:" + avgPower);
                System.out.print(" max:" + (maxPower != null ? maxPower : "N/A"));
            }

            /*
            // Extra lap info: Drag Factor and Stroke Length
            if (lapExtraRecords.get(i).avgDragFactor != null) {
                System.out.print("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                System.out.print(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
            }
            if (lapExtraRecords.get(i).avgStrokeLen != null) {
                System.out.print("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                System.out.print(" max:" + lapExtraRecords.get(i).maxStrokeLen);
            }
            */

            System.out.println();
            i++;
            lapNo++;
        }
    }

    public void printLapLongSummery() {
        System.out.println();
        System.out.println("================================================");
        System.out.println("====LAPS IN FILE (lap3-LapLongSummary)");
        System.out.println("---- ACTIVE LAPS ----");
        int i = 0;
        int lapNo = 1;

        // ACTIVE laps
        for (Mesg mesg : fitFile.getLapMesg()) {
            Short intensityVal = (Short) mesg.getFieldValue(FitFile.LAP_INTENSITY);
            String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

            if ("ACTIVE".equals(intensity)) {
                System.out.print("Lap:" + lapNo);

                /*
                if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                    System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
                }
                */

                Float totalTimer = mesg.getFieldFloatValue(FitFile.LAP_TIMER);
                if (totalTimer != null) {
                    System.out.print(" LapTime: " + PehoUtils.sec2minSecShort(totalTimer));
                }

                /*
                System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                if (i > 0) {
                    System.out.print(" HRmin" + lapExtraRecords.get(i - 1).hrMin);
                } else {
                    System.out.print(" HR");
                }
                System.out.print(" min:" + lapExtraRecords.get(i).hrMin);
                System.out.print("+" + (mesg.getFieldIntegerValue(LAP_MAX_HR) - lapExtraRecords.get(i).hrMin));
                System.out.print("-->max:" + mesg.getFieldIntegerValue(LAP_MAX_HR));
                System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
                */

                Float totalDist = mesg.getFieldFloatValue(FitFile.LAP_DIST);
                if (totalDist != null) System.out.print("--Dist:" + totalDist);

                Float enhAvgSpeed = mesg.getFieldFloatValue(FitFile.LAP_ESPEED);
                Float enhMaxSpeed = mesg.getFieldFloatValue(FitFile.LAP_EMSPEED);
                printLapAvgMaxSpeed(enhAvgSpeed, enhMaxSpeed);

                Short avgCadence = mesg.getFieldShortValue(FitFile.LAP_CAD);
                Short maxCadence = mesg.getFieldShortValue(FitFile.LAP_MCAD);
                if (avgCadence != null) {
                    System.out.print("--Cad avg:" + avgCadence);
                    System.out.print(" max:" + (maxCadence != null ? maxCadence : "N/A"));
                }

                Integer avgPower = mesg.getFieldIntegerValue(FitFile.LAP_POW);
                Integer maxPower = mesg.getFieldIntegerValue(FitFile.LAP_MPOW);
                if (avgPower != null) {
                    System.out.print("--Pow avg:" + avgPower);
                    System.out.print(" max:" + (maxPower != null ? maxPower : "N/A"));
                }

                /*
                if (lapExtraRecords.get(i).avgDragFactor != null) {
                    System.out.print("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                    System.out.print(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
                }
                if (lapExtraRecords.get(i).avgStrokeLen != null) {
                    System.out.print("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                    System.out.print(" max:" + lapExtraRecords.get(i).maxStrokeLen);
                }
                */

                System.out.println();
            }
            i++;
            lapNo++;
        }

        // REST/RECOVERY laps
        System.out.println("---- REST LAPS ----");
        i = 0;
        lapNo = 1;

        for (Mesg mesg : fitFile.getLapMesg()) {
            Short intensityVal = (Short) mesg.getFieldValue(FitFile.LAP_INTENSITY);
            String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

            if ("REST".equals(intensity) || "RECOVERY".equals(intensity)) {
                System.out.print("Lap:" + lapNo);

                /*
                if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                    System.out.print(" lv" + lapExtraRecords.get(i).level.intValue());
                }
                */

                Float totalTimer = mesg.getFieldFloatValue(FitFile.LAP_TIMER);
                if (totalTimer != null) {
                    System.out.print(" LapTime: " + PehoUtils.sec2minSecShort(totalTimer));
                }

                /*
                System.out.print(" HR start:" + lapExtraRecords.get(i).hrStart);
                System.out.print(" max:" + mesg.getFieldIntegerValue(LAP_MAX_HR));
                System.out.print("" + (lapExtraRecords.get(i).hrMin - mesg.getFieldIntegerValue(LAP_MAX_HR)));
                System.out.print("-->min:" + lapExtraRecords.get(i).hrMin);
                System.out.print(" end:" + lapExtraRecords.get(i).hrEnd);
                */

                Float totalDist = mesg.getFieldFloatValue(FitFile.LAP_DIST);
                if (totalDist != null) System.out.print("--Dist:" + totalDist);

                Float enhAvgSpeed = mesg.getFieldFloatValue(FitFile.LAP_ESPEED);
                Float enhMaxSpeed = mesg.getFieldFloatValue(FitFile.LAP_EMSPEED);
                printLapAvgMaxSpeed(enhAvgSpeed, enhMaxSpeed);

                Short avgCadence = mesg.getFieldShortValue(FitFile.LAP_CAD);
                Short maxCadence = mesg.getFieldShortValue(FitFile.LAP_MCAD);
                if (avgCadence != null) {
                    System.out.print("--Cad avg:" + avgCadence);
                    System.out.print(" max:" + (maxCadence != null ? maxCadence : "N/A"));
                }

                Integer avgPower = mesg.getFieldIntegerValue(FitFile.LAP_POW);
                Integer maxPower = mesg.getFieldIntegerValue(FitFile.LAP_MPOW);
                if (avgPower != null) {
                    System.out.print("--Pow avg:" + avgPower);
                    System.out.print(" max:" + (maxPower != null ? maxPower : "N/A"));
                }

                /*
                if (lapExtraRecords.get(i).avgDragFactor != null) {
                    System.out.print("--DFavg:" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor));
                    System.out.print(" max:" + (int) Math.round(lapExtraRecords.get(i).maxDragFactor));
                }
                if (lapExtraRecords.get(i).avgStrokeLen != null) {
                    System.out.print("--SLavg:" + lapExtraRecords.get(i).avgStrokeLen);
                    System.out.print(" max:" + lapExtraRecords.get(i).maxStrokeLen);
                }
                */

                System.out.println();
            }
            i++;
            lapNo++;
        }
    }

    public String createLapSummery() {
        String tempString = "";
        try {
            tempString += "---- ACTIVE LAPS ----" + System.lineSeparator();
            int i = 0;
            int lapNo = 1;
            for (Mesg mesg : fitFile.getLapMesg()) {

                Short intensityVal = (Short) mesg.getFieldValue(FitFile.LAP_INTENSITY);
                String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

                if ("ACTIVE".equals(intensity)) {
                    tempString += "Lap" + lapNo;

                    // --- lapExtra commented out ---
                    // if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                    //     if (isTreadmillFile()) {
                    //         tempString += " " + lapExtraRecords.get(i).level.intValue() + "%";
                    //     } else {
                    //         tempString += " lv" + lapExtraRecords.get(i).level.intValue();
                    //     }
                    // }

                    tempString += " HR";
                    // tempString += ">st" + lapExtraRecords.get(i).hrStart;

                    Short hrMax = mesg.getFieldShortValue(FitFile.LAP_MHR);
                    if (hrMax != null) {
                        tempString += "->max" + hrMax;
                    }
                    // tempString += " end" + lapExtraRecords.get(i).hrEnd;

                    Float timer = mesg.getFieldFloatValue(FitFile.LAP_TIMER);
                    if (timer != null) {
                        tempString += " " + PehoUtils.sec2minSecShort(timer) + "min";
                    }

                    Short cadence = mesg.getFieldShortValue(FitFile.LAP_CAD);
                    if (cadence != null) {
                        tempString += " " + cadence + "spm";
                    }

                    Float speed = mesg.getFieldFloatValue(FitFile.LAP_ESPEED);
                    if (speed != null) {
                        if (fitFile.isSkiErgFile()) {
                            tempString += " " + PehoUtils.sec2minSecLong(500 / speed) + "min/500m";
                        } else {
                            tempString += " " + PehoUtils.sec2minSecLong(1000 / speed) + "min/km";
                            tempString += " " + String.format("%.1fkm/h", speed * 3.60);
                        }
                    }

                    Integer power = mesg.getFieldIntegerValue(FitFile.LAP_POW);
                    if (power != null) {
                        tempString += " " + power + "W";
                    }

                    Float dist = mesg.getFieldFloatValue(FitFile.LAP_DIST);
                    if (dist != null) {
                        tempString += " " + String.format("%.1fkm", dist / 1000);
                    }

                    // --- lapExtra commented out ---
                    // if (lapExtraRecords.get(i).avgDragFactor != null && isSkiErgFile()) {
                    //     tempString += " df" + (int) Math.round(lapExtraRecords.get(i).avgDragFactor);
                    // }

                    tempString += System.lineSeparator();
                }
                i++;
                lapNo++;
            }
            tempString += lapEndSum2String(fitFile.getActiveAvgCad(), fitFile.getActiveAvgSpeed(), fitFile.getActiveAvgPower(), fitFile.getActiveDist());

            tempString += "---- REST LAPS ----" + System.lineSeparator();
            i = 0;
            lapNo = 1;
            for (Mesg mesg : fitFile.getLapMesg()) {

                Short intensityVal = (Short) mesg.getFieldValue(FitFile.LAP_INTENSITY);
                String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

                if ("REST".equals(intensity) || "RECOVERY".equals(intensity)) {
                    tempString += "Lap" + lapNo;

                    // --- lapExtra commented out ---
                    // if (lapExtraRecords.get(i).level != null && !isSkiErgFile()) {
                    //     if (isTreadmillFile()) {
                    //         tempString += " " + lapExtraRecords.get(i).level.intValue() + "%";
                    //     } else {
                    //         tempString += " lv" + lapExtraRecords.get(i).level.intValue();
                    //     }
                    // }

                    tempString += " HRst?";
                    Short hrMax = mesg.getFieldShortValue(FitFile.LAP_MHR);
                    if (hrMax != null) {
                        tempString += "->max" + hrMax;
                    }

                    Float timer = mesg.getFieldFloatValue(FitFile.LAP_TIMER);
                    if (timer != null) {
                        tempString += " " + PehoUtils.sec2minSecShort(timer) + "min";
                    }

                    Short cadence = mesg.getFieldShortValue(FitFile.LAP_CAD);
                    if (cadence != null) {
                        tempString += " " + cadence + "spm";
                    }

                    Float speed = mesg.getFieldFloatValue(FitFile.LAP_ESPEED);
                    if (speed != null) {
                        if (fitFile.isSkiErgFile()) {
                            tempString += " " + PehoUtils.sec2minSecLong(500 / speed) + "min/500m";
                        } else {
                            tempString += " " + PehoUtils.sec2minSecLong(1000 / speed) + "min/km";
                            tempString += " " + String.format("%.1fkm/h", speed * 3.60);
                        }
                    }

                    Integer power = mesg.getFieldIntegerValue(FitFile.LAP_POW);
                    if (power != null) {
                        tempString += " " + power + "W";
                    }

                    Float dist = mesg.getFieldFloatValue(FitFile.LAP_DIST);
                    if (dist != null) {
                        tempString += " " + String.format("%.1fkm", dist / 1000);
                    }

                    tempString += System.lineSeparator();
                }
                i++;
                lapNo++;
            }
            tempString += lapEndSum2String(fitFile.getRestAvgCad(), fitFile.getRestAvgSpeed(), fitFile.getRestAvgPower(), fitFile.getRestDist());
        }
        catch (FitRuntimeException e) {
            System.out.println("LAP ERROR!!!!");
        }
        return tempString;
    }
}