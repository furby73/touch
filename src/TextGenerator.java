import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TextGenerator {
    static ArrayList<String> list_of_words;
    private static Statistic statistic;
    private static final Queue<String> lastUsedWords = new LinkedList<>();
    private static final int MAX_RECENT_WORDS = 100;

    private static void generate_text() {
        list_of_words = new ArrayList<>();
        try {
            File myObj = new File("file.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                list_of_words.add(data.trim());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static String getRandomWord() {
        if (list_of_words == null) generate_text();
        return list_of_words.get((new Random()).nextInt(list_of_words.size()));
    }

    public static String getOptimalWord() {
        if (list_of_words == null) generate_text();
        if (statistic == null) throw new IllegalStateException("Statistic object is not set");

        Set<String> recentWordsSet = new HashSet<>(lastUsedWords);
        Set<Character> typedLetters = statistic.getTypedLetters();

        double maxCombinedValue = -1;
        String optimalWord = "";
        ArrayList<String> untypedLetterWords = new ArrayList<>();

        for (String word : list_of_words) {
            if (recentWordsSet.contains(word)) {
                continue;
            }

            boolean hasUntypedLetter = false;
            double totalRelativeSpeed = 0;
            double totalRelativeFrequency = 0;
            int letterCount = 0;

            for (char letter : word.toCharArray()) {
                double relativeSpeed = statistic.getRelativeSpeed(letter);
                double relativeFrequency = statistic.getRelativeFrequency(letter);
                if (relativeSpeed > 0) {
                    totalRelativeSpeed += relativeSpeed;
                    totalRelativeFrequency += relativeFrequency;
                    letterCount++;
                } else if (!typedLetters.contains(letter)) {
                    untypedLetterWords.add(word);
                }
            }

            if (letterCount > 0) {
                double combinedValue = (totalRelativeSpeed + totalRelativeFrequency) / letterCount;
                if (combinedValue > maxCombinedValue) {
                    maxCombinedValue = combinedValue;
                    optimalWord = word;
                }
            }
        }

        if (!untypedLetterWords.isEmpty()) {
            optimalWord = untypedLetterWords.remove(new Random().nextInt(untypedLetterWords.size()));
        } else if (optimalWord.isEmpty()) {
            return getRandomWord();
        }

        lastUsedWords.add(optimalWord);
        if (lastUsedWords.size() > MAX_RECENT_WORDS) {
            lastUsedWords.poll();
        }

        return optimalWord;
    }

    public static void setStatistic(Statistic stat) {
        statistic = stat;
    }
}
