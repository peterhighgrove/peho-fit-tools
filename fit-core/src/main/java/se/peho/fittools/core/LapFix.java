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

        fitFile.appendTempUpdateLoggLn("Merged laps: " + fromLap + " to " + toLap);
        fitFile.appendTempUpdateLoggLn("-- New lap " + (toLap-1) + " time: " + PehoUtils.sec2minSecLong(timerSumOfLaps) + " min, dist: " + Math.round(distSumOfLaps) + " m");

        // Deleting the merged laps (fromLap to toLap-1)
        //-----------------------------------------------
        for (int lapIxCounter = fromLap-1; lapIxCounter <= toLap-2; lapIxCounter++) {
            Long lapStartTime = fitFile.lapMesg.get(lapIxCounter).getFieldLongValue(FitFile.LAP_STIME);
            int mesgIxCounter = 0;
            for (Mesg mesg:fitFile.allMesg) {
                if (mesg.getNum() == MesgNum.LAP) {
                    if (mesg.getFieldLongValue(FitFile.LAP_STIME).equals(lapStartTime)) {
                        fitFile.appendTempUpdateLoggLn("-- Deleting lap ix:" + lapIxCounter + " time:" + FitDateTime.toString(mesg.getFieldLongValue(FitFile.LAP_STIME),0));
                        break;
                    }
                }
                mesgIxCounter++;
            }
            fitFile.allMesg.remove(mesgIxCounter);
            fitFile.lapMesg.remove(fromLap-1);
        }

        fitFile.numberOfLaps -= (toLap - fromLap);

        // Update LAP_IX for remaining laps
        for (int i = 0; i < fitFile.lapMesg.size(); i++) {
            fitFile.lapMesg.get(i).setFieldValue(FitFile.LAP_IX, i);
        }

        // Update SES_LAPS
        if (!fitFile.sessionMesg.isEmpty()) {
            fitFile.sessionMesg.get(0).setFieldValue(FitFile.SES_LAPS, fitFile.numberOfLaps);
        }

        // Print and save logs
        System.out.println(fitFile.getTempUpdateLogg());
        fitFile.appendUpdateLogg(fitFile.getTempUpdateLogg());
    }
}