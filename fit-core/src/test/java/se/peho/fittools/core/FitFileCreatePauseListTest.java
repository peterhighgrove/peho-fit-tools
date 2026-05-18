package se.peho.fittools.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Assume;
import org.junit.Test;

import com.garmin.fit.EventMesg;
import com.garmin.fit.EventType;
import com.garmin.fit.FileIdMesg;
import com.garmin.fit.LapMesg;
import com.garmin.fit.Mesg;
import com.garmin.fit.RecordMesg;
import com.garmin.fit.TimeInZoneMesg;

import se.peho.fittools.core.commands.ActivityAddAnother;

public class FitFileCreatePauseListTest {

    private Path resolveFixturePath(String relativePathFromRepoRoot) {
        Path fromWorkspaceRoot = Paths.get(relativePathFromRepoRoot);
        if (fromWorkspaceRoot.toFile().exists()) {
            return fromWorkspaceRoot.toAbsolutePath().normalize();
        }

        Path fromFitCoreModule = Paths.get("..", relativePathFromRepoRoot);
        if (fromFitCoreModule.toFile().exists()) {
            return fromFitCoreModule.toAbsolutePath().normalize();
        }

        return fromWorkspaceRoot.toAbsolutePath().normalize();
    }

    @Test
    public void createPauseList_handlesIndoorRecordsWithoutGpsOrAltitude() {
        FitFile fitFile = new FitFile();

        fitFile.setTimeLastRecord(1_500L);

        RecordMesg recordAtPauseStart = new RecordMesg();
        recordAtPauseStart.setTimestamp(new com.garmin.fit.DateTime(1_000L));
        recordAtPauseStart.setDistance(100f);

        RecordMesg recordAtPauseEnd = new RecordMesg();
        recordAtPauseEnd.setTimestamp(new com.garmin.fit.DateTime(1_100L));
        recordAtPauseEnd.setDistance(100f);

        fitFile.getRecordMesg().add(recordAtPauseStart);
        fitFile.getRecordMesg().add(recordAtPauseEnd);

        EventMesg stopAll = new EventMesg();
        stopAll.setTimestamp(new com.garmin.fit.DateTime(1_000L));
        stopAll.setEventType(EventType.STOP_ALL);

        EventMesg start = new EventMesg();
        start.setTimestamp(new com.garmin.fit.DateTime(1_100L));
        start.setEventType(EventType.START);

        fitFile.getEventTimerMesg().add((Mesg) stopAll);
        fitFile.getEventTimerMesg().add((Mesg) start);

        fitFile.createPauseList();

        assertEquals(1, fitFile.getPauseList().size());

        FitFile.PauseMesg pause = fitFile.getPauseList().get(0);
        assertEquals(0f, pause.getDistPause(), 0.001f);
        assertEquals(0f, pause.getAltPause(), 0.001f);
    }

    @Test
    public void activityAddAnother_handlesIndoorSkiergFiles_withoutPauseCrash() {
        Path basePath = resolveFixturePath("regression/wkt/skierg/.run/2026-02-14-15-11-11-SkiErg-6-5-4+2-3-2-1+1min-10.2km-50.13min-Friskis Orminge-garmin-e2pro-v25.11-org.fit");
        Path addPath = resolveFixturePath("regression/wkt/skierg/.run/2026-02-14-15-13-11-SkiErg-6-5-4+2-3-2-1+1min-10.2km-50.13min-Friskis Orminge-garmin-e2pro-v25.11-merged2min.fit");

        Assume.assumeTrue("Required FIT fixture missing: " + basePath, basePath.toFile().exists());
        Assume.assumeTrue("Required FIT fixture missing: " + addPath, addPath.toFile().exists());

        FitFile watchFitFile = new FitFile();
        watchFitFile.readFitFile(basePath.toString());

        int recordsBefore = watchFitFile.getRecordMesg().size();

        ActivityAddAnother command = new ActivityAddAnother();
        try (Scanner scanner = new Scanner(addPath.toString() + System.lineSeparator())) {
            command.run(scanner, watchFitFile);
        }

        int recordsAfter = watchFitFile.getRecordMesg().size();
        assertTrue("Expected added activity to increase record count", recordsAfter > recordsBefore);
        assertTrue("Expected pause list to be available after aa flow", watchFitFile.getPauseList().size() >= 1);
    }

    @Test
    public void activityReportGenerator_groupsConsecutiveRepeatedMesg() {
        FitFile fitFile = new FitFile();
        fitFile.getAllMesg().addAll(Arrays.asList(
            (Mesg) new FileIdMesg(),
            (Mesg) new RecordMesg(),
            (Mesg) new RecordMesg(),
            (Mesg) new EventMesg(),
            (Mesg) new EventMesg(),
            (Mesg) new EventMesg(),
            (Mesg) new LapMesg(),
            (Mesg) new RecordMesg()
        ));

        ActivityReportGenerator generator = new ActivityReportGenerator(fitFile);

        List<String> summary = generator.buildFileStructureSummary();

        assertEquals(Arrays.asList(
            "[0] FILE_ID(0)",
            "[1-2] RECORD(20) x2",
            "[3-5] EVENT(21) x3",
            "[6] LAP(19)",
            "[7] RECORD(20)"
        ), summary);
    }

    @Test
    public void activityReportGenerator_includesSecondMesgIndexesForRepeatedPair() {
        FitFile fitFile = new FitFile();

        for (int i = 0; i < 3; i++) {
            Mesg timeInZone = new TimeInZoneMesg();
            fitFile.getAllMesg().add(timeInZone);

            LapMesg lap = new LapMesg();
            lap.setMessageIndex(i);
            fitFile.getAllMesg().add(lap);
        }

        ActivityReportGenerator generator = new ActivityReportGenerator(fitFile);

        List<String> summary = generator.buildFileStructureSummary();

        assertEquals(Arrays.asList(
            "[0-4] TIME_IN_ZONE(216), [1-5:0-2] LAP(19) x3"
        ), summary);
    }
}