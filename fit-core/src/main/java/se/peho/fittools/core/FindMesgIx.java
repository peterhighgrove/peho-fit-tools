package se.peho.fittools.core;

import java.util.List;

import com.garmin.fit.Event;
import com.garmin.fit.EventMesg;
import com.garmin.fit.EventType;
import com.garmin.fit.Mesg;
import com.garmin.fit.MesgNum;

public final class FindMesgIx {

    private FindMesgIx() {
    }

    public static int findFirstMesgIndex(List<Mesg> mesgs, int mesgNum) {
        for (int i = 0; i < mesgs.size(); i++) {
            if (mesgs.get(i).getNum() == mesgNum) {
                return i;
            }
        }
        return -1;
    }

    public static int findLastMesgIndex(List<Mesg> mesgs, int mesgNum) {
        for (int i = mesgs.size() - 1; i >= 0; i--) {
            if (mesgs.get(i).getNum() == mesgNum) {
                return i;
            }
        }
        return -1;
    }

    public static int findIxInAllMesgBasedOnTime(List<Mesg> allMesg, Long timeToSearchFor, int recTimeFieldNum) {
        int ix = 0;
        for (Mesg mesg : allMesg) {
            if (mesg.getNum() == MesgNum.RECORD) {
                Long recTime = mesg.getFieldLongValue(recTimeFieldNum);
                if (recTime != null && recTime.equals(timeToSearchFor)) {
                    break;
                }
            }
            ix += 1;
        }
        return ix;
    }

    public static int findIxInRecordMesgBasedOnTime(List<Mesg> recordMesg, Long timeToSearchFor, int recTimeFieldNum) {
        int ix = 0;
        for (Mesg mesg : recordMesg) {
            Long recTime = mesg.getFieldLongValue(recTimeFieldNum);
            if (recTime != null && recTime.equals(timeToSearchFor)) {
                break;
            }
            ix += 1;
        }
        return ix;
    }

    public static int findLastTimerStopEventIndex(List<Mesg> mesgs) {
        for (int i = mesgs.size() - 1; i >= 0; i--) {
            Mesg mesg = mesgs.get(i);
            if (mesg.getNum() != MesgNum.EVENT) {
                continue;
            }
            Short eventValue = mesg.getFieldShortValue(EventMesg.EventFieldNum);
            Short eventTypeValue = mesg.getFieldShortValue(EventMesg.EventTypeFieldNum);
            if (eventValue == null || eventTypeValue == null) {
                continue;
            }
            if (!eventValue.equals(Event.TIMER.getValue())) {
                continue;
            }
            EventType eventType = EventType.getByValue(eventTypeValue);
            String eventTypeName = eventType != null ? String.valueOf(eventType) : "";
            if ("STOP_ALL".equals(eventTypeName) || "STOP_DISABLE_ALL".equals(eventTypeName)) {
                return i;
            }
        }
        return -1;
    }

    public static int findFirstIndexAtOrAfter(List<Long> values, long targetValue, int fromIndex) {
        int startIndex = Math.max(0, fromIndex);
        for (int i = startIndex; i < values.size(); i++) {
            if (values.get(i) >= targetValue) {
                return i;
            }
        }
        return values.size() - 1;
    }

    public static int findMesgIndexByIntField(List<Mesg> mesgs, int mesgNum, int fieldNum, int targetValue) {
        for (int i = 0; i < mesgs.size(); i++) {
            Mesg mesg = mesgs.get(i);
            if (mesg.getNum() != mesgNum) {
                continue;
            }
            Integer value = mesg.getFieldIntegerValue(fieldNum);
            if (value != null && value == targetValue) {
                return i;
            }
        }
        return -1;
    }
}