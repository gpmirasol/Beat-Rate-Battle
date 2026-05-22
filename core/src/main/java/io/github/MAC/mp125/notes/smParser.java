package io.github.MAC.mp125.notes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class smParser {

    public static ConcurrentLinkedQueue<GameNote> parseChart(String internalAssetPath, int targetPlayerID) {
        ConcurrentLinkedQueue<GameNote> gameTimeline = new ConcurrentLinkedQueue<>();

        TreeMap<Double, Double> bpmChanges = new TreeMap<>();
        double offsetSeconds = 0.0;
        List<String> rawNoteLines = new ArrayList<>();

        // find .sm file in assets
        FileHandle file = Gdx.files.internal(internalAssetPath);
        if (!file.exists()) {
            Gdx.app.error("Parser", "Target .sm chart file not found: " + internalAssetPath);
            return gameTimeline;
        }

        // read .sm file and extract note mappings
        try (BufferedReader reader = new BufferedReader(file.reader())) {
            String line;
            boolean insideNotesSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                if (line.startsWith("#BPMS:")) {
                    StringBuilder bpmString = new StringBuilder(line.substring(6));
                    while (!bpmString.toString().contains(";") && reader.ready()) {
                        String nextLine = reader.readLine();
                        if (nextLine == null)
                            break;
                        bpmString.append(nextLine.trim());
                    }
                    String content = bpmString.toString().replace(";", "");
                    String[] changes = content.split(",");
                    for (String change : changes) {
                        String[] parts = change.split("=");
                        if (parts.length == 2) {
                            bpmChanges.put(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                        }
                    }
                } else if (line.startsWith("#OFFSET:")) {
                    int semiIndex = line.indexOf(";");
                    String content = (semiIndex != -1) ? line.substring(8, semiIndex) : line.substring(8);
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

        // breaks the .sm into measures
        if (bpmChanges.isEmpty()) {
            bpmChanges.put(0.0, 120.0);
        }

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

        // turns the measures into actual notes
        double accumulatedTimeMs = -offsetSeconds * 1000.0;
        GameNote[] activeHolds = new GameNote[4];
        double currentBeat = 0.0;

        for (List<String> measure : measuresList) {
            int rowsInThisMeasure = measure.size();
            if (rowsInThisMeasure == 0)
                continue;

            double beatsPerRow = 4.0 / rowsInThisMeasure;

            for (int rowIndex = 0; rowIndex < rowsInThisMeasure; rowIndex++) {
                Map.Entry<Double, Double> entry = bpmChanges.floorEntry(currentBeat + 0.001);
                double activeBpm = (entry != null) ? entry.getValue() : 120.0;
                double msPerRow = (beatsPerRow * 60.0 / activeBpm) * 1000.0;

                long accurateHitTimestampMs = Math.round(accumulatedTimeMs);

                String noteBitmask = measure.get(rowIndex);
                for (int laneColumn = 0; laneColumn < 4; laneColumn++) {
                    if (laneColumn < noteBitmask.length()) {
                        char c = noteBitmask.charAt(laneColumn);
                        if (c == '1') {
                            gameTimeline.add(new GameNote(accurateHitTimestampMs, laneColumn, targetPlayerID));
                        } else if (c == '2') {
                            GameNote holdNote = new GameNote(accurateHitTimestampMs, laneColumn, targetPlayerID);
                            holdNote.isHoldNote = true;
                            activeHolds[laneColumn] = holdNote;
                            gameTimeline.add(holdNote);
                        } else if (c == '3') {
                            if (activeHolds[laneColumn] != null) {
                                activeHolds[laneColumn].endTimeMs = accurateHitTimestampMs;
                                activeHolds[laneColumn] = null;
                            }
                        }
                    }
                }

                accumulatedTimeMs += msPerRow;
                currentBeat += beatsPerRow;
            }
        }

        Gdx.app.log("Parser", "Loaded " + gameTimeline.size() + " active notes from .sm for Player " + targetPlayerID);
        return gameTimeline;
    }
}