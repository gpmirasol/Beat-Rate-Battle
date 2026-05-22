package io.github.MAC.mp125;

public class PlayerStats {
    public int totalNotes = 0;
    public int maxCombo = 0;
    public int currentCombo = 0;
    public int perfects = 0;
    public int goods = 0;
    public int mehs = 0;
    public int misses = 0;
    public int score = 0;

    public void recordHit(io.github.MAC.mp125.notes.HitDetector.HitGrade grade) {
        if (grade == io.github.MAC.mp125.notes.HitDetector.HitGrade.NONE) return;
        
        totalNotes++;
        if (grade != io.github.MAC.mp125.notes.HitDetector.HitGrade.MISS) {
            currentCombo++;
            if (currentCombo > maxCombo) {
                maxCombo = currentCombo;
            }
        } else {
            currentCombo = 0;
            misses++;
            return;
        }

        switch(grade) {
            case PERFECT: perfects++; break;
            case GOOD: goods++; break;
            case MEH: mehs++; break;
            default: break;
        }
    }
    
    public void recordMiss() {
        totalNotes++;
        currentCombo = 0;
        misses++;
    }
}
