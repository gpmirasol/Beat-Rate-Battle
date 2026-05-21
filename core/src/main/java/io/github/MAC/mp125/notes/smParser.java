package io.github.MAC.mp125.notes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import io.github.MAC.mp125.notes.GameNote;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class smParser {

    public static ConcurrentLinkedQueue<GameNote> parseChart(String internalAssetPath, int targetPlayerID) {
        ConcurrentLinkedQueue<GameNote> gameTimeline = new ConcurrentLinkedQueue<>();

        double bpm = 120.0;
        double offsetSeconds = 0.0;
        List<String> rawNoteLines = new ArrayList<>();

        // Use LibGDX file internal handler to native asset paths safely
        FileHandle file = Gdx.files.internal(internalAssetPath);
        if (!file.exists()) {
            Gdx.app.error("Parser", "Target .sm chart file not found: " + internalAssetPath);
            return gameTimeline;
        }

        // Phase 1: Read structural tags and isolate note mappings
        try (BufferedReader reader = new BufferedReader(file.reader())) {
            String line;
            boolean insideNotesSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                if (line.startsWith("#BPMS:")) {
                    String content = line.substring(6, line.indexOf(";"));
                    String[] parts = content.split("=");
                    bpm = Double.parseDouble(parts[1]);
                } else if (line.startsWith("#OFFSET:")) {
                    String content = line.substring(8, line.indexOf(";"));
                    offsetSeconds = Double.parseDouble(content);
                } else if (line.startsWith("#NOTES:")) {
                    insideNotesSection = true;
                    // Skip the 5 metadata rows inside StepMania declarations
                    for (int i = 0; i < 5; i++) {
                        reader.readLine();
                    }
                    continue;
                }

                if (insideNotesSection) {
                    if (line.startsWith(";")) {
                        insideNotesSection = false;
                    } else {
                        rawNoteLines.add(line);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            Gdx.app.error("Parser", "Error reading chart bytes: " + e.getMessage());
            return gameTimeline;
        }

        // Phase 2: Compute math spacing rules
        double beatsPerSecond = bpm / 60.0;
        double secondsPerMeasure = 4.0 / beatsPerSecond;
        double msPerMeasure = secondsPerMeasure * 1000.0;
        long globalSongStartOffsetMs = Math.round(offsetSeconds * 1000.0);

        List<List<String>> measuresList = new ArrayList<>();
        List<String> currentMeasure = new ArrayList<>();

        for (String row : rawNoteLines) {
            if (row.contains(",")) {
                measuresList.add(currentMeasure);
                currentMeasure = new ArrayList<>();
            } else if (!row.startsWith("//")) {
                currentMeasure.add(row);
            }
        }
        if (!currentMeasure.isEmpty()) {
            measuresList.add(currentMeasure);
        }

        // Phase 3: Build Note objects dynamically
        double accumulatedTimeMs = globalSongStartOffsetMs;

        for (List<String> measure : measuresList) {
            int rowsInThisMeasure = measure.size();
            if (rowsInThisMeasure == 0)
                continue;

            double msPerRowFraction = msPerMeasure / rowsInThisMeasure;

            for (int rowIndex = 0; rowIndex < rowsInThisMeasure; rowIndex++) {
                String noteBitmask = measure.get(rowIndex);
                long accurateHitTimestampMs = Math.round(accumulatedTimeMs + (rowIndex * msPerRowFraction));

                for (int laneColumn = 0; laneColumn < 4; laneColumn++) {
                    if (laneColumn < noteBitmask.length() && noteBitmask.charAt(laneColumn) == '1') {
                        gameTimeline.add(new GameNote(accurateHitTimestampMs, laneColumn, targetPlayerID));
                    }
                }
            }
            accumulatedTimeMs += msPerMeasure;
        }

        Gdx.app.log("Parser", "Loaded " + gameTimeline.size() + " active notes from .sm for Player " + targetPlayerID);
        return gameTimeline;
    }
}