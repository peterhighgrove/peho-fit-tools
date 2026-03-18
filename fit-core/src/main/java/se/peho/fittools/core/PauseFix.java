package se.peho.fittools.core;

import com.garmin.fit.Event;
import com.garmin.fit.EventType;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;
import com.garmin.fit.RecordMesg;

public class PauseFix {

    private final FitFile fitFile;

    public PauseFix(FitFile fitFile) {
        this.fitFile = fitFile;
    }

    public void pauseShorten(int pauseNo, Long newPauseTime) {

        FitFile.PauseMesg pauseToShorten = fitFile.getPauseList().get(pauseNo-1);

        Mesg startPauseRecord = fitFile.getRecordMesg().get(pauseToShorten.getIxStart());
        Mesg stopGapRecord = fitFile.getRecordMesg().get(pauseToShorten.getIxStop()); // Org PAUSE STOP = New GAP STOP

        Long startPauseTime = startPauseRecord.getFieldLongValue(FitFile.REC_TIME);
        Float startPauseDist = startPauseRecord.getFieldFloatValue(FitFile.REC_DIST);

        Long startGapTime = startPauseTime + newPauseTime;
        Float startGapDist = startPauseDist;
        int startGapPow = 0;
        if (fitFile.getRecordMesg().get(pauseToShorten.getIxStop()+1).getFieldIntegerValue(FitFile.REC_POW) != null){
            startGapPow = fitFile.getRecordMesg().get(pauseToShorten.getIxStop()+1).getFieldIntegerValue(FitFile.REC_POW);
        }

        Long stopGapTime = stopGapRecord.getFieldLongValue(FitFile.REC_TIME); // New GAP END
        Float stopGapDist = startGapDist + pauseToShorten.getDistPause(); // New GAP END
        int stopGapPow = startGapPow;

        // Speed Vaules
        Float startGapSpeed = (float) (pauseToShorten.getDistPause() / (stopGapTime - startGapTime)) ;
        Float stopGapSpeed = startGapSpeed;

        stopGapRecord.setFieldValue(FitFile.REC_SPEED, stopGapSpeed);
        stopGapRecord.setFieldValue(FitFile.REC_ESPEED, stopGapSpeed);

        // Power Value always missing in record after Pause
        stopGapRecord.setFieldValue(FitFile.REC_POW, stopGapPow);

        fitFile.clearTempUpdateLogg();
        fitFile.appendTempUpdateLoggLn("");
        fitFile.appendTempUpdateLoggLn("PAUSE - SHORTEN, forgot to resume timer after pause");
        fitFile.appendTempUpdateLoggLn("--------------------------------------------");
        fitFile.appendTempUpdateLoggLn("Shortened pause no: " + pauseNo);
        fitFile.appendTempUpdateLoggLn("-- Pause decreased from " + pauseToShorten.getTimePause() + "sec to " + newPauseTime + "sec");
        fitFile.appendTempUpdateLoggLn("-->"
            + "new speed:" + PehoUtils.mps2minpkm(startGapSpeed) + "min/km" 
            + " / " + PehoUtils.mps2kmph3(startGapSpeed) + "km/h"
            + " dist:" + pauseToShorten.getDistPause() + "m"
            + " gap dist start:" + startGapDist + "m end:" + stopGapDist + "m"
            + " gap time end-start:" + (stopGapTime - startGapTime) + "s");
        System.out.println(fitFile.getTempUpdateLogg());
        fitFile.appendUpdateLogg(fitFile.getTempUpdateLogg());

        // Updating EVENT-TIMER-START DATA
        //----------------------   
        fitFile.getEventTimerMesg().get(pauseToShorten.getIxEvStop()).setFieldValue(FitFile.EVE_TIME, startGapTime);

        // Create new PAUSE STOP / GAP START
        // ------------------------------------------------
        Mesg startGapNewRecord = new Mesg(startPauseRecord); // New PAUSE STOP = GAP START
        startGapNewRecord.setFieldValue(FitFile.REC_TIME, startGapTime);
        startGapNewRecord.setFieldValue(FitFile.REC_SPEED, startGapSpeed);
        startGapNewRecord.setFieldValue(FitFile.REC_ESPEED, startGapSpeed);
        startGapNewRecord.setFieldValue(FitFile.REC_POW, startGapPow);
        fitFile.getAllMesg().add(fitFile.findIxInAllMesgBasedOnTime(stopGapTime), startGapNewRecord);
        fitFile.getRecordMesg().add(pauseToShorten.getIxStop(), startGapNewRecord);
        fitFile.setNumberOfRecords(fitFile.getNumberOfRecords() + 1);

        // Increase distance after the shortened pause, starting from 1 after pause stop
        // ------------------------------------------------------
        fitFile.addDistToRecords(pauseToShorten.getIxStop()+1, pauseToShorten.getDistPause());

        // Updating LAP DATA
        //------------------
        Float lapTime = fitFile.getLapMesg().get(pauseToShorten.getIxLap()).getFieldFloatValue(FitFile.LAP_TIMER) + pauseToShorten.getTimePause() - newPauseTime;
        Float lapDist = fitFile.getLapMesg().get(pauseToShorten.getIxLap()).getFieldFloatValue(FitFile.LAP_DIST) + pauseToShorten.getDistPause();
        fitFile.getLapMesg().get(pauseToShorten.getIxLap()).setFieldValue(FitFile.LAP_TIMER, lapTime);
        //fitFile.getLapMesg().get(pauseToShorten.getIxLap()).setFieldValue(FitFile.LAP_ETIMER, lapTime);
        fitFile.getLapMesg().get(pauseToShorten.getIxLap()).setFieldValue(FitFile.LAP_DIST, lapDist);
        fitFile.getLapMesg().get(pauseToShorten.getIxLap()).setFieldValue(FitFile.LAP_SPEED, lapDist / lapTime);
        fitFile.getLapMesg().get(pauseToShorten.getIxLap()).setFieldValue(FitFile.LAP_ESPEED, lapDist / lapTime);

        // Updating SESSION DATA
        //----------------------
        fitFile.setTotalTimerTime(fitFile.getTotalTimerTime() + (float) pauseToShorten.getTimePause() - newPauseTime);
        fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_TIMER, fitFile.getTotalTimerTime());
        fitFile.getActivityMesg().get(0).setFieldValue(FitFile.ACT_TIMER, fitFile.getTotalTimerTime());
        //fitFile.sessionMesg.get(0).setFieldValue(FitFile.SES_MTIMER, (float) (fitFile.sessionMesg.get(0).getFieldFloatValue(FitFile.SES_MTIMER) + pauseToShorten.getTimePause() - newPauseTime));
        //fitFile.elapsedTimerTime += (float) pauseToShorten.getTimePause() - newPauseTime;
        //fitFile.sessionMesg.get(0).setFieldValue(FitFile.SES_ETIMER, fitFile.elapsedTimerTime);

