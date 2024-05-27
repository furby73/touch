import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Statistic {
    private static class LetterStats {
        private final Queue<Long> times;
        private long totalTime;

        public LetterStats() {
            this.times = new LinkedList<>();
            this.totalTime = 0;
        }

        public void update(long time) {
            if (times.size() == 10) {
                totalTime -= times.poll();
            }
            times.add(time);
            totalTime += time;
        }

        public double getAverageTime() {
            if (times.isEmpty()) {
                return 0;
            }
            return (double) totalTime / times.size();
        }
    }

    private final Map<Character, LetterStats> letterStatsMap;

    public Statistic() {
        letterStatsMap = new HashMap<>();
    }

    public void updateStats(char letter, long time) {
        letterStatsMap.putIfAbsent(letter, new LetterStats());
        letterStatsMap.get(letter).update(time);
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
}
