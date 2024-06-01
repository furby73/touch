import java.util.*;

public class Statistic {
    private static class LetterStats {
        private final Queue<Long> times;
        private long totalTime;
        private int count;

        public LetterStats() {
            this.times = new LinkedList<>();
            this.totalTime = 0;
            this.count = 0;
        }

        public void update(long time) {
            if (times.size() == 10) {
                totalTime -= times.poll();
            }
            times.add(time);
            totalTime += time;
            count++;
        }

        public double getAverageTime() {
            if (times.isEmpty()) {
                return 0;
            }
            return (double) totalTime / times.size();
        }

        public int getCount() {
            return count;
        }
    }

    private final Map<Character, LetterStats> letterStatsMap;
    private double maxSpeed;
    private int maxCount;

    public Statistic() {
        letterStatsMap = new HashMap<>();
        maxSpeed = 0;
        maxCount = 0;
    }

    public void updateStats(char letter, long time) {
        letterStatsMap.putIfAbsent(letter, new LetterStats());
        LetterStats stats = letterStatsMap.get(letter);
        stats.update(time);

        double averageSpeed = stats.getAverageTime();
        if (averageSpeed > maxSpeed) {
            maxSpeed = averageSpeed;
        }

        int count = stats.getCount();
        if (count > maxCount) {
            maxCount = count;
        }
    }

    public double getStats(char letter) {
        if (!letterStatsMap.containsKey(letter)) {
            return 0;
        }
        return letterStatsMap.get(letter).getAverageTime();
    }

    public Map<Character, Double> getAllStats() {
        Map<Character, Double> allStats = new HashMap<>();
        for (Map.Entry<Character, LetterStats> entry : letterStatsMap.entrySet()) {
            allStats.put(entry.getKey(), entry.getValue().getAverageTime());
        }
        return allStats;
    }

    public Set<Character> getTypedLetters() {
        return new HashSet<>(letterStatsMap.keySet());
    }

    public double getRelativeSpeed(char letter) {
        updateMaxSpeed();
        if (!letterStatsMap.containsKey(letter) || maxSpeed == 0) {
            return 0;
        }
        return letterStatsMap.get(letter).getAverageTime() / maxSpeed;
    }

    public double getRelativeFrequency(char letter) {
        if (!letterStatsMap.containsKey(letter) || maxCount == 0) {
            return 0;
        }
        return (double) letterStatsMap.get(letter).getCount() / maxCount;
    }

    private void updateMaxSpeed() {
        maxSpeed = 0;
        for (LetterStats stats : letterStatsMap.values()) {
            double averageSpeed = stats.getAverageTime();
            if (maxSpeed == 0)
                maxSpeed = averageSpeed;
            if (averageSpeed > maxSpeed) {
                maxSpeed = averageSpeed;
            }
        }
    }
}
