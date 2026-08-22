package se.peho.fittools.core;

import com.garmin.fit.Intensity;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;
import com.garmin.fit.FitRuntimeException;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
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
        int colTimeStart = 9;
        colSum += colTimeStart;
        headerFormat.add("%" + colTimeStart + "s");
        header1Values.add("Time");
        header2Values.add("1st");
        int colTimeEnd = 9;
        colSum += colTimeEnd;
        headerFormat.add("%" + colTimeEnd + "s");
        header1Values.add("Time");
        header2Values.add("last");
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
        int colHrMin = 4;
        colSum += colHrMin;
        headerFormat.add("%" + colHrMin + "s");
        header1Values.add("");
        header2Values.add("min");
        int colRecordIxStart = 6;
        colSum += colRecordIxStart;
        headerFormat.add("%" + colRecordIxStart + "s");
        header1Values.add("Rec");
        header2Values.add("1st");
        int colRecordIxEnd = 6;
        colSum += colRecordIxEnd;
        headerFormat.add("%" + colRecordIxEnd + "s");
        header1Values.add("");
        header2Values.add("end");
        int colDistStart = 8;
        colSum += colDistStart;
        headerFormat.add("%" + colDistStart + "s");
        header1Values.add("Dist");
        header2Values.add("start");
        int colDistEnd = 8;
        colSum += colDistEnd;
        headerFormat.add("%" + colDistEnd + "s");
        header1Values.add("");
        header2Values.add("end");
        int colDistCalc = 7;
        colSum += colDistCalc;
        headerFormat.add("%" + colDistCalc + "s");
        header1Values.add("");
        header2Values.add("calc");
        int colDistOrg = 7;
        colSum += colDistOrg;
        headerFormat.add("%" + colDistOrg + "s");
        header1Values.add("");
        header2Values.add("org");
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
        int colIntensity = 10;
        colSum += colIntensity;
        headerFormat.add("%" + colIntensity + "s");
        header1Values.add("Intensity");
        header2Values.add("");
        int colLevel = 4;
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
        int colAvgDragFactor = 4;
        colSum += colAvgDragFactor;
        headerFormat.add("%" + colAvgDragFactor + "s");
        header1Values.add("DF");
        header2Values.add("avg");
        int colMaxDragFactor = 4;
        colSum += colMaxDragFactor;
        headerFormat.add("%" + colMaxDragFactor + "s");
        header1Values.add("");
        header2Values.add("max");
        int colStepLen = 4;
        colSum += colStepLen;
        headerFormat.add("%" + colStepLen + "s");
        header1Values.add("Step");
        header2Values.add("Len");
        int colSpeedLapSum = 4;
        colSum += colSpeedLapSum;
        headerFormat.add("%" + colSpeedLapSum + "s");
        header1Values.add("Sp");
        header2Values.add("sum");
        int colCadLapSum = 4;
        colSum += colCadLapSum;
        headerFormat.add("%" + colCadLapSum + "s");
        header1Values.add("Cad");
        header2Values.add("sum");

        String headerFormatStr = "";
        for (String fmt : headerFormat) {
            headerFormatStr += fmt;
        }

        System.out.println();
        System.out.println("-".repeat(colSum));
        System.out.println("--- LAPS IN FILE - LapExtraDebug (lap4-LapAllSummary) ---");
        System.out.println("-".repeat(colSum));
        System.out.printf("Laps: %d  LapsExtras: %d  Records: %d%n%n", fitFile.getLapMesg().size(), fitFile.getLapExtraRecords().size(), fitFile.getRecordMesg().size());
        
        System.out.printf(headerFormatStr + "%n", header1Values.toArray());
        System.out.printf(headerFormatStr + "%n", header2Values.toArray());
        

        // System.out.printf(headerFormatStr + "%" + colTimeStart + "s%" + colTimeEnd + "s%" + colHrStart + "s%" + colHrEnd + "s%" + colHrMin + "s%" + colRecordIxStart + "s%" + colRecordIxEnd + "s%" + colDistStart + "s%" + colDistEnd + "s%" + colDistCalc + "s%" + colDistOrg + "s%" + colAltStart + "s%" + colAltEnd + "s%" + colLevel + "s%" + colAvgStrokeLen + "s%" + colMaxStrokeLen + "s%" + colAvgDragFactor + "s%" + colMaxDragFactor + "s%" + colStepLen + "s%" + colSpeedLapSum + "s%" + colCadLapSum + "s%" + colIntensity + "s%n"
        //     , "Lap", "Time", "", "HR", "", "", "Rec", "", "Dist", "", "", "", "Alt", "", "Lv", "strL", "", "DF", "",  "Step", "Spee", "Cad", "Inten");
        // System.out.printf("%" + colLapNo + "s%" + colTimeStart + "s%" + colTimeEnd + "s%" + colHrStart + "s%" + colHrEnd + "s%" + colHrMin + "s%" + colRecordIxStart + "s%" + colRecordIxEnd + "s%" + colDistStart + "s%" + colDistEnd + "s%" + colDistCalc + "s%" + colDistOrg + "s%" + colAltStart + "s%" + colAltEnd + "s%" + colLevel + "s%" + colAvgStrokeLen + "s%" + colMaxStrokeLen + "s%" + colAvgDragFactor + "s%" + colMaxDragFactor + "s%" + colStepLen + "s%" + colSpeedLapSum + "s%" + colCadLapSum + "s%" + colIntensity + "s%n"
        //     , "no", "first", "end", "st", "end", "min", "stIx", "eIx", "start", "end", "calc", "org", "st", "end", "", "avg", "max", "avg", "max", "Len", "sum", "sum", ""); 
        System.out.println("-".repeat(colSum));

        for (Mesg mesg : fitFile.getLapMesg()) {
            
            Integer lapNo = fitFile.getLapExtraRecords().get(i).getLapNo();
            String lapNoStr = lapNo != null ? String.format("%d", lapNo) : "-";
            System.out.printf("%" + colLapNo + "s", lapNoStr);

            Long startTime = mesg.getFieldLongValue(FitFile.LAP_STIME);
            String startTimeStr = startTime != null ? new Tstr(startTime, fitFile.getDiffMinutesLocalUTC()).get() : "-";
            System.out.printf("%" + colTimeStart + "s", startTimeStr);
            
            Long endTime = fitFile.getLapExtraRecords().get(i).getTimeEnd();
            String endTimeStr = endTime != null ? new Tstr(endTime, fitFile.getDiffMinutesLocalUTC()).get() : "-";
            System.out.printf("%" + colTimeEnd + "s", endTimeStr);

            Short hrStart = fitFile.getLapExtraRecords().get(i).getHrStart();
            String hrStartStr = hrStart != null ? String.format("%d", hrStart) : "-";
            System.out.printf("%" + colHrStart + "s", hrStartStr);
            
            Short hrEnd = fitFile.getLapExtraRecords().get(i).getHrEnd();
            String hrEndStr = hrEnd != null ? String.format("%d", hrEnd) : "-";
            System.out.printf("%" + colHrEnd + "s", hrEndStr);
            
            Short hrMin = fitFile.getLapExtraRecords().get(i).getHrMin();
            String hrMinStr = hrMin != null ? String.format("%d", hrMin) : "-";
            System.out.printf("%" + colHrMin + "s", hrMinStr);
            
            Integer recordIxStart = fitFile.getLapExtraRecords().get(i).getRecordIxStart();
            String recordIxStartStr = recordIxStart != null ? String.format("%d", recordIxStart) : "-";
            System.out.printf("%" + colRecordIxStart + "s", recordIxStartStr);

            Integer recordIxEnd = fitFile.getLapExtraRecords().get(i).getRecordIxEnd();
            String recordIxEndStr = recordIxEnd != null ? String.format("%d", recordIxEnd) : "-";
            System.out.printf("%" + colRecordIxEnd + "s", recordIxEndStr);

            Float distStart = fitFile.getLapExtraRecords().get(i).getDistStart();
            String distStartStr = distStart != null ? String.format("%.1f", distStart) : "-";
            System.out.printf("%" + colDistStart + "s", distStartStr);

            Float distEnd = fitFile.getLapExtraRecords().get(i).getDistEnd();
            String distEndStr = distEnd != null ? String.format("%.1f", distEnd) : "-";
            System.out.printf("%" + colDistEnd + "s", distEndStr);

            Float distDiff = 0f;
            if (distEnd != null && distStart != null) {
                distDiff = distEnd - distStart;
                String distDiffStr = String.format("%.1f", distDiff);
                System.out.printf("%" + colDistCalc + "s", distDiffStr);
            } else {
                System.out.printf("%" + colDistCalc + "s", "-");
            }

            Float distOrg = mesg.getFieldFloatValue(FitFile.LAP_DIST);
            String distOrgStr = distOrg != null ? String.format("%.1f", distOrg) : "-";
            System.out.printf("%" + colDistOrg + "s", distOrgStr);

            Integer altStart = fitFile.getLapExtraRecords().get(i).getAltStart();
            String altStartStr = altStart != null ? String.format("%d", altStart) : "-";
            System.out.printf("%" + colAltStart + "s", altStartStr);

            Integer altEnd = fitFile.getLapExtraRecords().get(i).getAltEnd();
            String altEndStr = altEnd != null ? String.format("%d", altEnd) : "-";
            System.out.printf("%" + colAltEnd + "s", altEndStr);

            Short intensityVal = mesg.getFieldShortValue(FitFile.LAP_INTENSITY);
            String intensity = intensityVal != null ? Intensity.getStringFromValue(Intensity.getByValue(intensityVal)) : "UNKNOWN";
            System.out.printf("%" + colIntensity + "s", intensity);

            Float level = fitFile.getLapExtraRecords().get(i).getLevel();
            String levelStr = level != null ? String.format("%f", level) : "-";
            System.out.printf("%" + colLevel + "s", levelStr);

            Float avgStrokeLen = fitFile.getLapExtraRecords().get(i).getAvgStrokeLen();
            String avgStrokeLenStr = avgStrokeLen != null ? String.format("%.1f", avgStrokeLen) : "-";
            System.out.printf("%" + colAvgStrokeLen + "s", avgStrokeLenStr);

            Float maxStrokeLen = fitFile.getLapExtraRecords().get(i).getMaxStrokeLen();
            String maxStrokeLenStr = maxStrokeLen != null ? String.format("%.1f", maxStrokeLen) : "-";
            System.out.printf("%" + colMaxStrokeLen + "s", maxStrokeLenStr);

            Float avgDragFactor = fitFile.getLapExtraRecords().get(i).getAvgDragFactor();
            String avgDragFactorStr = avgDragFactor != null ? String.format("%.1f", avgDragFactor) : "-";
            System.out.printf("%" + colAvgDragFactor + "s", avgDragFactorStr);

            Float maxDragFactor = fitFile.getLapExtraRecords().get(i).getMaxDragFactor();
            String maxDragFactorStr = maxDragFactor != null ? String.format("%.1f", maxDragFactor) : "-";
            System.out.printf("%" + colMaxDragFactor + "s", maxDragFactorStr);

            Float stepLen = fitFile.getLapExtraRecords().get(i).getStepLen();
            String stepLenStr = stepLen != null ? String.format("%.1f", stepLen) : "-";
            System.out.printf("%" + colStepLen + "s", stepLenStr);

            Float speedLapSum = fitFile.getLapExtraRecords().get(i).getSpeedLapSum();
            String speedLapSumStr = speedLapSum != null ? String.format("%.1f", speedLapSum) : "-";
            System.out.printf("%" + colSpeedLapSum + "s", speedLapSumStr);

            Float cadLapSum = fitFile.getLapExtraRecords().get(i).getCadLapSum();
            String cadLapSumStr = cadLapSum != null ? String.format("%.1f", cadLapSum) : "-";
            System.out.printf("%" + colCadLapSum + "s", cadLapSumStr);
            
            System.out.println();
            i++;
            lapNo++;
        }
        System.out.println("-".repeat(colSum));
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

                    Short hrMin = 0;
                    if (i > 0) {
                        hrMin = fitFile.getLapExtraRecords().get(i - 1).getHrMin();
                        fitFile.appendTempUpdateLog(" HRmin" + hrMin);
                    } else {
                        fitFile.appendTempUpdateLog(" HR");
                    }

                    Short hrStart = fitFile.getLapExtraRecords().get(i).getHrStart();
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
                : startLat;
            Long recStartLatMinus1 = (i - 1 >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx-1).getFieldLongValue(FitFile.REC_LAT) 
                : startLat;

            Long startLon = lap.getFieldLongValue(FitFile.LAP_SLON);
            Long recStartLon = (i >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx).getFieldLongValue(FitFile.REC_LON) 
                : startLon;
            Long recStartLonMinus1 = (i - 1 >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx-1).getFieldLongValue(FitFile.REC_LON) 
                : startLon;

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
                ? fitFile.getRecordMesg().get(sRecIx).getFieldLongValue(FitFile.REC_LAT) 
                : endLat;
            Long recEndLatPlus1 = (i + 1 < fitFile.getRecordMesg().size()) 
                ? fitFile.getRecordMesg().get(sRecIx+1).getFieldLongValue(FitFile.REC_LAT) 
                : recEndLat;

            Long endLon = lap.getFieldLongValue(FitFile.LAP_ELON);
            Long recEndLon = (i >= 0) 
                ? fitFile.getRecordMesg().get(sRecIx).getFieldLongValue(FitFile.REC_LON) 
                : endLon;
            Long recEndLonPlus1 = (i + 1 < fitFile.getRecordMesg().size()) 
                ? fitFile.getRecordMesg().get(sRecIx+1).getFieldLongValue(FitFile.REC_LON) 
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
                recEndLatPlus1 != null ? recEndLatPlus1 : "N/A", 
                recEndLat != null ? recEndLat : "N/A");
            System.out.printf("%" + col1 + "s%" + col2 + "s%" + col3 + "s%" + col4 + "s%" + col5 + "s%n", 
                "end lon:",
                endLon != null ? endLon : "N/A",
                recEndLon != null ? recEndLon : "N/A",
                recEndLonPlus1 != null ? recEndLonPlus1 : "N/A", 
                recEndLon != null ? recEndLon : "N/A");
                
            
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