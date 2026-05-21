package io.github.MAC.mp125.notes;

public class HitDetector {

    // These are the timing windows in milliseconds (how early/late a player can
    // be).
    // You can adjust these numbers to make the game easier or harder!
    public static final int WINDOW_PERFECT = 22;
    public static final int WINDOW_AMAZING = 45;
    public static final int WINDOW_GOOD = 90;
    public static final int WINDOW_MEH = 135;
    public static final int WINDOW_BAD = 180;

    public enum HitGrade {
        PERFECT, AMAZING, GOOD, MEH, BAD, MISS, NONE
    }

    /**
     * Call this when the player presses a button.
     * 
     * @param targetTimeMs      The exact time the note is supposed to be hit.
     * @param currentSongTimeMs The current time of the song.
     * @return The grade of the hit, or NONE if they pressed too early.
     */
    public static HitGrade evaluateHit(long targetTimeMs, long currentSongTimeMs) {
        long timeDifference = Math.abs(targetTimeMs - currentSongTimeMs);

        if (timeDifference <= WINDOW_PERFECT)
            return HitGrade.PERFECT;
        if (timeDifference <= WINDOW_AMAZING)
            return HitGrade.AMAZING;
        if (timeDifference <= WINDOW_GOOD)
            return HitGrade.GOOD;
        if (timeDifference <= WINDOW_MEH)
            return HitGrade.MEH;
        if (timeDifference <= WINDOW_BAD)
            return HitGrade.BAD;

        // If they pressed way too early, don't count it as a hit attempt yet
        return HitGrade.NONE;
    }

    /**
     * Call this inside your update/render loop to check if a note just scrolled
     * past the screen untouched.
     */
    public static boolean hasMissed(long targetTimeMs, long currentSongTimeMs) {
        // If the current time has passed the target time by MORE than the worst timing
        // window, it's a miss
        return (currentSongTimeMs - targetTimeMs) > WINDOW_BAD;
    }
}
