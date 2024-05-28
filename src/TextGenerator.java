import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class TextGenerator {
    static ArrayList<String> list_of_words;

    private static void generate_text() {
        list_of_words = new ArrayList<>();
        try {
            File myObj = new File("file.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                list_of_words.add(data);
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
        return "optimal";
    }

}
