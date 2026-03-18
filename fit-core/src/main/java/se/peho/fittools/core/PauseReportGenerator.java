package se.peho.fittools.core;

import com.garmin.fit.*;

public class PauseReportGenerator {
    private final FitFile fitFile;

    public PauseReportGenerator(FitFile fitFile) {
        this.fitFile = fitFile;
    }

    public void printPauseList(String pauseCommandInput, Integer minDistToShow) {

        int ix = 0;

        System.out.println();
        System.out.println("================================================");
        System.out.println("PAUSES IN FILE");
        System.out.println(" File  between " + FitDateTime.toString(fitFile.getTimeFirstRecord(), fitFile.getDiffMinutesLocalUTC()) + " >>>> " + FitDateTime.toString(fitFile.getTimeLastRecord(), fitFile.getDiffMinutesLocalUTC()));
        System.out.println(String.format(" TotalTime:%1$.0fsec Dist:%2$.0fm", fitFile.getTotalTimerTime(), fitFile.getTotalDistance()));
        System.out.println("------------------------------------------------");

        for (FitFile.PauseMesg record : fitFile.getPauseList()) {
            if ((record.getIxStop() - record.getIxStart()) > 1) {
                System.out.println("==> WARNING - Data Records in pause! Pause no: " + record.getNo());
            }
            if (record.getDistPause() >= minDistToShow) {
                printPause(ix);

                if (pauseCommandInput == null) {
                    // Show minimal
                } else if (pauseCommandInput.equals("d")) {
                    System.out.print(" " + FitDateTime.toString(record.getTimeStart(), fitFile.getDiffMinutesLocalUTC()));
                    System.out.print(" Ele:" + (record.getAltStart()) + "m");
                    System.out.print(" lapNo:" + (record.getIxLap()+1));
                    System.out.print("   @ix:" + (record.getIxStart()) + "->" + (record.getIxStop()));
                    System.out.print(" @ixEv:" + (record.getIxEvStart()) + "->" + (record.getIxEvStop()));
                }
                System.out.println();
                ix ++;
            }
        }
        System.out.println("------------------------------------------------");
    }

    public void printPause(int ix) {
        FitFile.PauseMesg pauseRecord = fitFile.getPauseList().get(ix);
        int hrDiff = 0;
        String hrSign = "";

        System.out.print("   Pause (" + pauseRecord.getNo() + ")");
        System.out.print(String.format(" %1$dsec %2$.0fm ele%3$.1fm", pauseRecord.getTimePause(), pauseRecord.getDistPause(), pauseRecord.getAltPause()));

        hrDiff = fitFile.getRecordMesg().get(pauseRecord.getIxStop()).getFieldIntegerValue(FitFile.REC_HR)
                - fitFile.getRecordMesg().get(pauseRecord.getIxStart()).getFieldIntegerValue(FitFile.REC_HR);
        if (hrDiff > 0) {
            hrSign = "+";
        } else {
            hrSign = "";
        }
        System.out.print(String.format(" HR:%1$d%2$s%3$d",
            fitFile.getRecordMesg().get(pauseRecord.getIxStart()).getFieldIntegerValue(FitFile.REC_HR),
            hrSign,
            hrDiff));
        System.out.print(" @time:" + PehoUtils.sec2minSecShort(fitFile.getRecordMesgAddOnRecords().get(pauseRecord.getIxStart()).getTimer()));
        System.out.print(" @dist:" + PehoUtils.m2km2(pauseRecord.getDistStart()) + "km");
    }

    public void printEvents(Long eventTimeStartToPrint, Long eventTimeEndToPrint, Event eventToPrint, EventType eventTypeToPrint) {

        // Use EventType EventType.INVALID for Event independent of type

        System.out.println("------------------------------------------");
        System.out.println("Printing events from " + FitDateTime.toString(eventTimeStartToPrint, fitFile.getDiffMinutesLocalUTC())
                + " to " + FitDateTime.toString(eventTimeEndToPrint, fitFile.getDiffMinutesLocalUTC())
                + " for event: " + eventToPrint + " and type: " + eventTypeToPrint);
        System.out.println("------------------------------------------");

        int eventIx = 0;

        for (Mesg mesg: fitFile.getAllMesg()) {
            if (mesg.getNum() == MesgNum.EVENT) {
                Short rawEvent = mesg.getFieldShortValue(FitFile.EVE_EVENT);
                Short rawEventType = mesg.getFieldShortValue(FitFile.EVE_TYPE);
                Long eventTime = mesg.getFieldLongValue(FitFile.EVE_TIME);
                if (rawEvent == null || rawEventType == null || eventTime == null)
                    continue;

                Event mesgEvent = Event.getByValue(rawEvent);
                EventType mesgEventType = EventType.getByValue(rawEventType);

                Integer eventData = mesg.getFieldIntegerValue(EventMesg.DataFieldNum);

                if (eventTime >= eventTimeStartToPrint && eventTime <= eventTimeEndToPrint) {
                    System.out.println("Event ix: " + eventIx
                         + " / " + mesgEvent
                         + " / " + mesgEventType
                         + " / " + eventData
                         + " @" + FitDateTime.toString(eventTime, fitFile.getDiffMinutesLocalUTC())
                        );
                }
                eventIx++;
            }
        }
    }
}
