package io.github.MAC.mp125.notes;

public class GameNote {
    public long targetTimeMs; // The exact millisecond this note hits the receptor line
    public int laneIndex; // 0 = Left, 1 = Down, 2 = Up, 3 = Right
    public int playerID; // 1 = Player 1, 2 = Player 2
    public float yCoordinate; // Used by your LibGDX sprite renderer to draw the arrow
    public boolean isHoldNote = false;
    public long endTimeMs = 0;
    public boolean isBeingHeld = false;

    public GameNote(long targetTimeMs, int laneIndex, int playerID) {
        this.targetTimeMs = targetTimeMs;
        this.laneIndex = laneIndex;
        this.playerID = playerID;
    }

    /**
     * Translates time differences directly into screen pixels.
     * 
     * @param currentSongTimeMs Master atomic clock value from the audio worker.
     */
    public void updateVisualPosition(long currentSongTimeMs) {
        // Adjust this factor to make arrows scroll faster or slower overall
        float scrollSpeedFactor = 1.5f;

        long timeRemaining = this.targetTimeMs - currentSongTimeMs;
        this.yCoordinate = timeRemaining * scrollSpeedFactor;
    }
}