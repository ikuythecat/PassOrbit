import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PassOrbit {

    // ===== SHA256 HASH FUNCTION =====
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // ===== SIMPLE XOR ENCRYPTION =====
    public static String xorEncryptDecrypt(String data, String key) {
        char[] output = data.toCharArray();

        for (int i = 0; i < data.length(); i++) {
            output[i] = (char) (data.charAt(i) ^ key.charAt(i % key.length()));
        }

        return new String(output);
    }

    // ===== SAVE PASSWORD ENTRY =====
    public static void saveEntry(String site, String username, String password, String key) {

        try {
            FileWriter file = new FileWriter("vault.dat", true);

            String data = site + "|" + username + "|" + password + "\n";
            String encrypted = xorEncryptDecrypt(data, key);

            file.write(encrypted);
            file.close();

        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }

    // ===== VIEW PASSWORDS =====
    public static void viewEntries(String key) {

        try {
            BufferedReader file = new BufferedReader(new FileReader("vault.dat"));
            String line;

            while ((line = file.readLine()) != null) {
                String decrypted = xorEncryptDecrypt(line, key);
                System.out.println(decrypted);
            }

            file.close();

        } catch (IOException e) {
            System.out.println("Vault empty or not found.");
        }
    }

    // ===== SEED PHRASE GENERATOR =====
    public static String generateSeedPhrase(int numWords) {

        String[] wordlist = {
            "abandon","ability","able","about","above",
            "bamboo","banana","banner","bar","barely",
            "chef","cherry","chest","chicken","chief",
            "define","defy","degree","delay","deliver",
            "eager","eagle","early","earn","earth",
            "fence","festival","fetch","fever","few",
            "gadget","gain","galaxy","gallery","game",
            "habit","hair","half","hammer","hamster",
            "ice","icon","idea","identify","idle",
            "jacket","jaguar","jar","jazz","jealous",
            "kangaroo","keen","keep","ketchup","key",
            "lab","label","labor","ladder","lady",
            "machine","mad","magic","magnet","maid",
            "naive","name","napkin","narrow","nasty",
            "oak","obey","object","oblige","obscure",
            "pact","paddle","page","pair","palace",
            "quality","quantum","quarter","question","quick",
            "rabbit","raccoon","race","rack","radar",
            "sad","saddle","sadness","safe","sail",
            "table","tackle","tag","tail","talent",
            "ugly","umbrella","unable","unaware","uncle",
            "vacant","vacuum","vague","valid","valley",
            "wage","wagon","wait","walk","wall",
            "yard","year","yellow","you","young",
            "zebra","zero","zone","zoo"
        };

        Random rand = new Random();
        StringBuilder phrase = new StringBuilder();

        for (int i = 0; i < numWords; i++) {
            phrase.append(wordlist[rand.nextInt(wordlist.length)]);
            if (i < numWords - 1) phrase.append(" ");
        }

        return phrase.toString();
    }

    // ===== MAIN PROGRAM =====
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String masterPassword;
        String storedHash = "";

        File check = new File("master.dat");

        try {

            if (!check.exists()) {

                System.out.print("Set a new master password: ");
                masterPassword = scanner.nextLine();

                FileWriter out = new FileWriter("master.dat");
                out.write(sha256(masterPassword));
                out.close();

                System.out.println("Master password created!");

            } else {

                BufferedReader reader = new BufferedReader(new FileReader("master.dat"));
                storedHash = reader.readLine();
                reader.close();

                System.out.print("Enter master password: ");
                masterPassword = scanner.nextLine();

                if (!sha256(masterPassword).equals(storedHash)) {
                    System.out.println("Access denied!");
                    return;
                }

                System.out.println("Access granted!");
            }

            int choice;

            do {

                System.out.println("\n=== PassOrbit ===");
                System.out.println("1. Add Password");
                System.out.println("2. View Passwords");
                System.out.println("3. Generate Seed Phrase");
                System.out.println("4. Exit");
                System.out.print("Choose: ");

                choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {

                    System.out.print("Site: ");
                    String site = scanner.nextLine();

                    System.out.print("Username: ");
                    String username = scanner.nextLine();

                    System.out.print("Password: ");
                    String password = scanner.nextLine();

                    saveEntry(site, username, password, masterPassword);
                    System.out.println("Saved securely!");

                }

                else if (choice == 2) {
                    viewEntries(masterPassword);
                }

                else if (choice == 3) {
                    System.out.println("\nGenerated Seed Phrase:");
                    System.out.println("----------------------");
                    System.out.println(generateSeedPhrase(12));
                    System.out.println("----------------------");
                }

            } while (choice != 4);

        } catch (IOException e) {
            System.out.println("File error.");
        }

        scanner.close();
    }
}