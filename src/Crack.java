/**
 * @author Trevor Horton
 * @author Jason Carr
 *
 * @since Version 1.0
 */
import org.apache.commons.codec.digest.Crypt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.stream.Stream;

public class Crack {
    private final User[] users;
    private final String dictionary;

    public Crack(String shadowFile, String dictionary) throws FileNotFoundException {
        this.dictionary = dictionary;
        this.users = Crack.parseShadow(shadowFile);
    }

    public void crack() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(dictionary);
        Scanner scanner = new Scanner(fis);
        while (scanner.hasNextLine()) {
            String word = scanner.nextLine();
            for (User user : users) {
                if (user.getPassHash().contains("$")) {
                    String hash = Crypt.crypt(word, user.getPassHash());
                    if (hash.equals(user.getPassHash())) {
                        System.out.println("Found password: " + word + " for user " + user.getUsername());
                    }
                }
            }
        }
    }

    public static int getLineCount(String path) {
        int lineCount = 0;
        try (Stream<String> stream = Files.lines(Path.of(path), StandardCharsets.UTF_8)) {
            lineCount = (int) stream.count();
        } catch (IOException ignored) {
        }
        return lineCount;
    }

    public static User[] parseShadow(String shadowFile) throws FileNotFoundException {
        int numLines = getLineCount(shadowFile);
        User[] users = new User[numLines];
        FileInputStream fis = new FileInputStream(shadowFile);
        Scanner scanner = new Scanner(fis);
        int i = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] fields = line.split(":");
            String username = fields[0];
            String password = fields[1];
            users[i] = new User(username, password);
            i++;
        }
        return users;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        String choice;
        String shadowPath;
        String dictPath;
        System.out.print("Type the path to your shadow file or D for default: ");
        choice = sc.nextLine();
        if (choice.equals("d")) {
            shadowPath = "resources/shadow";
        } else {
            shadowPath = choice;
        }
        System.out.print("Type the path to your dictionary file or D for default: ");
        choice = sc.nextLine();
        if (choice.equals("d")) {
            dictPath = "resources/englishSmall.dic";
        } else {
            dictPath = choice;
        }
        System.out.println();

        Crack c = new Crack(shadowPath, dictPath);
        c.crack();
    }
}
