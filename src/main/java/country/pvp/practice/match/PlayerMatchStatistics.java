package country.pvp.practice.match;

import lombok.Data;

@Data
public class PlayerMatchStatistics {

    private int hits;
    private int longestCombo;
    int currentCombo;
    int potionsThrown;
    int potionsMissed;

    public void handleHit() {
        hits++;

        if (++currentCombo > longestCombo) {
            longestCombo = currentCombo;
        }
    }

    public void handleBeingHit() {
        currentCombo = 0;
    }

    public double getPotionAccuracy() {
        if (potionsMissed == 0) {
            return 100.0;
        } else if (potionsThrown == potionsMissed) {
            return 50.0;
        }

        return Math.round(100.0D - (((double) potionsMissed / (double) potionsThrown) * 100.0D));
    }

    public void increaseMissedPotions() {
        potionsMissed++;
    }

    public void increasedThrownPotions() {
        potionsThrown++;
    }

}