        fitFile.setTotalDistance(fitFile.getRecordMesg().get(fitFile.getNumberOfRecords()-1).getFieldFloatValue(RecordMesg.DistanceFieldNum));
        fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_DIST, fitFile.getTotalDistance());

        fitFile.setAvgSpeed(fitFile.getTotalDistance() / fitFile.getTotalTimerTime());
        fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_SPEED, fitFile.getAvgSpeed());
        fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_ESPEED, fitFile.getAvgSpeed());
    }

    public void pauseIncrease(int pauseNo, Long secondsToPutIntoPause) {

        FitFile.PauseMesg pauseToIncrease = fitFile.getPauseList().get(pauseNo-1);

        Mesg startPauseEvent = fitFile.getEventTimerMesg().get(pauseToIncrease.getIxEvStart());
        Long orgStartEventTime = startPauseEvent.getFieldLongValue(FitFile.EVE_TIME);

        Mesg startPauseRecord = fitFile.getRecordMesg().get(pauseToIncrease.getIxStart());
        Float orgStartPauseDist = startPauseRecord.getFieldFloatValue(FitFile.REC_DIST);

        Long newStartEventTime = orgStartEventTime - secondsToPutIntoPause;
        startPauseEvent.setFieldValue(FitFile.EVE_TIME, newStartEventTime);

        // Delete records between new and old start of pause, inside the pause
        // ----------------------------------------------------------------------
        Long recordToDeleteTime = orgStartEventTime;
        int i = 0;
        int recordToDeleteIx = pauseToIncrease.getIxStart();
        int allMesgToDeleteIx = fitFile.findIxInAllMesgBasedOnTime(orgStartEventTime);
        while (recordToDeleteTime > newStartEventTime) {
            //System.out.println("Deleting record time:"+recordToDeleteTime+" Rix:"+recordToDeleteIx+" Aix:"+allMesgToDeleteIx);
            fitFile.getAllMesg().remove(allMesgToDeleteIx);
            fitFile.getRecordMesg().remove(recordToDeleteIx);

            allMesgToDeleteIx--;
            while (fitFile.getAllMesg().get(allMesgToDeleteIx).getNum() != MesgNum.RECORD){
                allMesgToDeleteIx--;
            }
            recordToDeleteIx--;
            recordToDeleteTime--;
            i++;

        }
        fitFile.setNumberOfRecords(fitFile.getNumberOfRecords() - i);

        fitFile.clearTempUpdateLogg();
        fitFile.appendTempUpdateLoggLn("PAUSE - INCREASE, forgot to stop before");
        fitFile.appendTempUpdateLoggLn("---------------------------------------");
        fitFile.appendTempUpdateLoggLn("Increased pause no: " + pauseNo);
        fitFile.appendTempUpdateLoggLn("-- Pause increased with " + secondsToPutIntoPause + "sec to " + PehoUtils.sec2minSecLong(pauseToIncrease.getTimePause()+secondsToPutIntoPause) + "min");
        System.out.println(fitFile.getTempUpdateLogg());
        fitFile.appendUpdateLogg(fitFile.getTempUpdateLogg());

        // Increase distance after the shortened pause, starting from 1 after pause stop
        // ------------------------------------------------------
        Float newStartPauseDist = fitFile.getRecordMesg().get(recordToDeleteIx).getFieldFloatValue(FitFile.REC_DIST);
        Float distChangeValue = newStartPauseDist-orgStartPauseDist; // Will be negative
        System.out.println("Dist:"+orgStartPauseDist+"-"+newStartPauseDist+"="+distChangeValue);
        fitFile.addDistToRecords(recordToDeleteIx+1, distChangeValue);

        // Updating LAP DATA
        //------------------
        Float lapTime = fitFile.getLapMesg().get(pauseToIncrease.getIxLap()).getFieldFloatValue(FitFile.LAP_TIMER) - secondsToPutIntoPause;
        Float lapDist = fitFile.getLapMesg().get(pauseToIncrease.getIxLap()).getFieldFloatValue(FitFile.LAP_DIST) + distChangeValue;
        fitFile.getLapMesg().get(pauseToIncrease.getIxLap()).setFieldValue(FitFile.LAP_TIMER, (lapTime));
        //fitFile.getLapMesg().get(pauseToShorten.getIxLap()).setFieldValue(FitFile.LAP_ETIMER, (lapTime - secondsToPutIntoPause));
        fitFile.getLapMesg().get(pauseToIncrease.getIxLap()).setFieldValue(FitFile.LAP_DIST, (lapDist));
        fitFile.getLapMesg().get(pauseToIncrease.getIxLap()).setFieldValue(FitFile.LAP_SPEED, (lapDist / lapTime));
        fitFile.getLapMesg().get(pauseToIncrease.getIxLap()).setFieldValue(FitFile.LAP_ESPEED, (lapDist / lapTime));

        // Updating SESSION DATA
        //----------------------
        fitFile.setTotalTimerTime(fitFile.getTotalTimerTime() - (float) secondsToPutIntoPause);
        fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_TIMER, fitFile.getTotalTimerTime());
        fitFile.getActivityMesg().get(0).setFieldValue(FitFile.ACT_TIMER, fitFile.getTotalTimerTime());
        Float sesMTimer = fitFile.getSessionMesg().get(0).getFieldFloatValue(FitFile.SES_MTIMER);
        if (sesMTimer != null) {
            fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_MTIMER, sesMTimer - secondsToPutIntoPause);
        }
        //fitFile.elapsedTimerTime -= (float) secondsToPutIntoPause;
        //fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_ETIMER, fitFile.elapsedTimerTime);

        fitFile.setTotalDistance(fitFile.getRecordMesg().get(fitFile.getNumberOfRecords()-1).getFieldFloatValue(RecordMesg.DistanceFieldNum));
        fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_DIST, fitFile.getTotalDistance());

        fitFile.setAvgSpeed(fitFile.getTotalDistance() / fitFile.getTotalTimerTime());
        fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_SPEED, fitFile.getAvgSpeed());
        fitFile.getSessionMesg().get(0).setFieldValue(FitFile.SES_ESPEED, fitFile.getAvgSpeed());
    }

    public void pauseToGap(int pauseNo) {

        fitFile.clearTempUpdateLogg();
        fitFile.appendTempUpdateLoggLn("-------------------------");
        fitFile.appendTempUpdateLoggLn("Deleting TIMER events in pause " + pauseNo + " to create a GAP");
        fitFile.appendTempUpdateLoggLn("-------------------------");

        Long pauseStart = fitFile.getPauseList().get(pauseNo - 1).getTimeStart();
        Long pauseStop = fitFile.getPauseList().get(pauseNo - 1).getTimeStop();

        fitFile.deleteEvents(pauseStart, pauseStop, Event.TIMER, EventType.INVALID);
        fitFile.appendTempUpdateLoggLn("==>> Deleted Timer events between " + FitDateTime.toString(pauseStart, fitFile.getDiffMinutesLocalUTC()) + " and " + FitDateTime.toStringTime(pauseStop, fitFile.getDiffMinutesLocalUTC()) + " (inclusive).");

        fitFile.updateActivityInfoWhenDeletingPauseToGap(pauseNo - 1);

        System.out.println(fitFile.getTempUpdateLogg());
        fitFile.appendUpdateLogg(fitFile.getTempUpdateLogg());
    }
}
