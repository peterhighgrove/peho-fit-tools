package se.peho.fittools.core;

import com.garmin.fit.DateTime;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;

public class LapFix {

    private final FitFile fitFile;

    public LapFix(FitFile fitFile) {
        this.fitFile = fitFile;
    }

    public void lapMerge(int fromLap, int toLap) {

        Long timeStart = 0L;
        Long timeEnd = 0L;
        Float timerSumOfLaps = 0f;
        Float elapsedTimerSumOfLaps = 0f;
        Float movingTimerSumOfLaps = 0f;
        Float distSumOfLaps = 0f;
        Float hrAvgFaktorsumOfLaps = 0f;
        Integer hrMaxOfLaps = 0;
        Float speedAvgFaktorsumOfLaps = 0f;
        Float speedMaxOfLaps = 0f;
        Float enhSpeedAvgFaktorsumOfLaps = 0f;
        Float enhSpeedMaxOfLaps = 0f;
        Float powerAvgFaktorsumOfLaps = 0f;
        Integer powerMaxOfLaps = 0;
        Float cadenceAvgFaktorsumOfLaps = 0f;
        Integer cadenceMaxOfLaps = 0;
        Float ascentSumOfLaps = 0f;
        Float descentSumOfLaps = 0f;
        Float altAvgFaktorsumOfLaps = 0f;
        Float altMaxOfLaps = 0f;
        Float altMinOfLaps = 0f;
        Integer latStart = 0;
        Integer lonStart = 0;
        Integer latEnd = 0;
        Integer lonEnd = 0;
        Float tempAvgFaktorsumOfLaps = 0f;
        Byte tempMaxOfLaps = 0;
        Byte tempMinOfLaps = 0;

        fitFile.clearTempUpdateLogg();

        for (int lapIxCounter = (fromLap-1); lapIxCounter <= (toLap-1); lapIxCounter++) {

            // Summing up values from laps to be merged
            //-----------------------------------------------
            if (lapIxCounter == fromLap-1) {
                if (fitFile.lapMesg.get(lapIxCounter).getFieldLongValue(FitFile.LAP_STIME) != null) {
                    timeStart = fitFile.lapMesg.get(lapIxCounter).getFieldLongValue(FitFile.LAP_STIME);
                }
                if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLAT) != null) {
                    latStart = fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLAT);
                }
                if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLON) != null) {
                    lonStart = fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLON);
                }
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_TIMER) != null) {
                timerSumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_TIMER);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ETIMER) != null) {
                elapsedTimerSumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ETIMER);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MTIMER) != null) {
                movingTimerSumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MTIMER);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DIST) != null) {
                distSumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DIST);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_HR) != null) {
                hrAvgFaktorsumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_HR)
                    * fitFile.lapMesg.get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIME);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MHR) != null) {
                if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MHR) > hrMaxOfLaps) {
                    hrMaxOfLaps = fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MHR);
                }
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_SPEED) != null) {
                speedAvgFaktorsumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_SPEED)
                    * fitFile.lapMesg.get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MSPEED) != null) {
                if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MSPEED) > speedMaxOfLaps) {
                    speedMaxOfLaps = fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MSPEED);
                }
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ESPEED) != null) {
                enhSpeedAvgFaktorsumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ESPEED)
                    * fitFile.lapMesg.get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_EMSPEED) != null) {
                if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_EMSPEED) > enhSpeedMaxOfLaps) {
                    enhSpeedMaxOfLaps = fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_EMSPEED);
                }
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_POW) != null) {
                powerAvgFaktorsumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_POW)
                    * fitFile.lapMesg.get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MPOW) != null) {
                if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MPOW) > powerMaxOfLaps) {
                    powerMaxOfLaps = fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MPOW);
                }
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_CAD) != null) {
                cadenceAvgFaktorsumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_CAD)
                    * fitFile.lapMesg.get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MCAD) != null) {
                if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MCAD) > cadenceMaxOfLaps) {
                    cadenceMaxOfLaps = fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MCAD);
                }
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ASC) != null) {
                ascentSumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ASC);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DESC) != null) {
                descentSumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DESC);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ALT) != null) {
                altAvgFaktorsumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ALT)
                    * fitFile.lapMesg.get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MALT) != null) {
                if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MALT) > altMaxOfLaps) {
                    altMaxOfLaps = fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MALT);
                }
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MINALT) != null) {
                if ((fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MINALT) < altMinOfLaps) || (altMinOfLaps == 0f)) {
                    altMinOfLaps = fitFile.lapMesg.get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MINALT);
                }
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_TEMP) != null) {
                tempAvgFaktorsumOfLaps += fitFile.lapMesg.get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_TEMP)
                    * fitFile.lapMesg.get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldByteValue(FitFile.LAP_MTEMP) != null) {
                if (fitFile.lapMesg.get(lapIxCounter).getFieldByteValue(FitFile.LAP_MTEMP) > tempMaxOfLaps) {
                    tempMaxOfLaps = fitFile.lapMesg.get(lapIxCounter).getFieldByteValue(FitFile.LAP_MTEMP);
                }
            }
            if (fitFile.lapMesg.get(lapIxCounter).getFieldByteValue(FitFile.LAP_MINTEMP) != null) {
                if ((fitFile.lapMesg.get(lapIxCounter).getFieldByteValue(FitFile.LAP_MINTEMP) < tempMinOfLaps) || (tempMinOfLaps == 0)) {
                    tempMinOfLaps = fitFile.lapMesg.get(lapIxCounter).getFieldByteValue(FitFile.LAP_MINTEMP);
                }
            }
        }

        // Getting values from the "toLap - 1" lap, but will not be used
        //-----------------------------------------------
        if (fitFile.lapMesg.get(toLap-1).getFieldLongValue(FitFile.LAP_TIME) != null) {
            timeEnd = fitFile.lapMesg.get(toLap-1).getFieldLongValue(FitFile.LAP_TIME);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELAT) != null) {
            latEnd = fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELAT);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELON) != null) {
            lonEnd = fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELON);
        }

        // Setting values in the "toLap - 1" lap
        //-----------------------------------------------
        if (fitFile.lapMesg.get(toLap-1).getFieldLongValue(FitFile.LAP_STIME) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_STIME, timeStart);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_TIMER) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_TIMER, timerSumOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_ETIMER) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_ETIMER, elapsedTimerSumOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_MTIMER) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_MTIMER, movingTimerSumOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_DIST) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_DIST, distSumOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_HR) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_HR, Math.round(hrAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_MHR) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_MHR, hrMaxOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_SPEED) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_SPEED, speedAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_MSPEED) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_MSPEED, speedMaxOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_ESPEED) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_ESPEED, enhSpeedAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_EMSPEED) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_EMSPEED, enhSpeedMaxOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_POW) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_POW, Math.round(powerAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_MPOW) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_MPOW, powerMaxOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_CAD) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_CAD, Math.round(cadenceAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_MCAD) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_MCAD, cadenceMaxOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_ASC) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_ASC, ascentSumOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_DESC) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_DESC, descentSumOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_ALT) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_ALT, altAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_MALT) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_MALT, altMaxOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldFloatValue(FitFile.LAP_MINALT) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_MINALT, altMinOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_TEMP) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_TEMP, Math.round(tempAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldByteValue(FitFile.LAP_MTEMP) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_MTEMP, tempMaxOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldByteValue(FitFile.LAP_MINTEMP) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_MINTEMP, tempMinOfLaps);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_SLAT) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_SLAT, latStart);
        }
        if (fitFile.lapMesg.get(toLap-1).getFieldIntegerValue(FitFile.LAP_SLON) != null) {
            fitFile.lapMesg.get(toLap-1).setFieldValue(FitFile.LAP_SLON, lonStart);
        }
        fitFile.getLapReportGenerator().printLapReport1();
        fitFile.getLapReportGenerator().printLapReport1AllMesg();

        fitFile.appendTempUpdateLoggLn("Merged laps: " + fromLap + " to " + toLap);
        fitFile.appendTempUpdateLoggLn("-- New lap " + (toLap-1) + " time: " + PehoUtils.sec2minSecLong(timerSumOfLaps) + " min, dist: " + Math.round(distSumOfLaps) + " m");

        // Deleting the merged laps (fromLap to toLap-1)
        //-----------------------------------------------
        int deleteCount = toLap - fromLap;
        int targetLapIx = fromLap - 1;
        for (int deleteCounter = 0; deleteCounter < deleteCount; deleteCounter++) {
            int lapAllMesgIx = findLapMesgIndexInAllMesgByLapIx(targetLapIx);
            if (lapAllMesgIx < 0) {
                fitFile.appendTempUpdateLoggLn("-- Could not find LAP mesg in allMesg for lap ix:" + targetLapIx);
                continue;
            }

            Mesg lapMesgToDelete = fitFile.allMesg.get(lapAllMesgIx);
            fitFile.appendTempUpdateLoggLn("-- Deleting lap ix:" + targetLapIx + " time:"
                + FitDateTime.toString(lapMesgToDelete.getFieldLongValue(FitFile.LAP_STIME), fitFile.getDiffMinutesLocalUTC()));

            int timeInZoneIx = findLinkedTimeInZoneMesgIndex(lapAllMesgIx, targetLapIx);
            if (timeInZoneIx >= 0) {
                fitFile.appendTempUpdateLoggLn("-- Deleting linked TIME_IN_ZONE mesg for lap ix:" + targetLapIx);
                int firstRemoveIx = Math.max(lapAllMesgIx, timeInZoneIx);
                int secondRemoveIx = Math.min(lapAllMesgIx, timeInZoneIx);
                fitFile.allMesg.remove(firstRemoveIx);
                fitFile.allMesg.remove(secondRemoveIx);
            } else {
                fitFile.appendTempUpdateLoggLn("-- Could not find linked TIME_IN_ZONE mesg for lap ix:" + targetLapIx);
                fitFile.allMesg.remove(lapAllMesgIx);
            }

            int lapMesgIx = findLapMesgIndexInLapMesgByLapIx(targetLapIx);
            if (lapMesgIx >= 0) {
                fitFile.lapMesg.remove(lapMesgIx);
            }

            decrementLapReferencesAfterDeletedLap(targetLapIx);
        }
        fitFile.numberOfLaps -= (toLap - fromLap);

        fitFile.getLapReportGenerator().printLapReport1();
        fitFile.getLapReportGenerator().printLapReport1AllMesg();

        // Update SES_LAPS
        if (!fitFile.sessionMesg.isEmpty()) {
            fitFile.sessionMesg.get(0).setFieldValue(FitFile.SES_LAPS, fitFile.numberOfLaps);
        }

        // Print and save logs
        System.out.println(fitFile.getTempUpdateLogg());
        fitFile.appendUpdateLogg(fitFile.getTempUpdateLogg());
    }

    private int findLapMesgIndexInAllMesgByLapIx(int lapIx) {
        for (int i = 0; i < fitFile.allMesg.size(); i++) {
            Mesg mesg = fitFile.allMesg.get(i);
            if (mesg.getNum() != MesgNum.LAP) {
                continue;
            }
            Integer currentLapIx = mesg.getFieldIntegerValue(FitFile.LAP_IX);
            if (currentLapIx != null && currentLapIx.equals(lapIx)) {
                return i;
            }
        }
        return -1;
    }

    private int findLapMesgIndexInLapMesgByLapIx(int lapIx) {
        for (int i = 0; i < fitFile.lapMesg.size(); i++) {
            Integer currentLapIx = fitFile.lapMesg.get(i).getFieldIntegerValue(FitFile.LAP_IX);
            if (currentLapIx != null && currentLapIx.equals(lapIx)) {
                return i;
            }
        }
        return -1;
    }

    private int findLinkedTimeInZoneMesgIndex(int lapAllMesgIx, int lapIx) {
        int nextIx = lapAllMesgIx + 1;
        if (nextIx < fitFile.allMesg.size()) {
            Mesg nextMesg = fitFile.allMesg.get(nextIx);
            if (isLinkedTimeInZoneMesg(nextMesg, lapIx)) {
                return nextIx;
            }
        }

        for (int i = 0; i < fitFile.allMesg.size(); i++) {
            if (i == lapAllMesgIx || i == nextIx) {
                continue;
            }
            if (isLinkedTimeInZoneMesg(fitFile.allMesg.get(i), lapIx)) {
                return i;
            }
        }
        return -1;
    }

    private void decrementLapReferencesAfterDeletedLap(int deletedLapIx) {
        for (Mesg mesg : fitFile.allMesg) {
            if (mesg.getNum() == MesgNum.LAP) {
                Integer lapIx = mesg.getFieldIntegerValue(FitFile.LAP_IX);
                if (lapIx != null && lapIx > deletedLapIx) {
                    mesg.setFieldValue(FitFile.LAP_IX, lapIx - 1);
                }
                continue;
            }

            if (mesg.getNum() == MesgNum.TIME_IN_ZONE) {
                Integer referenceMesg = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_MESG);
                Integer referenceIndex = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_IX);
                if (referenceMesg != null
                    && referenceMesg == MesgNum.LAP
                    && referenceIndex != null
                    && referenceIndex > deletedLapIx) {
                    mesg.setFieldValue(FitFile.TIZ_REF_IX, referenceIndex - 1);
                }
            }
        }
    }

    private boolean isLinkedTimeInZoneMesg(Mesg mesg, int lapIx) {
        if (mesg.getNum() != MesgNum.TIME_IN_ZONE) {
            return false;
        }
        Integer referenceMesg = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_MESG);
        Integer referenceIndex = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_IX);
        return referenceMesg != null
            && referenceMesg == MesgNum.LAP
            && referenceIndex != null
            && referenceIndex == lapIx;
    }

    private Integer getMesgFieldAsInt(Mesg mesg, int fieldNum) {
        Integer intValue = mesg.getFieldIntegerValue(fieldNum);
        if (intValue != null) {
            return intValue;
        }
        Short shortValue = mesg.getFieldShortValue(fieldNum);
        if (shortValue != null) {
            return shortValue.intValue();
        }
        Long longValue = mesg.getFieldLongValue(fieldNum);
        if (longValue != null) {
            return longValue.intValue();
        }
        return null;
    }
}