package se.peho.fittools.core;

import com.garmin.fit.Intensity;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;
import com.garmin.fit.FitRuntimeException;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

public class LapReportGenerator {
    private final FitFile fitFile;

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public LapReportGenerator(FitFile fitFile) {
        this.fitFile = fitFile;
        fitFile.fillLapExtraRecords();
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapReport1() {
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
                Long timerTime = fitFile.findTimerBasedOnTime(startTime);
                if (startTime != null && timerTime != null) lapTimerStr = PehoUtils.sec2minSecLong(timerTime);

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

                System.out.printf("%-3d %-3s %6s %-7s %6s %-4s %-5s %-6s %-3s %-8s%n"
                , lapNo
                , lapIxStr
                , lapTimerStr
                , startTimeStr
                , lapTimeStr
                , lapDistStr
                , speedKmhStr
                , paceStr
                , lapCadStr
                , intensityStr);
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

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapReport1AllMesg() {
        try {
            Mesg firstLapMesg = null;
            for (Mesg mesg : fitFile.getAllMesg()) {
                if (mesg.getNum() == MesgNum.LAP) {
                    firstLapMesg = mesg;
                    break;
                }
            }
            if (firstLapMesg == null) throw new FitRuntimeException("No lap message in allMesg");

            System.out.println();
            System.out.println("===================================================================");
            System.out.println("LAPS IN FILE (lap1)");
            System.out.println("Start datetime: " + FitDateTime.toString(firstLapMesg.getFieldLongValue(FitFile.LAP_STIME), fitFile.getDiffMinutesLocalUTC()));
            System.out.println("-------------------------------------------------------------------");
            System.out.println("No  Ix   Start           Time  Dist Speed Pace   Cad Intensity");
            System.out.println("         timer  clock          km   km/h  min/km ");
            System.out.println("--- --- ------ --------  ----- ---- ----- ------ --- --------");
            int lapNo = 1;
            for (Mesg mesg : fitFile.getAllMesg()) {
                if (mesg.getNum() != MesgNum.LAP) continue;

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

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapRecord(int ix) {
        Mesg lapRecord = fitFile.getLapMesg().get(ix);

        // Level from extra records
        if (fitFile.getLapExtraRecords().get(ix).getLevel() != null) {
            if (fitFile.getLapExtraRecords().get(ix).getLevel() != null) {
                if (fitFile.getMySport() == FitFile.MySport.TREADMILL) {
                    System.out.print(" " + fitFile.getLapExtraRecords().get(ix).getLevel().intValue() + "%");
                } else 
                if (fitFile.getMySport() == FitFile.MySport.ELLIPTICAL) {
                    System.out.print(" lv" + fitFile.getLapExtraRecords().get(ix).getLevel().intValue());
                }
            }
        }

        // Timer
        Float totalTimer = lapRecord.getFieldFloatValue(FitFile.LAP_TIMER);
        if (totalTimer != null) System.out.print(" " + PehoUtils.sec2minSecShort(totalTimer) + "min");

        // Distance
        Float lapDist = lapRecord.getFieldFloatValue(FitFile.LAP_DIST);
        if (lapDist != null) System.out.print(" " + PehoUtils.m2km2(lapDist) + "km");

        // DistFrom / DistTo from secRecords
        System.out.print(" DistFrom:" 
            + PehoUtils.m2km2(fitFile.getRecordMesg().get(fitFile.getLapExtraRecords().get(ix).getRecordIxStart())
            .getFieldFloatValue(FitFile.REC_DIST)));
        System.out.print(" DistTo:" 
            + PehoUtils.m2km2(fitFile.getRecordMesg().get(fitFile.getLapExtraRecords().get(ix).getRecordIxEnd())
            .getFieldFloatValue(FitFile.REC_DIST)));

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
            System.out.print(" WktInt:" + intensityLabel);
        }

        // Workout Step Index
        Integer wktStepIx = lapRecord.getFieldIntegerValue(FitFile.LAP_WKT_STEP_IDX);
        if (wktStepIx != null) System.out.print(" WktStepIx:" + wktStepIx);

        // Start Time
        Long startTime = lapRecord.getFieldLongValue(FitFile.LAP_STIME);
        if (startTime != null) {
            System.out.print(" start@"
                 + PehoUtils.sec2minSecLong(fitFile.findTimerBasedOnTime(startTime))
                 + ", "
                 + FitDateTime.toStringTime(startTime, fitFile.getDiffMinutesLocalUTC()));
        }

        // Start Timer WRONG VALUE!!!!!
        Long startTimer = lapRecord.getFieldLongValue(FitFile.LAP_TIMER);
        if (startTimer != null) {
            //System.out.print(" " + FitDateTime.toTimerString(startTimer));
        }

        // Timestamp
        // Long timestamp = mesg.getFieldLongValue(LAP_TIME);
        // if (timestamp != null) {
        //     System.out.print(" Timestamp: " + FitDateTime.toString(timestamp, diffMinutesLocalUTC));
        // }

        // Extra record fields
        if (fitFile.getLapExtraRecords().get(ix).getTimeEnd() != null) 
            System.out.print(" end@" 
                + FitDateTime.toStringTime(fitFile.getLapExtraRecords().get(ix).getTimeEnd(), fitFile.getDiffMinutesLocalUTC()));
        if (fitFile.getLapExtraRecords().get(ix).getStepLen() != null) 
            System.out.print(" StepLen:" + fitFile.getLapExtraRecords().get(ix).getStepLen());
        if (fitFile.getLapExtraRecords().get(ix).getAvgDragFactor() != null) 
            System.out.print(" DFavg:" + fitFile.getLapExtraRecords().get(ix).getAvgDragFactor());
        if (fitFile.getLapExtraRecords().get(ix).getMaxDragFactor() != null) 
            System.out.print(" DFmax:" + fitFile.getLapExtraRecords().get(ix).getMaxDragFactor());
        if (fitFile.getLapExtraRecords().get(ix).getAvgStrokeLen() != null) 
            System.out.print(" SLavg:" + fitFile.getLapExtraRecords().get(ix).getAvgStrokeLen());
        if (fitFile.getLapExtraRecords().get(ix).getMaxStrokeLen() != null) 
            System.out.print(" SLmax:" + fitFile.getLapExtraRecords().get(ix).getMaxStrokeLen());
        if (fitFile.getLapExtraRecords().get(ix).getHrStart() != 0) 
            System.out.print(" hrStart:" + fitFile.getLapExtraRecords().get(ix).getHrStart());
        if (fitFile.getLapExtraRecords().get(ix).getHrEnd() != 0) 
            System.out.print(" hrEnd:" + fitFile.getLapExtraRecords().get(ix).getHrEnd());
        if (fitFile.getLapExtraRecords().get(ix).getHrMin() != 0) 
            System.out.print(" hrMin:" + fitFile.getLapExtraRecords().get(ix).getHrMin());
        if (fitFile.getLapExtraRecords().get(ix).getRecordIxEnd() != 0) 
            System.out.print(" recordIxEnd:" + fitFile.getLapExtraRecords().get(ix).getRecordIxEnd());
       
        System.out.println();
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
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

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapAvgMaxSpeed(Float avgSpeed, Float maxSpeed) {
        if (avgSpeed != null) {
            if (fitFile.getMySport() == FitFile.MySport.SKIERG) {
                System.out.print("--Sp avg:" + PehoUtils.mps2minp500m(avgSpeed));
                System.out.print(" max:" + PehoUtils.mps2minp500m(maxSpeed));
            } else {
                System.out.print("--Sp avg:" + PehoUtils.mps2minpkm(avgSpeed));
                System.out.print(" max:" + PehoUtils.mps2minpkm(maxSpeed));
            }
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void printLapAvgSpeed(Float avgSpeed) {
        if (avgSpeed != null) {
            if (fitFile.getMySport() == FitFile.MySport.SKIERG) {
                System.out.print(" " + PehoUtils.mps2minp500m(avgSpeed) + "min/500m");
            } else {
                System.out.print(" " + PehoUtils.mps2minpkm(avgSpeed) + "min/km");
                System.out.print(" " + PehoUtils.mps2kmph1(avgSpeed) + "km/h");
            }
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String lapAvgSpeed(Float avgSpeed) {
        String tempString = "";
        if (avgSpeed != null) {
            if (fitFile.getMySport() == FitFile.MySport.SKIERG) {
                tempString += " " + PehoUtils.mps2minp500m(avgSpeed) + "min/500m";
            } else {
                tempString += " " + PehoUtils.mps2minpkm(avgSpeed) + "min/km";
                tempString += " " + PehoUtils.mps2kmph1(avgSpeed) + "km/h";
            }
        }
        return tempString;
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String lapEndSum2String(Float avgCad, Float avgSpeed, Float avgPower, Float dist) {
        String tempString = "";
        tempString += "avgCad: " + (int) Math.round(avgCad) + "spm";
        if (fitFile.getMySport() == FitFile.MySport.SKIERG) {
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

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
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

            
            // Extra fields: level and step length (skip if SkiErg)
            if (fitFile.getLapExtraRecords().get(i).getLevel() != null) {
                if (fitFile.getMySport() == FitFile.MySport.TREADMILL) {
                    System.out.print(" " + fitFile.getLapExtraRecords().get(i).getLevel().intValue() + "%");
                } else 
                if (fitFile.getMySport() == FitFile.MySport.ELLIPTICAL) {
                    System.out.print(" lv" + fitFile.getLapExtraRecords().get(i).getLevel().intValue());
                }
            }

            if (fitFile.getLapExtraRecords().get(i).getStepLen() != null
                 && fitFile.getMySport() != FitFile.MySport.SKIERG) {
                System.out.print(" steplen" + (int) (fitFile.getLapExtraRecords().get(i).getStepLen() * 100) + "cm");
            }
           

            // Total timer
            Float totalTimer = mesg.getFieldFloatValue(FitFile.LAP_TIMER);
            if (totalTimer != null) System.out.print(" LapTime: " + totalTimer);

            // Intensity
            Short intensityVal = (Short) mesg.getFieldValue(FitFile.LAP_INTENSITY);
            String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";

            System.out.print(" WktIntensity: " + intensity);

            
            // Heart rate logic
            Integer maxHr = mesg.getFieldIntegerValue(FitFile.LAP_MHR);
            if ("ACTIVE".equals(intensity) || "WARMUP".equals(intensity)) {
                System.out.print(" HR start:" + fitFile.getLapExtraRecords().get(i).getHrStart());
                System.out.print(" min:" + fitFile.getLapExtraRecords().get(i).getHrMin());
                System.out.print("+" + ((maxHr != null ? maxHr : 0) - fitFile.getLapExtraRecords().get(i).getHrMin()));
                System.out.print("-->max:" + (maxHr != null ? maxHr : "N/A"));
                System.out.print(" end:" + fitFile.getLapExtraRecords().get(i).getHrEnd());
            } else {
                System.out.print(" HR start:" + fitFile.getLapExtraRecords().get(i).getHrStart());
                System.out.print(" max:" + (maxHr != null ? maxHr : "N/A"));
                System.out.print("" + (fitFile.getLapExtraRecords().get(i).getHrMin() - (maxHr != null ? maxHr : 0)));
                System.out.print("-->min:" + fitFile.getLapExtraRecords().get(i).getHrMin());
                System.out.print(" end:" + fitFile.getLapExtraRecords().get(i).getHrEnd());
            }
           

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

            
            // Extra lap info: Drag Factor and Stroke Length
            if (fitFile.getLapExtraRecords().get(i).getAvgDragFactor() != null) {
                System.out.print("--DFavg:" + (int) Math.round(fitFile.getLapExtraRecords().get(i).getAvgDragFactor()));
                System.out.print(" max:" + (int) Math.round(fitFile.getLapExtraRecords().get(i).getMaxDragFactor()));
            }
            if (fitFile.getLapExtraRecords().get(i).getAvgStrokeLen() != null) {
                System.out.print("--SLavg:" + fitFile.getLapExtraRecords().get(i).getAvgStrokeLen());
                System.out.print(" max:" + fitFile.getLapExtraRecords().get(i).getMaxStrokeLen());
            }
           

            System.out.println();
            i++;
            lapNo++;
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
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

                if (fitFile.getLapExtraRecords().get(i).getLevel() != null) {
                    if (fitFile.getMySport() == FitFile.MySport.TREADMILL) {
                        System.out.print(" " + fitFile.getLapExtraRecords().get(i).getLevel().intValue() + "%");
                    } else 
                    if (fitFile.getMySport() == FitFile.MySport.ELLIPTICAL) {
                        System.out.print(" lv" + fitFile.getLapExtraRecords().get(i).getLevel().intValue());
                    }
                }

                Float totalTimer = mesg.getFieldFloatValue(FitFile.LAP_TIMER);
                if (totalTimer != null) {
                    System.out.print(" LapTime: " + PehoUtils.sec2minSecShort(totalTimer));
                }

                
                System.out.print(" HR start:" + fitFile.getLapExtraRecords().get(i).getHrStart());
                if (i > 0) {
                    System.out.print(" HRmin" + fitFile.getLapExtraRecords().get(i - 1).getHrMin());
                } else {
                    System.out.print(" HR");
                }
                System.out.print(" min:" + fitFile.getLapExtraRecords().get(i).getHrMin());
                System.out.print("+" + (mesg.getFieldIntegerValue(FitFile.LAP_MHR) - fitFile.getLapExtraRecords().get(i).getHrMin()));
                System.out.print("-->max:" + mesg.getFieldIntegerValue(FitFile.LAP_MHR));
                System.out.print(" end:" + fitFile.getLapExtraRecords().get(i).getHrEnd());
               

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

                
                if (fitFile.getLapExtraRecords().get(i).getAvgDragFactor() != null) {
                    System.out.print("--DFavg:" + (int) Math.round(fitFile.getLapExtraRecords().get(i).getAvgDragFactor()));
                    System.out.print(" max:" + (int) Math.round(fitFile.getLapExtraRecords().get(i).getMaxDragFactor()));
                }
                if (fitFile.getLapExtraRecords().get(i).getAvgStrokeLen() != null) {
                    System.out.print("--SLavg:" + fitFile.getLapExtraRecords().get(i).getAvgStrokeLen());
                    System.out.print(" max:" + fitFile.getLapExtraRecords().get(i).getMaxStrokeLen());
                }
               

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

                if (fitFile.getLapExtraRecords().get(i).getLevel() != null) {
                    if (fitFile.getMySport() == FitFile.MySport.TREADMILL) {
                        System.out.print(" " + fitFile.getLapExtraRecords().get(i).getLevel().intValue() + "%");
                    } else 
                    if (fitFile.getMySport() == FitFile.MySport.ELLIPTICAL) {
                        System.out.print(" lv" + fitFile.getLapExtraRecords().get(i).getLevel().intValue());
                    }
                }

                Float totalTimer = mesg.getFieldFloatValue(FitFile.LAP_TIMER);
                if (totalTimer != null) {
                    System.out.print(" LapTime: " + PehoUtils.sec2minSecShort(totalTimer));
                }

                
                System.out.print(" HR start:" + fitFile.getLapExtraRecords().get(i).getHrStart());
                System.out.print(" max:" + mesg.getFieldIntegerValue(fitFile.LAP_MHR));
                System.out.print("" + (fitFile.getLapExtraRecords().get(i).getHrMin() - mesg.getFieldIntegerValue(fitFile.LAP_MHR)));
                System.out.print("-->min:" + fitFile.getLapExtraRecords().get(i).getHrMin());
                System.out.print(" end:" + fitFile.getLapExtraRecords().get(i).getHrEnd());
               

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

                
                if (fitFile.getLapExtraRecords().get(i).getAvgDragFactor() != null) {
                    System.out.print("--DFavg:" + (int) Math.round(fitFile.getLapExtraRecords().get(i).getAvgDragFactor()));
                    System.out.print(" max:" + (int) Math.round(fitFile.getLapExtraRecords().get(i).getMaxDragFactor()));
                }
                if (fitFile.getLapExtraRecords().get(i).getAvgStrokeLen() != null) {
                    System.out.print("--SLavg:" + fitFile.getLapExtraRecords().get(i).getAvgStrokeLen());
                    System.out.print(" max:" + fitFile.getLapExtraRecords().get(i).getMaxStrokeLen());
                }
               

                System.out.println();
            }
            i++;
            lapNo++;
        }
    }


    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String printActiveRestLapSummery() {
        return printActiveRestLapSummeryWithPrintOption(true);
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public String printActiveRestLapSummeryWithPrintOption(Boolean printToConsoleAndSaveToLog) {
        fitFile.clearTempUpdateLogg();

        try {
            fitFile.appendTempUpdateLoggLn("---- ACTIVE LAPS ----");
            int i = 0;
            int lapNo = 1;
            for (Mesg record : fitFile.getLapMesg()) { // Generic Mesg type
                Short intensityVal = record.getFieldShortValue(fitFile.LAP_INTENSITY);
                String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "";

                if ("ACTIVE".equals(intensity)) {
                    fitFile.appendTempUpdateLogg("Lap" + lapNo);

                    if (fitFile.getLapExtraRecords().get(i).getLevel() != null) {
                        if (fitFile.getMySport() == FitFile.MySport.TREADMILL) {
                            fitFile.appendTempUpdateLogg(" " + fitFile.getLapExtraRecords().get(i).getLevel().intValue() + "%");
                        } else 
                        if (fitFile.getMySport() == FitFile.MySport.ELLIPTICAL) {
                            fitFile.appendTempUpdateLogg(" lv" + fitFile.getLapExtraRecords().get(i).getLevel().intValue());
                        }
                    }

                    Short hrMin = 0;
                    if (i > 0) {
                        hrMin = fitFile.getLapExtraRecords().get(i - 1).getHrMin();
                        fitFile.appendTempUpdateLogg(" HRmin" + hrMin);
                    } else {
                        fitFile.appendTempUpdateLogg(" HR");
                    }

                    Short hrStart = fitFile.getLapExtraRecords().get(i).getHrStart();
                    fitFile.appendTempUpdateLogg(">st" + hrStart);
                    if ((hrStart - hrMin) > 20) {
                        hrMin = hrStart;;
                    }

                    Short maxHr = record.getFieldShortValue(fitFile.LAP_MHR);
                    if (maxHr != null) {
                        fitFile.appendTempUpdateLogg("+" + (maxHr - hrMin));
                        fitFile.appendTempUpdateLogg("->max" + maxHr);
                    }

                    fitFile.appendTempUpdateLogg(" end" + fitFile.getLapExtraRecords().get(i).getHrEnd());

                    Float totalTime = record.getFieldFloatValue(fitFile.LAP_TIMER);
                    if (totalTime != null) {
                        fitFile.appendTempUpdateLogg(" " + PehoUtils.sec2minSecShort(totalTime) + "min");
                    }

                    Short avgCad = record.getFieldShortValue(fitFile.LAP_CAD);
                    if (avgCad != null) {
                        fitFile.appendTempUpdateLogg(" " + avgCad + "spm");
                    }

                    Float avgSpeed = record.getFieldFloatValue(fitFile.LAP_ESPEED);
                    if (avgSpeed != null) {
                        if (fitFile.getMySport() == FitFile.MySport.SKIERG) {
                            fitFile.appendTempUpdateLogg(" " + PehoUtils.sec2minSecLong(500 / avgSpeed) + "min/500m");
                        } else {
                            fitFile.appendTempUpdateLogg(" " + PehoUtils.sec2minSecLong(1000 / avgSpeed) + "min/km");
                            fitFile.appendTempUpdateLogg(" " + String.format("%.1fkm/h", avgSpeed * 3.60));
                        }
                    }

                    Integer avgPower = record.getFieldIntegerValue(fitFile.LAP_POW);
                    if (avgPower != null) {
                        fitFile.appendTempUpdateLogg(" " + avgPower + "W");
                    }

                    Double dist = record.getFieldDoubleValue(fitFile.LAP_DIST);
                    if (dist != null) {
                        fitFile.appendTempUpdateLogg(" " + String.format("%.1fkm", dist / 1000));
                    }

                    if (fitFile.getLapExtraRecords().get(i).getAvgDragFactor() != null
                         && fitFile.getMySport() == FitFile.MySport.SKIERG) {
                        fitFile.appendTempUpdateLogg(" df" + Math.round(fitFile.getLapExtraRecords().get(i).getAvgDragFactor()));
                    }
                    if (fitFile.getLapExtraRecords().get(i).getAvgStrokeLen() != null
                     && fitFile.getMySport() == FitFile.MySport.SKIERG) {
                        fitFile.appendTempUpdateLogg(" sl" + fitFile.getLapExtraRecords().get(i).getAvgStrokeLen());
                    }
                    if (fitFile.getLapExtraRecords().get(i).getStepLen() != null
                     && fitFile.getMySport() != FitFile.MySport.SKIERG) {
                        fitFile.appendTempUpdateLogg(" step" + (int) (fitFile.getLapExtraRecords().get(i).getStepLen() * 100) + "cm");
                    }

                    fitFile.appendTempUpdateLoggLn("");
                }
                i++;
                lapNo++;
            }

            fitFile.appendTempUpdateLogg(
                lapEndSum2String(
                    fitFile.getActiveAvgCad(), 
                    fitFile.getActiveAvgSpeed(), 
                    fitFile.getActiveAvgPower(), 
                    fitFile.getActiveDist()
                )
            );

            // ================= REST LAPS =================
            fitFile.appendTempUpdateLoggLn("---- REST LAPS ----");
            i = 0;
            lapNo = 1;
            for (Mesg record : fitFile.getLapMesg()) {
                Short intensityVal = record.getFieldShortValue(fitFile.LAP_INTENSITY);
                String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "";

                if ("REST".equals(intensity) || "RECOVERY".equals(intensity)) {
                    fitFile.appendTempUpdateLogg("Lap" + lapNo);

                    if (fitFile.getLapExtraRecords().get(i).getLevel() != null) {
                        if (fitFile.getMySport() == FitFile.MySport.TREADMILL) {
                            fitFile.appendTempUpdateLogg(" " + fitFile.getLapExtraRecords().get(i).getLevel().intValue() + "%");
                        } else {
                            fitFile.appendTempUpdateLogg(" lv" + fitFile.getLapExtraRecords().get(i).getLevel().intValue());
                        }
                    }

                    fitFile.appendTempUpdateLogg(" HRst" + fitFile.getLapExtraRecords().get(i).getHrStart());

                    Integer maxHr = record.getFieldIntegerValue(fitFile.LAP_MHR);
                    if (maxHr != null) {
                        fitFile.appendTempUpdateLogg(">max" + maxHr);
                        fitFile.appendTempUpdateLogg("" + (fitFile.getLapExtraRecords().get(i).getHrMin() - maxHr));
                        fitFile.appendTempUpdateLogg("->min" + fitFile.getLapExtraRecords().get(i).getHrMin());
                    }

                    fitFile.appendTempUpdateLogg(" end" + fitFile.getLapExtraRecords().get(i).getHrEnd());

                    Float totalTime = record.getFieldFloatValue(fitFile.LAP_TIMER);
                    if (totalTime != null) {
                        fitFile.appendTempUpdateLogg(" " + PehoUtils.sec2minSecShort(totalTime) + "min");
                    }

                    Short avgCad = record.getFieldShortValue(fitFile.LAP_CAD);
                    if (avgCad != null) {
                        fitFile.appendTempUpdateLogg(" " + avgCad + "spm");
                    }

                    Float avgSpeed = record.getFieldFloatValue(fitFile.LAP_ESPEED);
                    if (avgSpeed != null) {
                        if (fitFile.getMySport() == FitFile.MySport.SKIERG) {
                            fitFile.appendTempUpdateLogg(" " + PehoUtils.sec2minSecLong(500 / avgSpeed) + "min/500m");
                        } else {
                            fitFile.appendTempUpdateLogg(" " + PehoUtils.sec2minSecLong(1000 / avgSpeed) + "min/km");
                            fitFile.appendTempUpdateLogg(" " + String.format("%.1fkm/h", avgSpeed * 3.60));
                        }
                    }

                    Integer avgPower = record.getFieldIntegerValue(fitFile.LAP_POW);
                    if (avgPower != null) {
                        fitFile.appendTempUpdateLogg(" " + avgPower + "W");
                    }

                    Double dist = record.getFieldDoubleValue(fitFile.LAP_DIST);
                    if (dist != null) {
                        fitFile.appendTempUpdateLogg(" " + String.format("%.1fkm", dist / 1000));
                    }

                    if (fitFile.getLapExtraRecords().get(i).getStepLen() != null
                     && fitFile.getMySport() != FitFile.MySport.SKIERG) {
                        fitFile.appendTempUpdateLogg(" step" + (int) (fitFile.getLapExtraRecords().get(i).getStepLen() * 100) + "cm");
                    }

                    fitFile.appendTempUpdateLoggLn("");
                }
                i++;
                lapNo++;
            }

            fitFile.appendTempUpdateLogg(
                lapEndSum2String(
                    fitFile.getRestAvgCad(), 
                    fitFile.getRestAvgSpeed(), 
                    fitFile.getRestAvgPower(), 
                    fitFile.getRestDist()));

                    //asasas

        } catch (FitRuntimeException e) {
            System.out.println("LAP ERROR!!!!");
        }
        if (printToConsoleAndSaveToLog) {
            fitFile.appendUpdateLogg(fitFile.getTempUpdateLogg());
            System.out.println(fitFile.getTempUpdateLogg());
        }
        return fitFile.getTempUpdateLogg();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Debug method to print lap and record details for verification
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        public void debugLapRecords(List<Mesg> lapMesgs, List<Mesg> recordMesgs) {
        System.out.println("-------------------------------------------");
        System.out.println("----- L A P   R E C O R D   D E B U G -----");
        System.out.printf("Laps: %d  Records: %d%n%n", lapMesgs.size(), recordMesgs.size());

        for (int i = 0; i < lapMesgs.size(); i++) {
            Mesg lap = lapMesgs.get(i);

            // --- basic lap info
            Long startTimeL = getLongField(lap, "start_time", null);
            Long timestampL = getLongField(lap, "timestamp", null);
            if (startTimeL == null) startTimeL = timestampL;
            if (startTimeL == null) {
                System.out.printf("%n---- LAP %d ---- (no start_time, skipping)%n", i + 1);
                continue;
            }

            Float totalElapsed = getFloatField(lap, "total_elapsed_time", null);
            Float totalTimer = getFloatField(lap, "total_timer_time", null);

            Long endTimeL = null;
            if (totalElapsed != null && totalElapsed > 0f) {
                endTimeL = startTimeL + Math.round(totalElapsed);
            } else if (totalTimer != null && totalTimer > 0f) {
                endTimeL = startTimeL + Math.round(totalTimer);
            } else if (i + 1 < lapMesgs.size()) {
                Long nextStart = getLongField(lapMesgs.get(i + 1), "start_time", null);
                if (nextStart == null)
                    nextStart = getLongField(lapMesgs.get(i + 1), "timestamp", null);
                if (nextStart != null && nextStart > startTimeL) endTimeL = nextStart;
            }

            // fallback: find last record after lap start
            if (endTimeL == null) {
                Long lastAfter = null;
                for (Mesg r : recordMesgs) {
                    Long rts = getLongField(r, "timestamp", null);
                    if (rts != null && rts >= startTimeL) lastAfter = rts;
                }
                if (lastAfter != null && lastAfter > startTimeL) endTimeL = lastAfter;
            }

            if (endTimeL == null) endTimeL = startTimeL + 1;
            endTimeL++; // +1s inclusive

            long lapStart = startTimeL;
            long lapEnd = endTimeL;

            double lapDist = getFloatField(lap, "total_distance", 0f);
            double lapAvgSpd = getFloatField(lap, "avg_speed", 0f);

            long messageIndexL = getLongField(lap, "message_index", 0L);
            long eventL = getLongField(lap, "event", 0L);
            long eventTypeL = getLongField(lap, "event_type", 0L);
            long lapTriggerL = getLongField(lap, "lap_trigger", 0L);

            System.out.printf("---- LAP %d ----%n", i + 1);
            System.out.printf(
                "Start: %d  End: %d  Dur: %.1fs  LapMesg Dist: %.2fm  LapMesg AvgSpd: %.3f m/s%n",
                lapStart, lapEnd, (double) (lapEnd - lapStart), lapDist, lapAvgSpd);
            System.out.printf(
                "message_index=%d  event=%d  event_type=%d  lap_trigger=%d%n",
                messageIndexL, eventL, eventTypeL, lapTriggerL);

            // --- Records immediately after lap start
            System.out.println("10 Records after lap start:");
            int count = 0;
            for (Mesg r : recordMesgs) {
                Long ts = getLongField(r, "timestamp", null);
                if (ts != null && ts >= lapStart && ts < lapStart + 10) {
                    printRecord(r, lapStart);
                    count++;
                    if (count >= 10) break;
                }
            }

            // --- Records immediately before lap end
            System.out.println("10 Records before lap end:");
            count = 0;
            for (Mesg r : recordMesgs) {
                Long ts = getLongField(r, "timestamp", null);
                if (ts != null && ts >= lapEnd - 10 && ts <= lapEnd) {
                    printRecord(r, lapStart);
                    count++;
                    if (count >= 10) break;
                }
            }

            // --- Compute totals from records within the lap
            List<Mesg> lapRecords = new ArrayList<>();
            for (Mesg r : recordMesgs) {
                Long ts = getLongField(r, "timestamp", null);
                if (ts != null && ts >= lapStart && ts <= lapEnd) {
                    lapRecords.add(r);
                }
            }

            double firstDist = lapRecords.isEmpty() ? 0.0 : getFloatField(lapRecords.get(0), "distance", 0f);
            double lastDist = lapRecords.isEmpty() ? 0.0 : getFloatField(lapRecords.get(lapRecords.size() - 1), "distance", 0f);
            double distDelta = lastDist - firstDist;
            double timeDelta = (double) (lapEnd - lapStart);
            double avgSpeed = timeDelta > 0 ? distDelta / timeDelta : 0.0;

            System.out.printf(
                "=> Records total: Dist=%.2fm  Time=%.1fs  AvgSpeed=%.3f m/s%n",
                distDelta, timeDelta, avgSpeed);
            System.out.printf(
                "Compare LapMesg vs Records: LapMesgDist=%.2f  RecordsDist=%.2f  LapMesgAvgSpd=%.3f  RecAvgSpd=%.3f%n%n",
                lapDist, distDelta, lapAvgSpd, avgSpeed);
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private static void printRecord(Mesg r, long lapStart) {
        Long ts = getLongField(r, "timestamp", null);
        double dist = getFloatField(r, "distance", 0f);
        double spd = getFloatField(r, "speed", 0f);
        double enhSpd = getFloatField(r, "enhanced_speed", 0f);
        if (ts == null) return;
        System.out.printf(
            "  ?t=%6ds  ts=%d  dist=%.2f  spd=%.3f  enhSpd=%.3f%n",
            (ts - lapStart), ts, dist, spd, enhSpd);
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // safe field helpers
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private static Long getLongField(Mesg m, String name, Long defVal) {
        if (m == null) return defVal;
        try {
            Long v = m.getFieldLongValue(name);
            return v != null ? v : defVal;
        } catch (Exception e) { return defVal; }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private static Float getFloatField(Mesg m, String name, Float defVal) {
        if (m == null) return defVal;
        try {
            Float v = m.getFieldFloatValue(name);
            return v != null ? v : defVal;
        } catch (Exception e) { return defVal; }
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
}