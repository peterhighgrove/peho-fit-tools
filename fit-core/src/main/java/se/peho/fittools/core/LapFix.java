package se.peho.fittools.core;

import com.garmin.fit.DateTime;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;
import se.peho.fittools.core.strings.*;

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

        fitFile.clearTempUpdateLog();

        // int tempLapIx = 0;
        // for (Mesg mesg : fitFile.getAllMesg()) {
        //     if (mesg.getNum() == MesgNum.LAP) {
        //         Mesg lap = mesg;
        //         System.out.println("1LapIx: " + tempLapIx + 
        //             ", LapStartTime: " + new Tstr(lap.getFieldLongValue(FitFile.LAP_STIME), fitFile.getDiffMinutesLocalUTC()).get() + 
        //             ", LapTotalTimer: " + new TimeStr(lap.getFieldFloatValue(FitFile.LAP_TIMER)).get() + 
        //             ", LapIntensity: " + lap.getFieldShortValue(FitFile.LAP_INTENSITY));
        //         tempLapIx++;
        //     }
        // }
        

        for (int lapIxCounter = (fromLap-1); lapIxCounter <= (toLap-1); lapIxCounter++) {

            // Summing up values from laps to be merged
            //-----------------------------------------------
            if (lapIxCounter == fromLap-1) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_STIME) != null) {
                    timeStart = fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_STIME);
                }
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLAT) != null) {
                    latStart = fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLAT);
                }
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLON) != null) {
                    lonStart = fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_SLON);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_TIMER) != null) {
                timerSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ETIMER) != null) {
                elapsedTimerSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ETIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MTIMER) != null) {
                movingTimerSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MTIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DIST) != null) {
                distSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DIST);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_HR) != null) {
                hrAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_HR)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIME);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MHR) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MHR) > hrMaxOfLaps) {
                    hrMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MHR);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_SPEED) != null) {
                speedAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_SPEED)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MSPEED) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MSPEED) > speedMaxOfLaps) {
                    speedMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MSPEED);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ESPEED) != null) {
                enhSpeedAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ESPEED)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_EMSPEED) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_EMSPEED) > enhSpeedMaxOfLaps) {
                    enhSpeedMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_EMSPEED);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_POW) != null) {
                powerAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_POW)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MPOW) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MPOW) > powerMaxOfLaps) {
                    powerMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MPOW);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_CAD) != null) {
                cadenceAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_CAD)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MCAD) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MCAD) > cadenceMaxOfLaps) {
                    cadenceMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_MCAD);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ASC) != null) {
                ascentSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ASC);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DESC) != null) {
                descentSumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_DESC);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ALT) != null) {
                altAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_ALT)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MALT) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MALT) > altMaxOfLaps) {
                    altMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MALT);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MINALT) != null) {
                if ((fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MINALT) < altMinOfLaps) || (altMinOfLaps == 0f)) {
                    altMinOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldFloatValue(FitFile.LAP_MINALT);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_TEMP) != null) {
                tempAvgFaktorsumOfLaps += fitFile.getLapMesg().get(lapIxCounter).getFieldIntegerValue(FitFile.LAP_TEMP)
                    * fitFile.getLapMesg().get(lapIxCounter).getFieldLongValue(FitFile.LAP_TIMER);
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MTEMP) != null) {
                if (fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MTEMP) > tempMaxOfLaps) {
                    tempMaxOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MTEMP);
                }
            }
            if (fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MINTEMP) != null) {
                if ((fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MINTEMP) < tempMinOfLaps) || (tempMinOfLaps == 0)) {
                    tempMinOfLaps = fitFile.getLapMesg().get(lapIxCounter).getFieldByteValue(FitFile.LAP_MINTEMP);
                }
            }
        }

        // Getting values from the "toLap - 1" lap, but will not be used
        //-----------------------------------------------
        if (fitFile.getLapMesg().get(toLap-1).getFieldLongValue(FitFile.LAP_TIME) != null) {
            timeEnd = fitFile.getLapMesg().get(toLap-1).getFieldLongValue(FitFile.LAP_TIME);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELAT) != null) {
            latEnd = fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELAT);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELON) != null) {
            lonEnd = fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_ELON);
        }

        // Setting values in the "toLap - 1" lap
        //-----------------------------------------------
        if (fitFile.getLapMesg().get(toLap-1).getFieldLongValue(FitFile.LAP_STIME) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_STIME, timeStart);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_TIMER) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_TIMER, timerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_ETIMER) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_ETIMER, elapsedTimerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_MTIMER) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MTIMER, movingTimerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_DIST) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_DIST, distSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_HR) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_HR, Math.round(hrAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_MHR) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MHR, hrMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_SPEED) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_SPEED, speedAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_MSPEED) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MSPEED, speedMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_ESPEED) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_ESPEED, enhSpeedAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_EMSPEED) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_EMSPEED, enhSpeedMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_POW) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_POW, Math.round(powerAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_MPOW) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MPOW, powerMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_CAD) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_CAD, Math.round(cadenceAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_MCAD) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MCAD, cadenceMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_ASC) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_ASC, ascentSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_DESC) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_DESC, descentSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_ALT) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_ALT, altAvgFaktorsumOfLaps / timerSumOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_MALT) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MALT, altMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldFloatValue(FitFile.LAP_MINALT) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MINALT, altMinOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_TEMP) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_TEMP, Math.round(tempAvgFaktorsumOfLaps / timerSumOfLaps));
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldByteValue(FitFile.LAP_MTEMP) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MTEMP, tempMaxOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldByteValue(FitFile.LAP_MINTEMP) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_MINTEMP, tempMinOfLaps);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_SLAT) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_SLAT, latStart);
        }
        if (fitFile.getLapMesg().get(toLap-1).getFieldIntegerValue(FitFile.LAP_SLON) != null) {
            fitFile.getLapMesg().get(toLap-1).setFieldValue(FitFile.LAP_SLON, lonStart);
        }

        fitFile.appendTempUpdateLogLn("Merged laps: " + fromLap + " to " + toLap);
        fitFile.appendTempUpdateLogLn("-- New lap " + (toLap-1) + " time: " + PehoUtils.sec2minSecLong(timerSumOfLaps) + " min, dist: " + Math.round(distSumOfLaps) + " m");

        // Deleting the merged laps (fromLap to toLap-1)
        //-----------------------------------------------
        int deleteCount = toLap - fromLap;
        int targetLapIx = fromLap - 1;
        for (int deleteCounter = 0; deleteCounter < deleteCount; deleteCounter++) {
            int lapAllMesgIx = findLapMesgIndexInAllMesgByLapIx(targetLapIx);
            if (lapAllMesgIx < 0) {
                fitFile.appendTempUpdateLogLn("-- Could not find LAP mesg in allMesg for lap ix:" + targetLapIx);
                continue;
            }

            Mesg lapMesgToDelete = fitFile.getAllMesg().get(lapAllMesgIx);
            fitFile.appendTempUpdateLogLn("-- Deleting lap ix:" + targetLapIx + " time:"
                + FitDateTime.toString(lapMesgToDelete.getFieldLongValue(FitFile.LAP_STIME), fitFile.getDiffMinutesLocalUTC()));

            int timeInZoneIx = findLinkedTimeInZoneMesgIndex(lapAllMesgIx, targetLapIx);
            if (timeInZoneIx >= 0) {
                fitFile.appendTempUpdateLogLn("-- Deleting linked TIME_IN_ZONE mesg for lap ix:" + targetLapIx);
                int firstRemoveIx = Math.max(lapAllMesgIx, timeInZoneIx);
                int secondRemoveIx = Math.min(lapAllMesgIx, timeInZoneIx);
                fitFile.getAllMesg().remove(firstRemoveIx);
                fitFile.getAllMesg().remove(secondRemoveIx);
            } else {
                fitFile.appendTempUpdateLogLn("-- Could not find linked TIME_IN_ZONE mesg for lap ix:" + targetLapIx);
                fitFile.getAllMesg().remove(lapAllMesgIx);
            }

            int lapMesgIx = findLapMesgIndexInLapMesgByLapIx(targetLapIx);
            if (lapMesgIx >= 0) {
                fitFile.getLapMesg().remove(lapMesgIx);
            }

            decrementLapReferencesAfterDeletedLap(targetLapIx);
        }
        fitFile.setNumberOfLaps(fitFile.getNumberOfLaps() - (toLap - fromLap));

        // Update SES_LAPS
        if (!fitFile.getSessionMesg().isEmpty()) {
            fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_LAPS, fitFile.getNumberOfLaps());
        }

        // Print and save logs
        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }

    public void lapNew(Long totalTimer) {
        fitFile.clearTempUpdateLog();

        if (totalTimer == null) {
            fitFile.appendTempUpdateLogLn("==XX> No timer value provided.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }
        if (fitFile.getRecordMesgAddOnRecords() == null || fitFile.getRecordMesgAddOnRecords().isEmpty()) {
            fitFile.appendTempUpdateLogLn("==XX> Timer list is empty. Run createTimerList() first.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }
        if (fitFile.getLapMesg() == null || fitFile.getLapMesg().isEmpty()) {
            fitFile.appendTempUpdateLogLn("==XX> No lap messages found.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        int splitRecordIx = findFirstRecordIndexAtOrAfterTimer(totalTimer);
        if (splitRecordIx <= 0 || splitRecordIx >= fitFile.getRecordMesg().size()) {
            fitFile.appendTempUpdateLogLn("==XX> Timer cannot be used for lap split (outside record range): "
                + PehoUtils.sec2minSecLong(totalTimer));
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        Mesg splitRecord = fitFile.getRecordMesg().get(splitRecordIx);
        Mesg prevRecord = fitFile.getRecordMesg().get(splitRecordIx - 1);

        Long splitTime = splitRecord.getFieldLongValue(FitFile.REC_TIME);
        Long prevTime = prevRecord.getFieldLongValue(FitFile.REC_TIME);
        Long splitTimer = fitFile.getRecordMesgAddOnRecords().get(splitRecordIx).getTimer();

        if (splitTime == null || prevTime == null || splitTimer == null) {
            fitFile.appendTempUpdateLogLn("==XX> Could not resolve split record timing values.");
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        int lapIx = findLapIndexForTime(splitTime);
        if (lapIx < 0 || lapIx >= fitFile.getLapMesg().size()) {
            fitFile.appendTempUpdateLogLn("==XX> Could not find lap for timer " + PehoUtils.sec2minSecLong(totalTimer));
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        Mesg firstLap = fitFile.getLapMesg().get(lapIx);
        Mesg secondLap = new Mesg(firstLap);

        Long lapStartTime = firstLap.getFieldLongValue(FitFile.LAP_STIME);
        if (lapStartTime == null) {
            fitFile.appendTempUpdateLogLn("==XX> Lap has no start time. Cannot split lap " + (lapIx + 1));
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        Long lapStartTimer = fitFile.findTimerBasedOnTime(lapStartTime);
        if (lapStartTimer == null) {
            fitFile.appendTempUpdateLogLn("==XX> Could not resolve lap start timer for lap " + (lapIx + 1));
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        Float lapTimerOriginal = firstLap.getFieldFloatValue(FitFile.LAP_TIMER);
        float lapTimerRef = (lapTimerOriginal != null) ? lapTimerOriginal : 0f;
        float splitInLap = splitTimer - lapStartTimer;

        if (lapTimerRef <= 0f) {
            fitFile.appendTempUpdateLogLn("==XX> Lap total timer is zero/unknown for lap " + (lapIx + 1));
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }
        if (splitInLap <= 0f || splitInLap >= lapTimerRef) {
            fitFile.appendTempUpdateLogLn("==XX> Split timer must be inside lap boundaries. Lap " + (lapIx + 1)
                + " timer=" + PehoUtils.sec2minSecLong((long) lapTimerRef)
                + ", split-at=" + PehoUtils.sec2minSecLong((long) splitInLap));
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        float firstRatio = splitInLap / lapTimerRef;
        if (firstRatio <= 0f || firstRatio >= 1f) {
            fitFile.appendTempUpdateLogLn("==XX> Invalid split ratio: " + firstRatio);
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        Long originalLapEndTime = firstLap.getFieldLongValue(FitFile.LAP_TIME);

        setLongIfPresent(firstLap, FitFile.LAP_TIME, prevTime);
        setLongIfPresent(secondLap, FitFile.LAP_TIME, originalLapEndTime != null ? originalLapEndTime : splitTime);
        setLongIfPresent(secondLap, FitFile.LAP_STIME, splitTime);

        splitFloatField(firstLap, secondLap, FitFile.LAP_TIMER, firstRatio);
        splitFloatField(firstLap, secondLap, FitFile.LAP_ETIMER, firstRatio);
        splitFloatField(firstLap, secondLap, FitFile.LAP_MTIMER, firstRatio);
        splitFloatField(firstLap, secondLap, FitFile.LAP_DIST, firstRatio);
        splitFloatField(firstLap, secondLap, FitFile.LAP_ASC, firstRatio);
        splitFloatField(firstLap, secondLap, FitFile.LAP_DESC, firstRatio);

        copyFloatFieldToBoth(firstLap, secondLap, FitFile.LAP_SPEED);
        copyFloatFieldToBoth(firstLap, secondLap, FitFile.LAP_MSPEED);
        copyFloatFieldToBoth(firstLap, secondLap, FitFile.LAP_ESPEED);
        copyFloatFieldToBoth(firstLap, secondLap, FitFile.LAP_EMSPEED);
        copyFloatFieldToBoth(firstLap, secondLap, FitFile.LAP_ALT);
        copyFloatFieldToBoth(firstLap, secondLap, FitFile.LAP_MALT);
        copyFloatFieldToBoth(firstLap, secondLap, FitFile.LAP_MINALT);

        copyIntFieldToBoth(firstLap, secondLap, FitFile.LAP_HR);
        copyIntFieldToBoth(firstLap, secondLap, FitFile.LAP_MHR);
        copyIntFieldToBoth(firstLap, secondLap, FitFile.LAP_POW);
        copyIntFieldToBoth(firstLap, secondLap, FitFile.LAP_MPOW);
        copyIntFieldToBoth(firstLap, secondLap, FitFile.LAP_CAD);
        copyIntFieldToBoth(firstLap, secondLap, FitFile.LAP_MCAD);
        copyIntFieldToBoth(firstLap, secondLap, FitFile.LAP_TEMP);

        copyByteFieldToBoth(firstLap, secondLap, FitFile.LAP_MTEMP);
        copyByteFieldToBoth(firstLap, secondLap, FitFile.LAP_MINTEMP);

        Integer splitLat = splitRecord.getFieldIntegerValue(FitFile.REC_LAT);
        Integer splitLon = splitRecord.getFieldIntegerValue(FitFile.REC_LON);
        Integer prevLat = prevRecord.getFieldIntegerValue(FitFile.REC_LAT);
        Integer prevLon = prevRecord.getFieldIntegerValue(FitFile.REC_LON);

        setIntIfPresent(firstLap, FitFile.LAP_ELAT, prevLat != null ? prevLat : splitLat);
        setIntIfPresent(firstLap, FitFile.LAP_ELON, prevLon != null ? prevLon : splitLon);
        setIntIfPresent(secondLap, FitFile.LAP_SLAT, splitLat);
        setIntIfPresent(secondLap, FitFile.LAP_SLON, splitLon);

        int lapAllMesgIx = findLapMesgIndexInAllMesgByLapIx(lapIx);
        if (lapAllMesgIx < 0) {
            fitFile.appendTempUpdateLogLn("==XX> Could not find LAP in allMesg for lap ix: " + lapIx);
            System.out.println(fitFile.getTempUpdateLog());
            return;
        }

        int linkedTizIx = findLinkedTimeInZoneMesgIndex(lapAllMesgIx, lapIx);
        Mesg insertedTimeInZone = null;
        Mesg sourceTimeInZone = linkedTizIx >= 0 ? fitFile.getAllMesg().get(linkedTizIx) : null;
        int insertAllMesgIx = lapAllMesgIx + 1;
        if (linkedTizIx == lapAllMesgIx + 1) {
            insertAllMesgIx = lapAllMesgIx + 2;
        }

        setIntIfPresent(secondLap, FitFile.LAP_IX, lapIx + 1);
        fitFile.getLapMesg().add(lapIx + 1, secondLap);
        fitFile.getAllMesg().add(insertAllMesgIx, secondLap);

        if (sourceTimeInZone != null) {
            insertedTimeInZone = new Mesg(sourceTimeInZone);
            insertedTimeInZone.setFieldValue(FitFile.TIZ_REF_MESG, MesgNum.LAP);
            insertedTimeInZone.setFieldValue(FitFile.TIZ_REF_IX, lapIx + 1);
            fitFile.getAllMesg().add(insertAllMesgIx + 1, insertedTimeInZone);
        }

        incrementLapReferencesAfterInsertedLap(lapIx, secondLap, insertedTimeInZone);

        fitFile.setNumberOfLaps(fitFile.getNumberOfLaps() + 1);
        if (!fitFile.getSessionMesg().isEmpty()) {
            fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_LAPS, fitFile.getNumberOfLaps());
        }

        fitFile.appendTempUpdateLogLn("Split lap " + (lapIx + 1)
            + " at totalTimer=" + PehoUtils.sec2minSecLong(totalTimer)
            + " (recordIx=" + splitRecordIx + ")");
        fitFile.appendTempUpdateLogLn("-- Lap " + (lapIx + 1)
            + " new timer: " + new TimeStr(firstLap.getFieldFloatValue(FitFile.LAP_TIMER)).get()
            + ", new lap " + (lapIx + 2)
            + " timer: " + new TimeStr(secondLap.getFieldFloatValue(FitFile.LAP_TIMER)).get());

        System.out.println(fitFile.getTempUpdateLog());
        fitFile.appendUpdateLog(fitFile.getTempUpdateLog());
    }

    private int findLapMesgIndexInAllMesgByLapIx(int lapIx) {
        return FindMesgIx.findMesgIndexByIntField(fitFile.getAllMesg(), MesgNum.LAP, FitFile.LAP_IX, lapIx);
    }

    private int findFirstRecordIndexAtOrAfterTimer(Long totalTimer) {
        for (int i = 0; i < fitFile.getRecordMesgAddOnRecords().size(); i++) {
            Long timer = fitFile.getRecordMesgAddOnRecords().get(i).getTimer();
            if (timer != null && timer >= totalTimer) {
                return i;
            }
        }
        return fitFile.getRecordMesgAddOnRecords().size() - 1;
    }

    private int findLapIndexForTime(Long timeValue) {
        int lapIx = -1;
        for (int i = 0; i < fitFile.getLapMesg().size(); i++) {
            Long lapStart = fitFile.getLapMesg().get(i).getFieldLongValue(FitFile.LAP_STIME);
            if (lapStart != null && lapStart <= timeValue) {
                lapIx = i;
            } else if (lapStart != null && lapStart > timeValue) {
                break;
            }
        }
        return lapIx;
    }

    private void incrementLapReferencesAfterInsertedLap(int insertedAfterLapIx, Mesg insertedLap, Mesg insertedTimeInZone) {
        for (Mesg mesg : fitFile.getAllMesg()) {
            if (mesg.getNum() == MesgNum.LAP) {
                if (mesg == insertedLap) {
                    continue;
                }
                Integer lapIx = mesg.getFieldIntegerValue(FitFile.LAP_IX);
                if (lapIx != null && lapIx > insertedAfterLapIx) {
                    mesg.setFieldValue(FitFile.LAP_IX, lapIx + 1);
                }
                continue;
            }

            if (mesg.getNum() == MesgNum.TIME_IN_ZONE) {
                if (mesg == insertedTimeInZone) {
                    continue;
                }
                Integer referenceMesg = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_MESG);
                Integer referenceIndex = getMesgFieldAsInt(mesg, FitFile.TIZ_REF_IX);
                if (referenceMesg != null
                    && referenceMesg == MesgNum.LAP
                    && referenceIndex != null
                    && referenceIndex > insertedAfterLapIx) {
                    mesg.setFieldValue(FitFile.TIZ_REF_IX, referenceIndex + 1);
                }
            }
        }
    }

    private void splitFloatField(Mesg firstLap, Mesg secondLap, int fieldNum, float firstRatio) {
        Float value = firstLap.getFieldFloatValue(fieldNum);
        if (value == null) {
            return;
        }
        float firstValue = value * firstRatio;
        float secondValue = value - firstValue;
        firstLap.setFieldValue(fieldNum, firstValue);
        secondLap.setFieldValue(fieldNum, secondValue);
    }

    private void copyFloatFieldToBoth(Mesg firstLap, Mesg secondLap, int fieldNum) {
        Float value = firstLap.getFieldFloatValue(fieldNum);
        if (value == null) {
            return;
        }
        firstLap.setFieldValue(fieldNum, value);
        secondLap.setFieldValue(fieldNum, value);
    }

    private void copyIntFieldToBoth(Mesg firstLap, Mesg secondLap, int fieldNum) {
        Integer intValue = firstLap.getFieldIntegerValue(fieldNum);
        if (intValue != null) {
            firstLap.setFieldValue(fieldNum, intValue);
            secondLap.setFieldValue(fieldNum, intValue);
            return;
        }
        Short shortValue = firstLap.getFieldShortValue(fieldNum);
        if (shortValue != null) {
            firstLap.setFieldValue(fieldNum, shortValue);
            secondLap.setFieldValue(fieldNum, shortValue);
        }
    }

    private void copyByteFieldToBoth(Mesg firstLap, Mesg secondLap, int fieldNum) {
        Byte value = firstLap.getFieldByteValue(fieldNum);
        if (value == null) {
            return;
        }
        firstLap.setFieldValue(fieldNum, value);
        secondLap.setFieldValue(fieldNum, value);
    }

    private void setIntIfPresent(Mesg mesg, int fieldNum, Integer value) {
        if (value == null) {
            return;
        }
        Integer intValue = mesg.getFieldIntegerValue(fieldNum);
        if (intValue != null) {
            mesg.setFieldValue(fieldNum, value);
            return;
        }
        Short shortValue = mesg.getFieldShortValue(fieldNum);
        if (shortValue != null) {
            mesg.setFieldValue(fieldNum, value.shortValue());
        }
    }

    private void setLongIfPresent(Mesg mesg, int fieldNum, Long value) {
        if (value == null) {
            return;
        }
        if (mesg.getFieldLongValue(fieldNum) != null) {
            mesg.setFieldValue(fieldNum, value);
        }
    }

    private int findLapMesgIndexInLapMesgByLapIx(int lapIx) {
        return FindMesgIx.findMesgIndexByIntField(fitFile.getLapMesg(), MesgNum.LAP, FitFile.LAP_IX, lapIx);
    }

    private int findLinkedTimeInZoneMesgIndex(int lapAllMesgIx, int lapIx) {
        int nextIx = lapAllMesgIx + 1;
        if (nextIx < fitFile.getAllMesg().size()) {
            Mesg nextMesg = fitFile.getAllMesg().get(nextIx);
            if (isLinkedTimeInZoneMesg(nextMesg, lapIx)) {
                return nextIx;
            }
        }

        for (int i = 0; i < fitFile.getAllMesg().size(); i++) {
            if (i == lapAllMesgIx || i == nextIx) {
                continue;
            }
            if (isLinkedTimeInZoneMesg(fitFile.getAllMesg().get(i), lapIx)) {
                return i;
            }
        }
        return -1;
    }

    private void decrementLapReferencesAfterDeletedLap(int deletedLapIx) {
        for (Mesg mesg : fitFile.getAllMesg()) {
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