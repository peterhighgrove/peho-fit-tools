package se.peho.fittools.core.strings;

import com.garmin.fit.Mesg;
import com.garmin.fit.Sport;
import com.garmin.fit.SubSport;

import java.util.List;

public final class ActivityNamingStr {

    private ActivityNamingStr() {
    }

    public static String getFilenameAndSetNewSportProfileName(
            Long activityDateTimeLocal,
            String sportProfile,
            String wktName,
            Float totalDistance,
            Float totalTimerTime,
            String product) {

        return new FileBaseStr(
                new DTstr(activityDateTimeLocal).get(),
                sportProfile,
                wktName,
                new DistTimerStr(totalDistance, totalTimerTime).get(),
                product
        ).get();
    }

    public static String getFileNameBaseNewTime(
            Long activityDateTimeLocal,
            String sportProfile,
            Sport sport,
            SubSport subsport,
            String wktName,
            Float totalDistance,
            Float totalTimerTime,
            Integer manufacturerNo,
            int productNo,
            Float swVer,
            String activityNamnSuffix) {

        if (StringsDebug.enabled) System.out.println("----> New FilenameBase: ");
        if (StringsDebug.enabled) System.out.println("        DateTimeLocal: " + new DTstr(activityDateTimeLocal).get());
        if (StringsDebug.enabled) System.out.println("        SportProfile : " + new ProfileStr(sportProfile, sport, subsport).get());
        if (StringsDebug.enabled) System.out.println("        WktName      : " + new WorkoutStr(wktName).get());
        if (StringsDebug.enabled) System.out.println("        TimerDist    : " + new DistTimerStr(totalDistance, totalTimerTime).get());
        if (StringsDebug.enabled) System.out.println("        Product      : " + new ProductStr(manufacturerNo, productNo, swVer).get());
        if (StringsDebug.enabled) System.out.println("        Suffix      : " + activityNamnSuffix);

        return new FileBaseStr(
                new DTstr(activityDateTimeLocal).get(),
                new ProfileStr(sportProfile, sport, subsport).get(),
                new WorkoutStr(wktName).get(),
                new DistTimerStr(totalDistance, totalTimerTime).get(),
                new ProductStr(manufacturerNo, productNo, swVer).get(),
                activityNamnSuffix
        ).get();
    }

    public static String getFileNameBaseOrgTime(
            Long activityDateTimeLocalOrg,
            String sportProfile,
            Sport sport,
            SubSport subsport,
            String wktName,
            Float totalDistance,
            Float totalTimerTime,
            Integer manufacturerNo,
            int productNo,
            Float swVer,
            String activityNamnSuffix) {

        if (StringsDebug.enabled) System.out.println("----> Org FilenameBase: ");
        if (StringsDebug.enabled) System.out.println("        DateTimeLocal: " + new DTstr(activityDateTimeLocalOrg).get());
        if (StringsDebug.enabled) System.out.println("        SportProfile : " + new ProfileStr(sportProfile, sport, subsport).get());
        if (StringsDebug.enabled) System.out.println("        WktName      : " + new WorkoutStr(wktName).get());
        if (StringsDebug.enabled) System.out.println("        TimerDist    : " + new DistTimerStr(totalDistance, totalTimerTime).get());
        if (StringsDebug.enabled) System.out.println("        Product      : " + new ProductStr(manufacturerNo, productNo, swVer).get());
        if (StringsDebug.enabled) System.out.println("        Suffix      : " + activityNamnSuffix);

        return new FileBaseStr(
                new DTstr(activityDateTimeLocalOrg).get(),
                new ProfileStr(sportProfile, sport, subsport).get(),
                new WorkoutStr(wktName).get(),
                new DistTimerStr(totalDistance, totalTimerTime).get(),
                new ProductStr(manufacturerNo, productNo, swVer).get(),
                activityNamnSuffix
        ).get();
    }

    public static String getActivityNameStr(
            String sportProfile,
            Sport sport,
            SubSport subsport,
            String wktName,
            Float totalDistance,
            String activityNamnSuffix) {

        if (StringsDebug.enabled) System.out.println("----> New ProfileBaseStr: ");
        if (StringsDebug.enabled) System.out.println("        SportProfile : " + new ProfileStr(sportProfile, sport, subsport).get());
        if (StringsDebug.enabled) System.out.println("        WktName      : " + new WorkoutStr(wktName).get());
        if (StringsDebug.enabled) System.out.println("        Dist    : " + new Km1(totalDistance).get());
        if (StringsDebug.enabled) System.out.println("        Suffix      : " + activityNamnSuffix);

        return new NewProfileStr(
                new ProfileStr(sportProfile, sport, subsport).get(),
                new WorkoutStr(wktName).get(),
                new Km1(totalDistance).get(),
                activityNamnSuffix
        ).get();
    }

    public static String setNewSportProfileName(
            List<Mesg> sessionMesg,
            int sesProfileFieldNum,
            String sportProfile,
            Sport sport,
            SubSport subsport,
            String wktName,
            Float totalDistance,
            String activityNamnSuffix) {

        String newActivityName = getActivityNameStr(
                sportProfile,
                sport,
                subsport,
                wktName,
                totalDistance,
                activityNamnSuffix
        );

        if (sessionMesg != null && !sessionMesg.isEmpty()) {
            Mesg session = sessionMesg.get(0);
            session.setFieldValue(sesProfileFieldNum, newActivityName);
        }

        return newActivityName;
    }
}