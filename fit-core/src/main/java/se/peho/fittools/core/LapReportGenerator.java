package se.peho.fittools.core;

import com.garmin.fit.Intensity;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;
import com.garmin.fit.FitRuntimeException;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

import se.peho.fittools.core.FitFile.LapExtraMesg;
import se.peho.fittools.core.strings.*;

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
                Short intensity = mesg.getFieldShortValue(FitFile.LAP_INTENSITY);
                intensityStr = formatIntensityForLap1(intensity);

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
    public void printLapReportFromAllMesg() {
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
                Short intensity = mesg.getFieldShortValue(FitFile.LAP_INTENSITY);
                intensityStr = formatIntensityForLap1(intensity);

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

    private String formatIntensityForLap1(Short intensityRaw) {
        if (intensityRaw == null) {
            return "-";
        }
        Intensity intensityEnum = Intensity.getByValue(intensityRaw);
        if (intensityEnum == null || intensityEnum == Intensity.INVALID) {
            return "-";
        }
        return Intensity.getStringFromValue(intensityEnum);
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
        // lap4 command
        int i = 0;
        
        int colSum = 0;
        List<String> headerFormat = new ArrayList<>();
        List<String> header1Values = new ArrayList<>();
        List<String> header2Values = new ArrayList<>();
        int colLapNo = 3;
        colSum += colLapNo;
        headerFormat.add("%" + colLapNo + "s");
        header1Values.add("Lap");
        header2Values.add("no");

        int colTimeStartCalc = 9;
        colSum += colTimeStartCalc;
        headerFormat.add("%" + colTimeStartCalc + "s");
        header1Values.add("Time");
        header2Values.add("calc");
        int colTimeStart = 9;
        colSum += colTimeStart;
        headerFormat.add("%" + colTimeStart + "s");
        header1Values.add("");
        header2Values.add("1st");
        int colTimeEnd = 9;
        colSum += colTimeEnd;
        headerFormat.add("%" + colTimeEnd + "s");
        header1Values.add("");
        header2Values.add("last");
        int colETimerLap = 7;
        colSum += colETimerLap;
        headerFormat.add("%" + colETimerLap + "s");
        header1Values.add("eTime");
        header2Values.add("lap");

        int colTTimerStartCalc = 7;
        colSum += colTTimerStartCalc;
        headerFormat.add("%" + colTTimerStartCalc + "s");
        header1Values.add("tTimer");
        header2Values.add("calc");
        int colTTimerStart = 6;
        colSum += colTTimerStart;
        headerFormat.add("%" + colTTimerStart + "s");
        header1Values.add("");
        header2Values.add("1st");
        int colTTimerEnd = 6;
        colSum += colTTimerEnd;
        headerFormat.add("%" + colTTimerEnd + "s");
        header1Values.add("");
        header2Values.add("last");
        int colTTimerLap = 7;
        colSum += colTTimerLap;
        headerFormat.add("%" + colTTimerLap + "s");
        header1Values.add("");
        header2Values.add("lap");
        int colDistStartCalc = 8;
        colSum += colDistStartCalc;
        headerFormat.add("%" + colDistStartCalc + "s");
        header1Values.add("Dist");
        header2Values.add("calc");
        int colDistStart = 8;
        colSum += colDistStart;
        headerFormat.add("%" + colDistStart + "s");
        header1Values.add("");
        header2Values.add("1st");
        int colDistEnd = 8;
        colSum += colDistEnd;
        headerFormat.add("%" + colDistEnd + "s");
        header1Values.add("");
        header2Values.add("last");
        int colDistLap = 7;
        colSum += colDistLap;
        headerFormat.add("%" + colDistLap + "s");
        header1Values.add("");
        header2Values.add("lap");

        int colSpeedMax = 6;
        colSum += colSpeedMax;
        headerFormat.add("%" + colSpeedMax + "s");
        header1Values.add("Speed");
        header2Values.add("max");
        int colSpeedMin = 6;
        colSum += colSpeedMin;
        headerFormat.add("%" + colSpeedMin + "s");
        header1Values.add("");
        header2Values.add("min");
        int colSpeedAvg = 6;
        colSum += colSpeedAvg;
        headerFormat.add("%" + colSpeedAvg + "s");
        header1Values.add("");
        header2Values.add("avg");
        int colSpeedEnhancedUsed = 5;
        colSum += colSpeedEnhancedUsed;
        headerFormat.add("%" + colSpeedEnhancedUsed + "s");
        header1Values.add("");
        header2Values.add("enh");

        // Second row
        // int colTTimerFile = 7;
        // colSum += colTTimerFile;
        // headerFormat.add("%" + colTTimerFile + "s");
        // header1Values.add("");
        // header2Values.add("file");

        // int colDistFile = 7;
        // colSum += colDistFile;
        // headerFormat.add("%" + colDistFile + "s");
        // header1Values.add("");
        // header2Values.add("file");

        String headerFormatStr = "";
        for (String fmt : headerFormat) {
            headerFormatStr += fmt;
        }
        headerFormatStr += "%n";

        System.out.println();
        System.out.println("-".repeat(colSum));
        System.out.println("--- LAPS IN FILE - LapExtraDebug (lap4-LapAllSummary) ---");
        System.out.println("-".repeat(colSum));
        System.out.printf("Laps: %d  LapsExtras: %d  Records: %d%n%n", fitFile.getLapMesg().size(), fitFile.getLapExtraRecords().size(), fitFile.getRecordMesg().size());
        
        System.out.printf(headerFormatStr, header1Values.toArray());
        System.out.printf(headerFormatStr, header2Values.toArray());

        System.out.println("-".repeat(colSum));

        for (Mesg mesg : fitFile.getLapMesg()) {
            LapExtraMesg lapExtra = fitFile.getLapExtraRecords().get(i);

            Integer lapNo = lapExtra.getLapNo();
            String lapNoStr = lapNo != null ? String.format("%d", lapNo) : "-";
            System.out.printf("%" + colLapNo + "s", lapNoStr);

            // Time
            Long startTimeCalc = lapExtra.getTimeStartCalc();
            String startTimeCalcStr = startTimeCalc != null ? new Tstr(startTimeCalc, fitFile.getDiffMinutesLocalUTC()).get() : "-";
            System.out.printf("%" + colTimeStartCalc + "s", startTimeCalcStr);

            Long startTime = mesg.getFieldLongValue(FitFile.LAP_STIME);
            String startTimeStr = startTime != null ? new Tstr(startTime, fitFile.getDiffMinutesLocalUTC()).get() : "-";
            System.out.printf("%" + colTimeStart + "s", startTimeStr);
            
            Long endTime = lapExtra.getTimeEnd();
            String endTimeStr = endTime != null ? new Tstr(endTime, fitFile.getDiffMinutesLocalUTC()).get() : "-";
            System.out.printf("%" + colTimeEnd + "s", endTimeStr);

            Float lapETimer = lapExtra.getETimerLap();
            String lapETimerStr = lapETimer != null ? String.format("%.1f", lapETimer) : "-";
            System.out.printf("%" + colETimerLap + "s", lapETimerStr);

            // tTimer
            Float tTimerStartCalc = lapExtra.getTTimerStartCalc();
            String tTimerStartCalcStr = tTimerStartCalc != null ? String.format("%.0f", tTimerStartCalc) : "-";
            System.out.printf("%" + colTTimerStartCalc + "s", tTimerStartCalcStr);

            Float tTimerStart = lapExtra.getTTimerStart();
            String tTimerStartStr = tTimerStart != null ? String.format("%.0f", tTimerStart) : "-";
            System.out.printf("%" + colTTimerStart + "s", tTimerStartStr);

            Float tTimerEnd = lapExtra.getTTimerEnd();
            String tTimerEndStr = tTimerEnd != null ? String.format("%.0f", tTimerEnd) : "-";
            System.out.printf("%" + colTTimerEnd + "s", tTimerEndStr);

            Float tTimerLap = lapExtra.getTTimerLap();
            String tTimerLapStr = tTimerLap != null ? String.format("%.1f", tTimerLap) : "-";
            System.out.printf("%" + colTTimerLap + "s", tTimerLapStr);

            // Distance
            Float distStartCalc = lapExtra.getDistStartCalc();
            String distStartCalcStr = distStartCalc != null ? String.format("%.1f", distStartCalc) : "-";
            System.out.printf("%" + colDistStartCalc + "s", distStartCalcStr);

            Float distStart = lapExtra.getDistStart();
            String distStartStr = distStart != null ? String.format("%.1f", distStart) : "-";
            System.out.printf("%" + colDistStart + "s", distStartStr);

            Float distEnd = lapExtra.getDistEnd();
            String distEndStr = distEnd != null ? String.format("%.1f", distEnd) : "-";
            System.out.printf("%" + colDistEnd + "s", distEndStr);

            Float distLap = 0f;
            if (distEnd != null && distStartCalc != null) {
                distLap = distEnd - distStartCalc;
                String distLapStr = String.format("%.1f", distLap);
                System.out.printf("%" + colDistLap + "s", distLapStr);
            } else {
                System.out.printf("%" + colDistLap + "s", "-");
            }

            Float speedMax = lapExtra.getSpeedMax();
            String speedMaxStr = speedMax != null ? String.format("%.3f", speedMax) : "-";
            System.out.printf("%" + colSpeedMax + "s", speedMaxStr);

            Float speedMin = lapExtra.getSpeedMin();
            String speedMinStr = speedMin != null ? String.format("%.3f", speedMin) : "-";
            System.out.printf("%" + colSpeedMin + "s", speedMinStr);

            Float speedAvg = lapExtra.getSpeedAvg();
            String speedAvgStr = speedAvg != null ? String.format("%.3f", speedAvg) : "-";
            System.out.printf("%" + colSpeedAvg + "s", speedAvgStr);

            Boolean speedEnhancedUsed = lapExtra.getSpeedEnhancedUsed();
            String speedEnhancedUsedStr = speedEnhancedUsed != null ? (speedEnhancedUsed ? "yes" : "no") : "-";
            System.out.printf("%" + colSpeedEnhancedUsed + "s", speedEnhancedUsedStr);

            System.out.println();

            // SECOND ROW WITH FILE VALUES
            // ==============================
            System.out.printf("%" + colLapNo + "s", "");

            // Time
            System.out.printf("%" + colTimeStartCalc + "s", "file->");

            System.out.printf("%" + colTimeStart + "s", "");
            
            System.out.printf("%" + colTimeEnd + "s", "");

            Float eTimerFile = mesg.getFieldFloatValue(FitFile.LAP_ETIMER);
            String eTimerFileStr = eTimerFile != null ? String.format("%.1f", eTimerFile) : "-";
            System.out.printf("%" + colETimerLap + "s", eTimerFileStr);

            // tTimer
            System.out.printf("%" + colTTimerStartCalc + "s", "");

            System.out.printf("%" + colTTimerStart + "s", "");

            System.out.printf("%" + colTTimerEnd + "s", "");

            Float tTimerFile = mesg.getFieldFloatValue(FitFile.LAP_TIMER);
            String tTimerFileStr = tTimerFile != null ? String.format("%.1f", tTimerFile) : "-";
            System.out.printf("%" + colTTimerLap + "s", tTimerFileStr);

            // Distance
            System.out.printf("%" + colDistStartCalc + "s", "");

            System.out.printf("%" + colDistStart + "s", "");

            System.out.printf("%" + colDistEnd + "s", "");

            Float distLapFile = mesg.getFieldFloatValue(FitFile.LAP_DIST);
            String distLapFileStr = distLapFile != null ? String.format("%.1f", distLapFile) : "-";
            System.out.printf("%" + colDistLap + "s", distLapFileStr);

            Float speedMaxFile = null;
            Float speedAvgFile = null;
            if (lapExtra.getSpeedEnhancedUsed() != null && lapExtra.getSpeedEnhancedUsed()) {
                speedMaxFile = mesg.getFieldFloatValue(FitFile.LAP_EMSPEED);
                speedAvgFile = mesg.getFieldFloatValue(FitFile.LAP_ESPEED);

            } else {
                speedMaxFile = mesg.getFieldFloatValue(FitFile.LAP_MSPEED);
                speedAvgFile = mesg.getFieldFloatValue(FitFile.LAP_SPEED);
            }
            String speedMaxStrFile = speedMaxFile != null ? String.format("%.3f", speedMaxFile) : "-";
            System.out.printf("%" + colSpeedMax + "s", speedMaxStrFile);

            System.out.printf("%" + colSpeedMin + "s", "");

            String speedAvgStrFile = speedAvgFile != null ? String.format("%.3f", speedAvgFile) : "-";
            System.out.printf("%" + colSpeedAvg + "s", speedAvgStrFile);

            System.out.printf("%" + colSpeedEnhancedUsed + "s", speedEnhancedUsedStr);

            System.out.println();
            i++;
        }
        System.out.println("-".repeat(colSum));

        // =================================================================
        // NEXT TABLE
        i = 0;
        colSum = 0;
        headerFormat.clear();
        header1Values.clear();
        header2Values.clear();

        colLapNo = 3;
        colSum += colLapNo;
        headerFormat.add("%" + colLapNo + "s");
        header1Values.add("Lap");
        header2Values.add("no");

        int colHrStart = 4;
        colSum += colHrStart;
        headerFormat.add("%" + colHrStart + "s");
        header1Values.add("HR");
        header2Values.add("1st");
        int colHrEnd = 4;
        colSum += colHrEnd;
        headerFormat.add("%" + colHrEnd + "s");
        header1Values.add("");
        header2Values.add("end");
        int colHrMax = 4;
        colSum += colHrMax;
        headerFormat.add("%" + colHrMax + "s");
        header1Values.add("");
        header2Values.add("max");
        int colHrMin = 4;
        colSum += colHrMin;
        headerFormat.add("%" + colHrMin + "s");
        header1Values.add("");
        header2Values.add("min");
        int colHrAvg = 4;
        colSum += colHrAvg;
        headerFormat.add("%" + colHrAvg + "s");
        header1Values.add("");
        header2Values.add("avg");

        int colCadMax = 4;
        colSum += colCadMax;
        headerFormat.add("%" + colCadMax + "s");
        header1Values.add("Cad");
        header2Values.add("max");
        int colCadMin = 4;
        colSum += colCadMin;
        headerFormat.add("%" + colCadMin + "s");
        header1Values.add("");
        header2Values.add("min");
        int colCadAvg = 4;
        colSum += colCadAvg;
        headerFormat.add("%" + colCadAvg + "s");
        header1Values.add("");
        header2Values.add("avg");

        int colPowerMax = 4;
        colSum += colPowerMax;
        headerFormat.add("%" + colPowerMax + "s");
        header1Values.add("Pow");
        header2Values.add("max");
        int colPowerMin = 4;
        colSum += colPowerMin;
        headerFormat.add("%" + colPowerMin + "s");
        header1Values.add("");
        header2Values.add("min");
        int colPowerAvg = 4;
        colSum += colPowerAvg;
        headerFormat.add("%" + colPowerAvg + "s");
        header1Values.add("");
        header2Values.add("avg");

        int colStepLen = 5;
        colSum += colStepLen;
        headerFormat.add("%" + colStepLen + "s");
        header1Values.add("Step");
        header2Values.add("len");

        int colIntensity = 10;
        colSum += colIntensity;
        headerFormat.add("%" + colIntensity + "s");
        header1Values.add("Intensity");
        header2Values.add("");

        int colRecordIxStart = 6;
        colSum += colRecordIxStart;
        headerFormat.add("%" + colRecordIxStart + "s");
        header1Values.add("RecIx");
        header2Values.add("1st");
        int colRecordIxEnd = 6;
        colSum += colRecordIxEnd;
        headerFormat.add("%" + colRecordIxEnd + "s");
        header1Values.add("");
        header2Values.add("end");

        headerFormatStr = "";
        for (String fmt : headerFormat) {
            headerFormatStr += fmt;
        }
        headerFormatStr += "%n";

        System.out.printf(headerFormatStr, header1Values.toArray());
        System.out.printf(headerFormatStr, header2Values.toArray());

        System.out.println("-".repeat(colSum));

        for (Mesg mesg : fitFile.getLapMesg()) {
            LapExtraMesg lapExtra = fitFile.getLapExtraRecords().get(i);

            // Lap no
            Integer lapNo = fitFile.getLapExtraRecords().get(i).getLapNo();
            String lapNoStr = lapNo != null ? String.format("%d", lapNo) : "-";
            System.out.printf("%" + colLapNo + "s", lapNoStr);

            // HR
            Integer hrStart = fitFile.getLapExtraRecords().get(i).getHrStart();
            String hrStartStr = hrStart != null ? String.format("%d", hrStart) : "-";
            System.out.printf("%" + colHrStart + "s", hrStartStr);
            
            Integer hrEnd = fitFile.getLapExtraRecords().get(i).getHrEnd();
            String hrEndStr = hrEnd != null ? String.format("%d", hrEnd) : "-";
            System.out.printf("%" + colHrEnd + "s", hrEndStr);
            
            Integer hrMax = fitFile.getLapExtraRecords().get(i).getHrMax();
            String hrMaxStr = hrMax != null ? String.format("%d", hrMax) : "-";
            System.out.printf("%" + colHrMax + "s", hrMaxStr);
            
            Integer hrMin = fitFile.getLapExtraRecords().get(i).getHrMin();
            String hrMinStr = hrMin != null ? String.format("%d", hrMin) : "-";
            System.out.printf("%" + colHrMin + "s", hrMinStr);
            
            Integer hrAvg = fitFile.getLapExtraRecords().get(i).getHrAvg();
            String hrAvgStr = hrAvg != null ? String.format("%d", hrAvg) : "-";
            System.out.printf("%" + colHrAvg + "s", hrAvgStr);
            
            Integer cadMax = fitFile.getLapExtraRecords().get(i).getCadMax();
            String cadMaxStr = cadMax != null ? String.format("%d", cadMax) : "-";
            System.out.printf("%" + colCadMax + "s", cadMaxStr);
            
            Integer cadMin = fitFile.getLapExtraRecords().get(i).getCadMin();
            String cadMinStr = cadMin != null ? String.format("%d", cadMin) : "-";
            System.out.printf("%" + colCadMin + "s", cadMinStr);
            
            Integer cadAvg = fitFile.getLapExtraRecords().get(i).getCadAvg();
            String cadAvgStr = cadAvg != null ? String.format("%d", cadAvg) : "-";
            System.out.printf("%" + colCadAvg + "s", cadAvgStr);
            
            Integer powerMax = fitFile.getLapExtraRecords().get(i).getPowerMax();
            String powerMaxStr = powerMax != null ? String.format("%d", powerMax) : "-";
            System.out.printf("%" + colPowerMax + "s", powerMaxStr);
            
            Integer powerMin = fitFile.getLapExtraRecords().get(i).getPowerMin();
            String powerMinStr = powerMin != null ? String.format("%d", powerMin) : "-";
            System.out.printf("%" + colPowerMin + "s", powerMinStr);
            
            Integer powerAvg = fitFile.getLapExtraRecords().get(i).getPowerAvg();
            String powerAvgStr = powerAvg != null ? String.format("%d", powerAvg) : "-";
            System.out.printf("%" + colPowerAvg + "s", powerAvgStr);
            
            // StepLen
            Float stepLen = lapExtra.getStepLen(); // Convert from FFRT double step to Garmin single step
            String stepLenStr = stepLen != null ? String.format("%.2f", stepLen / 2) : "-";
            System.out.printf("%" + colStepLen + "s", stepLenStr);

            // Intensity only in file, not in LapExtra
            System.out.printf("%" + colIntensity + "s", "");

            // RecordIx
            Integer recordIxStart = lapExtra.getRecordIxStart();
            String recordIxStartStr = recordIxStart != null ? String.format("%d", recordIxStart) : "-";
            System.out.printf("%" + colRecordIxStart + "s", recordIxStartStr);

            Integer recordIxEnd = lapExtra.getRecordIxEnd();
            String recordIxEndStr = recordIxEnd != null ? String.format("%d", recordIxEnd) : "-";
            System.out.printf("%" + colRecordIxEnd + "s", recordIxEndStr);

            System.out.println();
            
            // SECOND ROW WITH FILE VALUES
            // ==============================
            System.out.printf("%" + colLapNo + "s", "");

            System.out.printf("%" + colHrStart + "s", "file");
            
            System.out.printf("%" + colHrEnd + "s", "->  ");
            
            Integer hrMaxFile = mesg.getFieldIntegerValue(FitFile.LAP_MHR);
            String hrMaxFileStr = hrMaxFile != null ? String.format("%d", hrMaxFile) : "-";
            System.out.printf("%" + colHrMax + "s", hrMaxFileStr);
            
            System.out.printf("%" + colHrMin + "s", "");
            
            Integer hrAvgFile = mesg.getFieldIntegerValue(FitFile.LAP_HR);
            String hrAvgFileStr = hrAvgFile != null ? String.format("%d", hrAvgFile) : "-";
            System.out.printf("%" + colHrAvg + "s", hrAvgFileStr);
            
            Integer cadMaxFile = mesg.getFieldIntegerValue(FitFile.LAP_MCAD);
            String cadMaxFileStr = cadMaxFile != null ? String.format("%d", cadMaxFile) : "-";
            System.out.printf("%" + colCadMax + "s", cadMaxFileStr);
            
            System.out.printf("%" + colCadMin + "s", "");
            
            Integer cadAvgFile = mesg.getFieldIntegerValue(FitFile.LAP_CAD);
            String cadAvgFileStr = cadAvgFile != null ? String.format("%d", cadAvgFile) : "-";
            System.out.printf("%" + colCadAvg + "s", cadAvgFileStr);
            
            Integer powerMaxFile = mesg.getFieldIntegerValue(FitFile.LAP_MPOW);
            String powerMaxFileStr = powerMaxFile != null ? String.format("%d", powerMaxFile) : "-";
            System.out.printf("%" + colPowerMax + "s", powerMaxFileStr);
            
            System.out.printf("%" + colPowerMin + "s", "");
            
            Integer powerAvgFile = mesg.getFieldIntegerValue(FitFile.LAP_POW);
            String powerAvgFileStr = powerAvgFile != null ? String.format("%d", powerAvgFile) : "-";
            System.out.printf("%" + colPowerAvg + "s", powerAvgFileStr);
            
            // StepLen
            Float stepLenFile = mesg.getFieldFloatValue(FitFile.LAP_STEP);
            String stepLenStrFile = stepLenFile != null ? String.format("%.2f", stepLenFile / 1000) : "-";
            System.out.printf("%" + colStepLen + "s", stepLenStrFile);

            // Intensity
            Short intensityVal = mesg.getFieldShortValue(FitFile.LAP_INTENSITY);
            String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";
            System.out.printf("%" + colIntensity + "s", intensity);

            // RecordIx
            System.out.printf("%" + colRecordIxStart + "s", "");

            System.out.printf("%" + colRecordIxEnd + "s", "");

            System.out.println();
            i++;
        }
        System.out.println("-".repeat(colSum));

        // =================================================================
        // NEXT TABLE
        i = 0;
        colSum = 0;
        headerFormat.clear();
        header1Values.clear();
        header2Values.clear();

        colLapNo = 3;
        colSum += colLapNo;
        headerFormat.add("%" + colLapNo + "s");
        header1Values.add("Lap");
        header2Values.add("no");

        int colAltStart = 4;
        colSum += colAltStart;
        headerFormat.add("%" + colAltStart + "s");
        header1Values.add("Alt");
        header2Values.add("1st");
        int colAltEnd = 4;
        colSum += colAltEnd;
        headerFormat.add("%" + colAltEnd + "s");
        header1Values.add("");
        header2Values.add("end");
        int colAltMax = 4;
        colSum += colAltMax;
        headerFormat.add("%" + colAltMax + "s");
        header1Values.add("");
        header2Values.add("max");
        int colAltMin = 4;
        colSum += colAltMin;
        headerFormat.add("%" + colAltMin + "s");
        header1Values.add("");
        header2Values.add("min");
        int colAltAvg = 4;
        colSum += colAltAvg;
        headerFormat.add("%" + colAltAvg + "s");
        header1Values.add("");
        header2Values.add("avg");
        int colAscent = 4;
        colSum += colAscent;
        headerFormat.add("%" + colAscent + "s");
        header1Values.add("");
        header2Values.add("asc");
        int colDescent = 4;
        colSum += colDescent;
        headerFormat.add("%" + colDescent + "s");
        header1Values.add("");
        header2Values.add("des");
        int colAltEnhanced = 4;
        colSum += colAltEnhanced;
        headerFormat.add("%" + colAltEnhanced + "s");
        header1Values.add("");
        header2Values.add("enh");

        int colTempStart = 4;
        colSum += colTempStart;
        headerFormat.add("%" + colTempStart + "s");
        header1Values.add("Temp");
        header2Values.add("1st");
        int colTempEnd = 4;
        colSum += colTempEnd;
        headerFormat.add("%" + colTempEnd + "s");
        header1Values.add("");
        header2Values.add("end");
        int colTempMax = 4;
        colSum += colTempMax;
        headerFormat.add("%" + colTempMax + "s");
        header1Values.add("");
        header2Values.add("max");
        int colTempMin = 4;
        colSum += colTempMin;
        headerFormat.add("%" + colTempMin + "s");
        header1Values.add("");
        header2Values.add("min");
        int colTempAvg = 4;
        colSum += colTempAvg;
        headerFormat.add("%" + colTempAvg + "s");
        header1Values.add("");
        header2Values.add("avg");

        int colSLat = 10;
        colSum += colSLat;
        headerFormat.add("%-" + colSLat + "s");
        header1Values.add(" Start");
        header2Values.add(" lat");
        int colSLon = 10;
        colSum += colSLon;
        headerFormat.add("%-" + colSLon + "s");
        header1Values.add("");
        header2Values.add(" lon");
        int colELat = 10;
        colSum += colELat;
        headerFormat.add("%-" + colELat + "s");
        header1Values.add(" End");
        header2Values.add(" lat");
        int colELon = 10;
        colSum += colELon;
        headerFormat.add("%-" + colELon + "s");
        header1Values.add("");
        header2Values.add(" lon");

        
        headerFormatStr = "";
        for (String fmt : headerFormat) {
            headerFormatStr += fmt;
        }
        headerFormatStr += "%n";

        System.out.printf(headerFormatStr, header1Values.toArray());
        System.out.printf(headerFormatStr, header2Values.toArray());

        System.out.println("-".repeat(colSum));

        for (Mesg mesg : fitFile.getLapMesg()) {
            LapExtraMesg lapExtra = fitFile.getLapExtraRecords().get(i);

            Integer lapNo = lapExtra.getLapNo();
            String lapNoStr = lapNo != null ? String.format("%d", lapNo) : "-";
            System.out.printf("%" + colLapNo + "s", lapNoStr);

            // Altitude
            Float altStart = lapExtra.getAltStart();
            String altStartStr = altStart != null ? String.format("%.0f", altStart) : "-";
            System.out.printf("%" + colAltStart + "s", altStartStr);

            Float altEnd = lapExtra.getAltEnd();
            String altEndStr = altEnd != null ? String.format("%.0f", altEnd) : "-";
            System.out.printf("%" + colAltEnd + "s", altEndStr);

            Float altMax = lapExtra.getAltMax();
            String altMaxStr = altMax != null ? String.format("%.0f", altMax) : "-";
            System.out.printf("%" + colAltMax + "s", altMaxStr);

            Float altMin = lapExtra.getAltMin();
            String altMinStr = altMin != null ? String.format("%.0f", altMin) : "-";
            System.out.printf("%" + colAltMin + "s", altMinStr);

            Float altAvg = lapExtra.getAltAvg();
            String altAvgStr = altAvg != null ? String.format("%.0f", altAvg) : "-";
            System.out.printf("%" + colAltAvg + "s", altAvgStr);

            Float ascent = lapExtra.getAscent();
            String ascentStr = ascent != null ? String.format("%.0f", ascent) : "-";
            System.out.printf("%" + colAscent + "s", ascentStr);

            Float descent = lapExtra.getDescent();
            String descentStr = descent != null ? String.format("%.0f", descent) : "-";
            System.out.printf("%" + colDescent + "s", descentStr);

            Boolean altEnhancedUsed = lapExtra.getAltEnhancedUsed();
            String altEnhancedUsedStr = altEnhancedUsed != null ? (altEnhancedUsed ? "yes" : "no") : "-";
            System.out.printf("%" + colAltEnhanced + "s", altEnhancedUsedStr);

            // TEMP
            Integer tempStart = lapExtra.getTempStart();
            String tempStartStr = tempStart != null ? String.format("%d", tempStart) : "-";
            System.out.printf("%" + colTempStart + "s", tempStartStr);

            Integer tempEnd = lapExtra.getTempEnd();
            String tempEndStr = tempEnd != null ? String.format("%d", tempEnd) : "-";
            System.out.printf("%" + colTempEnd + "s", tempEndStr);

            Integer tempMax = lapExtra.getTempMax();
            String tempMaxStr = tempMax != null ? String.format("%d", tempMax) : "-";
            System.out.printf("%" + colTempMax + "s", tempMaxStr);

            Integer tempMin = lapExtra.getTempMin();
            String tempMinStr = tempMin != null ? String.format("%d", tempMin) : "-";
            System.out.printf("%" + colTempMin + "s", tempMinStr);

            Integer tempAvg = lapExtra.getTempAvg();
            String tempAvgStr = tempAvg != null ? String.format("%d", tempAvg) : "-";
            System.out.printf("%" + colTempAvg + "s", tempAvgStr);

            // Coordinates
            Integer sLat = lapExtra.getLatStart();
            String sLatStr = sLat != null ? String.format("%d", sLat) : "-";
            System.out.printf("%" + colSLat + "s", sLatStr);

            Integer sLon = lapExtra.getLonStart();
            String sLonStr = sLon != null ? String.format("%d", sLon) : "-";
            System.out.printf("%" + colSLon + "s", sLonStr);

            Integer eLat = lapExtra.getLatEnd();
            String eLatStr = eLat != null ? String.format("%d", eLat) : "-";
            System.out.printf("%" + colELat + "s", eLatStr);

            Integer eLon = lapExtra.getLonEnd();
            String eLonStr = eLon != null ? String.format("%d", eLon) : "-";
            System.out.printf("%" + colELon + "s", eLonStr);

            System.out.println();

            // SECOND ROW WITH FILE VALUES
            // ==============================
            System.out.printf("%" + colLapNo + "s", "");

            // Altitude
            System.out.printf("%" + colAltStart + "s", "file");

            System.out.printf("%" + colAltEnd + "s", "->  ");

            Float altMaxFile = null;
            Float altMinFile = null;
            Float altAvgFile = null;
            if (lapExtra.getAltEnhancedUsed() != null && lapExtra.getAltEnhancedUsed()) {
                altMaxFile = mesg.getFieldFloatValue(FitFile.LAP_EMALT);
                altMinFile = mesg.getFieldFloatValue(FitFile.LAP_EMINALT);
                altAvgFile = mesg.getFieldFloatValue(FitFile.LAP_EALT);
            } else {
                altMaxFile = mesg.getFieldFloatValue(FitFile.LAP_MALT);
                altMinFile = mesg.getFieldFloatValue(FitFile.LAP_MINALT);
                altAvgFile = mesg.getFieldFloatValue(FitFile.LAP_ALT);
            }

            String altMaxStrFile = altMaxFile != null ? String.format("%.0f", altMaxFile) : "-";
            System.out.printf("%" + colAltMax + "s", altMaxStrFile);

            String altMinStrFile = altMinFile != null ? String.format("%.0f", altMinFile) : "-";
            System.out.printf("%" + colAltMin + "s", altMinStrFile);

            String altAvgStrFile = altAvgFile != null ? String.format("%.0f", altAvgFile) : "-";
            System.out.printf("%" + colAltAvg + "s", altAvgStrFile);

            Float ascentFile = mesg.getFieldFloatValue(FitFile.LAP_ASC);
            String ascentStrFile = ascentFile != null ? String.format("%.0f", ascentFile) : "-";
            System.out.printf("%" + colAscent + "s", ascentStrFile);

            Float descentFile = mesg.getFieldFloatValue(FitFile.LAP_DESC);
            String descentStrFile = descentFile != null ? String.format("%.0f", descentFile) : "-";
            System.out.printf("%" + colDescent + "s", descentStrFile);

            System.out.printf("%" + colAltEnhanced + "s", "");

            System.out.printf("%" + colAltStart + "s", "");

            System.out.printf("%" + colAltEnd + "s", "");

            Integer tempMaxFile = mesg.getFieldIntegerValue(FitFile.LAP_MAXTEMP);
            Integer tempMinFile = mesg.getFieldIntegerValue(FitFile.LAP_MINTEMP);
            Integer tempAvgFile = mesg.getFieldIntegerValue(FitFile.LAP_TEMP);

            String tempMaxStrFile = tempMaxFile != null ? String.format("%d", tempMaxFile) : "-";
            System.out.printf("%" + colTempMax + "s", tempMaxStrFile);

            String tempMinStrFile = tempMinFile != null ? String.format("%d", tempMinFile) : "-";
            System.out.printf("%" + colTempMin + "s", tempMinStrFile);

            String tempAvgStrFile = tempAvgFile != null ? String.format("%d", tempAvgFile) : "-";
            System.out.printf("%" + colTempAvg + "s", tempAvgStrFile);

            // Coordinates
            Integer sLatFile = mesg.getFieldIntegerValue(FitFile.LAP_SLAT);
            String sLatStrFile = sLatFile != null ? String.format("%d", sLatFile) : "-";
            System.out.printf("%" + colSLat + "s", sLatStrFile);

            Integer sLonFile = mesg.getFieldIntegerValue(FitFile.LAP_SLON);
            String sLonStrFile = sLonFile != null ? String.format("%d", sLonFile) : "-";
            System.out.printf("%" + colSLon + "s", sLonStrFile);

            Integer eLatFile = mesg.getFieldIntegerValue(FitFile.LAP_ELAT);
            String eLatStrFile = eLatFile != null ? String.format("%d", eLatFile) : "-";
            System.out.printf("%" + colELat + "s", eLatStrFile);

            Integer eLonFile = mesg.getFieldIntegerValue(FitFile.LAP_ELON);
            String eLonStrFile = eLonFile != null ? String.format("%d", eLonFile) : "-";
            System.out.printf("%" + colELon + "s", eLonStrFile);

            System.out.println();
            i++;
        }
        // =================================================================
        // NEXT TABLE
        i = 0;
        colSum = 0;
        headerFormat.clear();
        header1Values.clear();
        header2Values.clear();

        colLapNo = 3;
        colSum += colLapNo;
        headerFormat.add("%" + colLapNo + "s");
        header1Values.add("Lap");
        header2Values.add("no");

        int colLevel = 5;
        colSum += colLevel;
        headerFormat.add("%" + colLevel + "s");
        header1Values.add("Lv");
        header2Values.add("");
        int colAvgStrokeLen = 6;
        colSum += colAvgStrokeLen;
        headerFormat.add("%" + colAvgStrokeLen + "s");
        header1Values.add("strL");
        header2Values.add("avg");
        int colMaxStrokeLen = 6;
        colSum += colMaxStrokeLen;
        headerFormat.add("%" + colMaxStrokeLen + "s");
        header1Values.add("");
        header2Values.add("max");
        int colAvgDragFactor = 6;
        colSum += colAvgDragFactor;
        headerFormat.add("%" + colAvgDragFactor + "s");
        header1Values.add("DF");
        header2Values.add("avg");
        int colMaxDragFactor = 6;
        colSum += colMaxDragFactor;
        headerFormat.add("%" + colMaxDragFactor + "s");
        header1Values.add("");
        header2Values.add("max");
        int colSpeedLapSum = 5;
        colSum += colSpeedLapSum;
        headerFormat.add("%" + colSpeedLapSum + "s");
        header1Values.add("Sp");
        header2Values.add("sum");
        int colCadLapSum = 4;
        colSum += colCadLapSum;
        headerFormat.add("%" + colCadLapSum + "s");
        header1Values.add("Cad");
        header2Values.add("sum");

        headerFormatStr = "";
        for (String fmt : headerFormat) {
            headerFormatStr += fmt;
        }
        headerFormatStr += "%n";

        System.out.printf(headerFormatStr, header1Values.toArray());
        System.out.printf(headerFormatStr, header2Values.toArray());

        System.out.println("-".repeat(colSum));

        for (Mesg mesg : fitFile.getLapMesg()) {
            LapExtraMesg lapExtra = fitFile.getLapExtraRecords().get(i);

            Integer lapNo = lapExtra.getLapNo();
            String lapNoStr = lapNo != null ? String.format("%d", lapNo) : "-";
            System.out.printf("%" + colLapNo + "s", lapNoStr);

            // Indoor
            Float level = lapExtra.getLevel();
            String levelStr = level != null ? String.format("%.2f", level) : "-";
            System.out.printf("%" + colLevel + "s", levelStr);

            Float avgStrokeLen = lapExtra.getAvgStrokeLen();
            String avgStrokeLenStr = avgStrokeLen != null ? String.format("%.2f", avgStrokeLen) : "-";
            System.out.printf("%" + colAvgStrokeLen + "s", avgStrokeLenStr);

            Float maxStrokeLen = lapExtra.getMaxStrokeLen();
            String maxStrokeLenStr = maxStrokeLen != null ? String.format("%.2f", maxStrokeLen) : "-";
            System.out.printf("%" + colMaxStrokeLen + "s", maxStrokeLenStr);

            Float avgDragFactor = lapExtra.getAvgDragFactor();
            String avgDragFactorStr = avgDragFactor != null ? String.format("%.1f", avgDragFactor) : "-";
            System.out.printf("%" + colAvgDragFactor + "s", avgDragFactorStr);

            Float maxDragFactor = lapExtra.getMaxDragFactor();
            String maxDragFactorStr = maxDragFactor != null ? String.format("%.1f", maxDragFactor) : "-";
            System.out.printf("%" + colMaxDragFactor + "s", maxDragFactorStr);

            Float speedLapSum = lapExtra.getSpeedLapSum();
            String speedLapSumStr = speedLapSum != null ? String.format("%.1f", speedLapSum) : "-";
            System.out.printf("%" + colSpeedLapSum + "s", speedLapSumStr);

            Float cadLapSum = lapExtra.getCadLapSum();
            String cadLapSumStr = cadLapSum != null ? String.format("%.1f", cadLapSum) : "-";
            System.out.printf("%" + colCadLapSum + "s", cadLapSumStr);
            
            System.out.println();

            // SECOND ROW WITH FILE VALUES
            // ==============================
            System.out.printf("%" + colLapNo + "s", "");

            // Indoor
            System.out.printf("%" + colLevel + "s", "");

            System.out.printf("%" + colAvgStrokeLen + "s", "");

            System.out.printf("%" + colMaxStrokeLen + "s", "");

            System.out.printf("%" + colAvgDragFactor + "s", "");

            System.out.printf("%" + colMaxDragFactor + "s", "");

            System.out.printf("%" + colSpeedLapSum + "s", "");

            System.out.printf("%" + colCadLapSum + "s", "");
            
            System.out.println();
            i++;
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
        fitFile.clearTempUpdateLog();

        try {
            fitFile.appendTempUpdateLogLn("---- ACTIVE LAPS ----");
            int i = 0;
            int lapNo = 1;
            for (Mesg record : fitFile.getLapMesg()) { // Generic Mesg type
                Short intensityVal = record.getFieldShortValue(fitFile.LAP_INTENSITY);
                String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "";

                if ("ACTIVE".equals(intensity)) {
                    fitFile.appendTempUpdateLog("Lap" + lapNo);

                    if (fitFile.getLapExtraRecords().get(i).getLevel() != null) {
                        if (fitFile.getMySport() == FitFile.MySport.TREADMILL) {
                            fitFile.appendTempUpdateLog(" " + fitFile.getLapExtraRecords().get(i).getLevel().intValue() + "%");
                        } else 
                        if (fitFile.getMySport() == FitFile.MySport.ELLIPTICAL) {
                            fitFile.appendTempUpdateLog(" lv" + fitFile.getLapExtraRecords().get(i).getLevel().intValue());
                        }
                    }

                    Integer hrMin = 0;
                    if (i > 0) {
                        hrMin = fitFile.getLapExtraRecords().get(i - 1).getHrMin();
                        fitFile.appendTempUpdateLog(" HRmin" + hrMin);
                    } else {
                        fitFile.appendTempUpdateLog(" HR");
                    }

                    Integer hrStart = fitFile.getLapExtraRecords().get(i).getHrStart();
                    fitFile.appendTempUpdateLog(">st" + hrStart);
                    if ((hrStart - hrMin) > 20) {
                        hrMin = hrStart;;
                    }

                    Short maxHr = record.getFieldShortValue(fitFile.LAP_MHR);
                    if (maxHr != null) {
                        fitFile.appendTempUpdateLog("+" + (maxHr - hrMin));
                        fitFile.appendTempUpdateLog("->max" + maxHr);
                    }

                    fitFile.appendTempUpdateLog(" end" + fitFile.getLapExtraRecords().get(i).getHrEnd());

                    Float totalTime = record.getFieldFloatValue(fitFile.LAP_TIMER);
                    if (totalTime != null) {
                        fitFile.appendTempUpdateLog(" " + PehoUtils.sec2minSecShort(totalTime) + "min");
                    }

                    Short avgCad = record.getFieldShortValue(fitFile.LAP_CAD);
                    if (avgCad != null) {
                        if (fitFile.getMySport() == FitFile.MySport.RUN) {
                            fitFile.appendTempUpdateLog(" " + (avgCad * 2) + "spm");
                        } else {
                            fitFile.appendTempUpdateLog(" " + avgCad + "spm");
                        }
                    }

                    Float avgSpeed = record.getFieldFloatValue(fitFile.LAP_ESPEED);
                    if (avgSpeed != null) {
                        if (fitFile.getMySport() == FitFile.MySport.SKIERG) {
                            fitFile.appendTempUpdateLog(" " + PehoUtils.sec2minSecLong(500 / avgSpeed) + "min/500m");
                        } else if (fitFile.getMySport() == FitFile.MySport.RUN) {
                            fitFile.appendTempUpdateLog(" " + PehoUtils.sec2minSecLong(1000 / avgSpeed) + "min/km");
                        } else if (fitFile.getMySport() == FitFile.MySport.BIKE) {
                            fitFile.appendTempUpdateLog(" " + String.format("%.1fkm/h", avgSpeed * 3.60));
                        } else {
                            fitFile.appendTempUpdateLog(" " + PehoUtils.sec2minSecLong(1000 / avgSpeed) + "min/km");
                            fitFile.appendTempUpdateLog(" " + String.format("%.1fkm/h", avgSpeed * 3.60));
                        }
                    }

                    Integer avgPower = record.getFieldIntegerValue(fitFile.LAP_POW);
                    if (avgPower != null) {
                        fitFile.appendTempUpdateLog(" " + avgPower + "W");
                    }

                    Double dist = record.getFieldDoubleValue(fitFile.LAP_DIST);
                    if (dist != null) {
                        fitFile.appendTempUpdateLog(" " + String.format("%.1fkm", dist / 1000));
                    }

                    if (fitFile.getLapExtraRecords().get(i).getAvgDragFactor() != null
                         && fitFile.getMySport() == FitFile.MySport.SKIERG) {
                        fitFile.appendTempUpdateLog(" df" + Math.round(fitFile.getLapExtraRecords().get(i).getAvgDragFactor()));
                    }
                    if (fitFile.getLapExtraRecords().get(i).getAvgStrokeLen() != null
                     && fitFile.getMySport() == FitFile.MySport.SKIERG) {
                        fitFile.appendTempUpdateLog(" sl" + fitFile.getLapExtraRecords().get(i).getAvgStrokeLen());
                    }
                    if (fitFile.getLapExtraRecords().get(i).getStepLen() != null
                     && (fitFile.getMySport() == FitFile.MySport.ELLIPTICAL
                        || fitFile.getMySport() == FitFile.MySport.TREADMILL
                        || fitFile.getMySport() == FitFile.MySport.OTHER
                            )) {
                        fitFile.appendTempUpdateLog(" step" + (int) (fitFile.getLapExtraRecords().get(i).getStepLen() * 100) + "cm");
                    }

                    fitFile.appendTempUpdateLogLn("");
                }
                i++;
                lapNo++;
            }

            fitFile.appendTempUpdateLog(
                lapEndSum2String(
                    fitFile.getActiveAvgCad(), 
                    fitFile.getActiveAvgSpeed(), 
                    fitFile.getActiveAvgPower(), 
                    fitFile.getActiveDist()
                )
            );

            // ================= REST LAPS =================
            fitFile.appendTempUpdateLogLn("---- REST LAPS ----");
            i = 0;
            lapNo = 1;
            for (Mesg record : fitFile.getLapMesg()) {
                Short intensityVal = record.getFieldShortValue(fitFile.LAP_INTENSITY);
                String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "";

                if ("REST".equals(intensity) || "RECOVERY".equals(intensity)) {
                    fitFile.appendTempUpdateLog("Lap" + lapNo);

                    if (fitFile.getLapExtraRecords().get(i).getLevel() != null) {
                        if (fitFile.getMySport() == FitFile.MySport.TREADMILL) {
                            fitFile.appendTempUpdateLog(" " + fitFile.getLapExtraRecords().get(i).getLevel().intValue() + "%");
                        } else {
                            fitFile.appendTempUpdateLog(" lv" + fitFile.getLapExtraRecords().get(i).getLevel().intValue());
                        }
                    }

                    fitFile.appendTempUpdateLog(" HRst" + fitFile.getLapExtraRecords().get(i).getHrStart());

                    Integer maxHr = record.getFieldIntegerValue(fitFile.LAP_MHR);
                    if (maxHr != null) {
                        fitFile.appendTempUpdateLog(">max" + maxHr);
                        fitFile.appendTempUpdateLog("" + (fitFile.getLapExtraRecords().get(i).getHrMin() - maxHr));
                        fitFile.appendTempUpdateLog("->min" + fitFile.getLapExtraRecords().get(i).getHrMin());
                    }

                    fitFile.appendTempUpdateLog(" end" + fitFile.getLapExtraRecords().get(i).getHrEnd());

                    Float totalTime = record.getFieldFloatValue(fitFile.LAP_TIMER);
                    if (totalTime != null) {
                        fitFile.appendTempUpdateLog(" " + PehoUtils.sec2minSecShort(totalTime) + "min");
                    }

                    Short avgCad = record.getFieldShortValue(fitFile.LAP_CAD);
                    if (avgCad != null) {
                        if (fitFile.getMySport() == FitFile.MySport.RUN) {
                            fitFile.appendTempUpdateLog(" " + (avgCad * 2) + "spm");
                        } else {
                            fitFile.appendTempUpdateLog(" " + avgCad + "spm");
                        }
                    }

                    Float avgSpeed = record.getFieldFloatValue(fitFile.LAP_ESPEED);
                    if (avgSpeed != null) {
                        if (fitFile.getMySport() == FitFile.MySport.SKIERG) {
                            fitFile.appendTempUpdateLog(" " + PehoUtils.sec2minSecLong(500 / avgSpeed) + "min/500m");
                        } else if (fitFile.getMySport() == FitFile.MySport.RUN) {
                            fitFile.appendTempUpdateLog(" " + PehoUtils.sec2minSecLong(1000 / avgSpeed) + "min/km");
                        } else if (fitFile.getMySport() == FitFile.MySport.BIKE) {
                            fitFile.appendTempUpdateLog(" " + String.format("%.1fkm/h", avgSpeed * 3.60));
                        } else {
                            fitFile.appendTempUpdateLog(" " + PehoUtils.sec2minSecLong(1000 / avgSpeed) + "min/km");
                            fitFile.appendTempUpdateLog(" " + String.format("%.1fkm/h", avgSpeed * 3.60));
                        }
                    }

                    Integer avgPower = record.getFieldIntegerValue(fitFile.LAP_POW);
                    if (avgPower != null) {
                        fitFile.appendTempUpdateLog(" " + avgPower + "W");
                    }

                    Double dist = record.getFieldDoubleValue(fitFile.LAP_DIST);
                    if (dist != null) {
                        fitFile.appendTempUpdateLog(" " + String.format("%.1fkm", dist / 1000));
                    }

                    if (fitFile.getLapExtraRecords().get(i).getStepLen() != null
                     && (fitFile.getMySport() == FitFile.MySport.ELLIPTICAL
                        || fitFile.getMySport() == FitFile.MySport.TREADMILL
                        || fitFile.getMySport() == FitFile.MySport.OTHER
                            )) {
                        fitFile.appendTempUpdateLog(" step" + (int) (fitFile.getLapExtraRecords().get(i).getStepLen() * 100) + "cm");
                    }

                    fitFile.appendTempUpdateLogLn("");
                }
                i++;
                lapNo++;
            }

            fitFile.appendTempUpdateLog(
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
            fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
            System.out.println(fitFile.getTempUpdateLog());
        }
        return fitFile.getTempUpdateLog();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // Debug method to print lap and record details for verification
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    // lapd command
    public void debugLapRecords(List<Mesg> lapMesgs, List<Mesg> recordMesgs) {
        System.out.println("-------------------------------------------");
        System.out.println("----- L A P   R E C O R D   D E B U G -----");
        System.out.printf("Laps: %d  Records: %d%n%n", lapMesgs.size(), recordMesgs.size());

        for (int i = 0; i < lapMesgs.size(); i++) {
            Mesg lap = lapMesgs.get(i);

            int sRecIx = fitFile.getLapExtraRecords().get(i).getRecordIxStart();
            int eRecIx = fitFile.getLapExtraRecords().get(i).getRecordIxEnd();

            // --- basic lap info
            Long startTime = lap.getFieldLongValue(FitFile.LAP_STIME);
            Long recStartTime = (i >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx).getFieldLongValue(FitFile.REC_TIME) 
                : startTime;
            Long recStartTimeMinus1 = (i - 1 >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx-1).getFieldLongValue(FitFile.REC_TIME) 
                : startTime;

            Float dist = lap.getFieldFloatValue(FitFile.LAP_DIST);

            Float startDist = null;
            Float recStartDist = (i >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx).getFieldFloatValue(FitFile.REC_DIST) 
                : 0f;
            Float recStartDistMinus1 = (i - 1 >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx-1).getFieldFloatValue(FitFile.REC_DIST) 
                : 0f;

            Float endDist = null;
            Float recEndDist = (i >= 0) 
                ? fitFile.getRecordMesg().get(eRecIx).getFieldFloatValue(FitFile.REC_DIST) 
                : 0f;
            Float recEndDistPlus1 = (i + 1 < fitFile.getLapExtraRecords().size()) 
                ? fitFile.getRecordMesg().get(eRecIx+1).getFieldFloatValue(FitFile.REC_DIST) 
                : recEndDist;

            Float speed = lap.getFieldFloatValue(FitFile.LAP_SPEED);
            Float eSpeed = lap.getFieldFloatValue(FitFile.LAP_ESPEED);

            Long startLat = lap.getFieldLongValue(FitFile.LAP_SLAT);
            Long recStartLat = (i >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx).getFieldLongValue(FitFile.REC_LAT) 
                : 0L;
            Long recStartLatMinus1 = (i - 1 >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx-1).getFieldLongValue(FitFile.REC_LAT) 
                : recStartLat;

            Long startLon = lap.getFieldLongValue(FitFile.LAP_SLON);
            Long recStartLon = (i >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx).getFieldLongValue(FitFile.REC_LON) 
                : 0L;
            Long recStartLonMinus1 = (i - 1 >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx-1).getFieldLongValue(FitFile.REC_LON) 
                : recStartLon;

                Long timestamp = lap.getFieldLongValue(FitFile.LAP_TIME);
            if (startTime == null) startTime = timestamp;
            if (startTime == null) {
                System.out.printf("%n---- LAP %d ---- (no start_time, skipping)%n", i + 1);
                continue;
            }

            Float eTimer = lap.getFieldFloatValue(FitFile.LAP_ETIMER) != null
             ? lap.getFieldFloatValue(FitFile.LAP_ETIMER) : null;
            Float tTimer = lap.getFieldFloatValue(FitFile.LAP_TIMER) != null
             ? lap.getFieldFloatValue(FitFile.LAP_TIMER) : null;

            Long endTime = fitFile.getLapExtraRecords().get(i).getTimeEnd();
            Long recEndTime = endTime;
            Long recEndTimePlus1 = (i + 1 < fitFile.getLapExtraRecords().size()) 
                ? fitFile.getRecordMesg().get(eRecIx+1).getFieldLongValue(FitFile.REC_TIME)
                : recEndTime;

            Long endLat = lap.getFieldLongValue(FitFile.LAP_ELAT);
            Long recEndLat = (i >= 0) 
                ? fitFile.getRecordMesg().get(eRecIx).getFieldLongValue(FitFile.REC_LAT) 
                : 0L;
            Long recEndLatPlus1 = (i + 1 < fitFile.getLapExtraRecords().size()) 
                ? fitFile.getRecordMesg().get(eRecIx+1).getFieldLongValue(FitFile.REC_LAT) 
                : recEndLat;

            Long endLon = lap.getFieldLongValue(FitFile.LAP_ELON);
            Long recEndLon = (i >= 0) 
                ? fitFile.getRecordMesg().get(eRecIx).getFieldLongValue(FitFile.REC_LON) 
                : 0L;
            Long recEndLonPlus1 = (i + 1 < fitFile.getLapExtraRecords().size()) 
                ? fitFile.getRecordMesg().get(eRecIx+1).getFieldLongValue(FitFile.REC_LON) 
                : recEndLon;

                Long nextStart = null;
            if (i + 1 < lapMesgs.size()) {
                nextStart = lapMesgs.get(i + 1).getFieldLongValue(FitFile.LAP_STIME);
            }
            if (endTime == null) {
                if (eTimer != null && eTimer > 0f) {
                    endTime = startTime + Math.round(eTimer);
                } else if (tTimer != null && tTimer > 0f) {
                    endTime = startTime + Math.round(tTimer);
                } else if (i + 1 < lapMesgs.size()) {
                    nextStart = getLongField(lapMesgs.get(i + 1), "start_time", null);
                    if (nextStart == null)
                        nextStart = getLongField(lapMesgs.get(i + 1), "timestamp", null);
                    if (nextStart != null && nextStart > startTime) endTime = nextStart;
                }
                // fallback: find last record after lap start
                if (endTime == null) {
                    Long lastAfter = null;
                    for (Mesg r : recordMesgs) {
                        Long rts = getLongField(r, "timestamp", null);
                        if (rts != null && rts >= startTime) lastAfter = rts;
                    }
                    if (lastAfter != null && lastAfter > startTime) endTime = lastAfter;
                }
            }

            if (endTime == null) endTime = startTime + 1;

            //endTimeL++; // +1s inclusive

            //long startTimeL = startTimeL;
            //long endTimeL = endTimeL;

            float lapDist = lap.getFieldFloatValue(FitFile.LAP_DIST) != null
             ? lap.getFieldFloatValue(FitFile.LAP_DIST) : 0f;
            float lapAvgSpd = lap.getFieldFloatValue(FitFile.LAP_ESPEED) != null
             ? lap.getFieldFloatValue(FitFile.LAP_ESPEED) : 0f;

            long messageIndexL = getLongField(lap, "message_index", 0L);
            long eventL = getLongField(lap, "event", 0L);
            long eventTypeL = getLongField(lap, "event_type", 0L);
            long lapTriggerL = getLongField(lap, "lap_trigger", 0L);

            System.out.printf("---- LAP %d ----%n", i + 1);
            System.out.printf(
                "Start: %s  End: %s  Dur: %s  LapMesg Dist: %skm  LapMesg AvgSpd: %.3f m/s%n",
                new Tstr(startTime).get(), new Tstr(endTime).get(), 
                new Hmmss(endTime - startTime).get(), new Km5(lapDist).get(), lapAvgSpd);
            System.out.printf(
                "message_index=%d  event=%d  event_type=%d  lap_trigger=%d%n",
                messageIndexL, eventL, eventTypeL, lapTriggerL);

            // --- Records immediately after lap start
            System.out.println("10 Records after lap start:");
            int count = 0;
            for (int j = sRecIx; j <= (sRecIx+9) && j < recordMesgs.size(); j++) {
                Mesg r = recordMesgs.get(j);
                Long ts = getLongField(r, "timestamp", null);
                if (ts != null && ts >= startTime && ts < startTime + 10) {
                    printRecord(r, startTime);
                    count++;
                    //if (count >= 10) break;
                }
            }

            // --- Records immediately before lap end
            System.out.println("10 Records before lap end:");
            count = 0;
            for (int j = eRecIx - 9; j <= eRecIx && j < recordMesgs.size(); j++) {
                Mesg r = recordMesgs.get(j);
                Long ts = getLongField(r, "timestamp", null);
                if (ts != null && ts >= endTime - 10 && ts <= endTime) {
                    printRecord(r, startTime);
                    count++;
                    //if (count >= 10) break;
                }
            }

            // --- Compute totals from records within the lap
            List<Mesg> lapRecords = new ArrayList<>();
            if (sRecIx >= 0 && eRecIx >= sRecIx && eRecIx < recordMesgs.size()) {
                lapRecords = recordMesgs.subList(sRecIx, eRecIx + 1);
            } else {
                for (Mesg r : recordMesgs) {
                    Long ts = r.getFieldLongValue(FitFile.REC_TIME);
                    if (ts != null && ts >= startTime && ts <= endTime) {
                        lapRecords.add(r);
                    }
                }
            }

            float firstDist = lapRecords.isEmpty() ? 0.0f : getFloatField(lapRecords.get(0), "distance", 0f);
            float lastDist = lapRecords.isEmpty() ? 0.0f : getFloatField(lapRecords.get(lapRecords.size() - 1), "distance", 0f);
            float distDelta = lastDist - firstDist;
            long timeDelta = endTime - startTime;
            float avgSpeed = timeDelta > 0 ? distDelta / timeDelta : 0.0f;


            System.out.printf(
                "=> Records total: %skm  %s %.3fm/s%n",
                new Km5(distDelta).get(), new Hmmss(timeDelta).get(), avgSpeed);
            int col1 = 11;
            int col2 = 10;
            int col3 = 15;
            int col4 = 17;
            int col5 = 17;
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "Compare:",
                "Lap mesg", 
                "Rec start->end", 
                "Rec start-1->end", 
                "Rec start->end+1");
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "start time:",
                new Tstr(startTime).get(),
                new Tstr(recStartTime).get(),
                new Tstr(recStartTimeMinus1).get(), 
                new Tstr(recStartTime).get());
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "end time:",
                new Tstr(endTime).get(),
                new Tstr(recEndTime).get(),
                new Tstr(recEndTime).get(), 
                new Tstr(recEndTimePlus1).get());
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "E Timer:",
                new Hmmss(eTimer).get(),
                new Hmmss(recEndTime - recStartTime).get(),
                new Hmmss(recEndTime - recStartTimeMinus1).get(), 
                new Hmmss(recEndTimePlus1 - recStartTime).get());
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "start dist:",
                startDist != null ? startDist : "N/A",
                recStartDist != null ? new Km5(recStartDist).get() : "N/A",
                recStartDistMinus1 != null ? new Km5(recStartDistMinus1).get() : "N/A", 
                recStartDist != null ? new Km5(recStartDist).get() : "N/A");
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "end dist:",
                endDist != null ? endDist : "N/A",
                recEndDist != null ? new Km5(recEndDist).get() : "N/A",
                recEndDist != null ? new Km5(recEndDist).get() : "N/A",
                recEndDistPlus1 != null ? new Km5(recEndDistPlus1).get() : "N/A");
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "dist:",
                dist != null ? new Km5(dist).get() : "N/A",
                new Km5(recEndDist - recStartDist).get(),
                new Km5(recEndDist - recStartDistMinus1).get(),
                recEndDistPlus1 != null ? new Km5(recEndDistPlus1 - recStartDist).get() : "N/A");
            System.out.printf("%" + col1 + "s%" + col2 + "f%" + col3 + "f%" + col4 + "f%" + col5 + "f%n",
                "E speed:",
                eSpeed != null ? eSpeed : 0,
                (tTimer != null && tTimer > 0) ? (recEndDist - recStartDist) / tTimer : 0,
                (tTimer != null && tTimer > 0) ? (recEndDist - recStartDistMinus1) / tTimer : 0,
                (tTimer != null && tTimer > 0) ? (recEndDistPlus1 - recStartDist) / tTimer : 0);
            System.out.printf("%" + col1 + "s%" + col2 + "f%" + col3 + "f%" + col4 + "f%" + col5 + "f%n",
                "speed:",
                speed != null ? speed : 0,
                (tTimer != null && tTimer > 0) ? (recEndDist - recStartDist) / tTimer : 0,
                (tTimer != null && tTimer > 0) ? (recEndDist - recStartDistMinus1) / tTimer : 0,
                (tTimer != null && tTimer > 0) ? (recEndDistPlus1 - recStartDist) / tTimer : 0);
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "start lat:",
                startLat != null ? startLat : "N/A",
                recStartLat != null ? recStartLat : "N/A",
                recStartLatMinus1 != null ? recStartLatMinus1 : "N/A", 
                recStartLat != null ? recStartLat : "N/A");
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "start lon:",
                startLon != null ? startLon : "N/A",
                recStartLon != null ? recStartLon : "N/A",
                recStartLonMinus1 != null ? recStartLonMinus1 : "N/A", 
                recStartLon != null ? recStartLon : "N/A");
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "end lat:",
                endLat != null ? endLat : "N/A",
                recEndLat != null ? recEndLat : "N/A",
                recEndLat != null ? recEndLat : "N/A",
                recEndLatPlus1 != null ? recEndLatPlus1 : "N/A");
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "end lon:",
                endLon != null ? endLon : "N/A",
                recEndLon != null ? recEndLon : "N/A",
                recEndLon != null ? recEndLon : "N/A",
                recEndLonPlus1 != null ? recEndLonPlus1 : "N/A");
                
            
            System.out.printf(
                "Compare LapMesg vs Records: LapMesgDist=%.2f  RecordsDist=%.2f  LapMesgAvgSpd=%.3f  RecAvgSpd=%.3f%n%n",
                lapDist, distDelta, lapAvgSpd, avgSpeed);
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private static void printRecord(Mesg r, long lapStart) {
        Long ts = r.getFieldLongValue(FitFile.REC_TIME);
        Float dist = r.getFieldFloatValue(FitFile.REC_DIST);
        Float spd = r.getFieldFloatValue(FitFile.REC_SPEED);
        Float enhSpd = r.getFieldFloatValue(FitFile.REC_ESPEED);
        Long lat = r.getFieldLongValue(FitFile.REC_LAT);
        Long lon = r.getFieldLongValue(FitFile.REC_LON);
        if (ts == null) return;
        System.out.printf(
            " lapt=%s %s %s %sm/s %sm/s(enh) lat=%s lon=%s%n",
            new Hmmss(ts - lapStart).get(), 
            new Tstr(ts).get(), 
            new Km5(dist).get(), 
            spd, enhSpd,
            lat, lon
        );
            //" lapt=%ds  ts=%d  dist=%.2f  spd=%.3f  enhSpd=%.3f%n",
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