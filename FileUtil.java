package util;

import model.Contact;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class providing CSV persistence operations for Contact records.
 * Manages directory creation, file initialization, CSV parsing, and atomic writes.
 */
public final class FileUtil {

    public static final String CSV_HEADER = "Name,PhoneNumber,Email,CreatedAt";

    private FileUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Ensures that the parent directory and the CSV file exist.
     * If the file is newly created, it writes the CSV header.
     *
     * @param filePath the path to the CSV file
     * @throws IOException if directory or file creation fails
     */
    public static void initializeFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Path parent = path.getParent();

        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        if (!Files.exists(path)) {
            Files.createFile(path);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
                writer.write(CSV_HEADER);
                writer.newLine();
            }
        }
    }

    /**
     * Reads all contacts from the specified CSV file.
     *
     * @param filePath path to the CSV file
     * @return list of parsed contacts
     */
    public static List<Contact> readContactsFromCsv(String filePath) {
        List<Contact> contacts = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                initializeFile(filePath);
            } catch (IOException e) {
                System.err.println("[File Warning] Could not initialize file " + filePath + ": " + e.getMessage());
                return contacts;
            }
            return contacts;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Read header line
            int lineNum = 1;

            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.isBlank()) {
                    continue;
                }

                try {
                    Contact contact = parseCsvLine(line);
                    if (contact != null) {
                        contacts.add(contact);
                    }
                } catch (Exception ex) {
                    System.err.printf("[CSV Warning] Skipping malformed row at line %d: %s (Reason: %s)%n",
                            lineNum, line, ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[File Error] Failed to read from CSV file " + filePath + ": " + e.getMessage());
        }

        return contacts;
    }

    /**
     * Writes the given list of contacts to the specified CSV file.
     * Overwrites existing content with the latest state.
     *
     * @param filePath path to the CSV file
     * @param contacts list of contacts to persist
     * @return true if save succeeded, false otherwise
     */
    public static boolean writeContactsToCsv(String filePath, List<Contact> contacts) {
        try {
            initializeFile(filePath);
        } catch (IOException e) {
            System.err.println("[File Error] Unable to prepare CSV destination: " + e.getMessage());
            return false;
        }

        File targetFile = new File(filePath);
        File tempFile = new File(filePath + ".tmp");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(CSV_HEADER);
            writer.newLine();

            if (contacts != null) {
                for (Contact c : contacts) {
                    writer.write(c.toCsvRow());
                    writer.newLine();
                }
            }
            writer.flush();
        } catch (IOException e) {
            System.err.println("[File Error] Failed writing temporary CSV file: " + e.getMessage());
            return false;
        }

        // Atomic replace with standard replacement fallback
        Path targetPath = targetFile.toPath();
        Path tempPath = tempFile.toPath();
        try {
            Files.move(tempPath, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            return true;
        } catch (IOException e) {
            // Fallback if atomic move unsupported by filesystem
            try {
                Files.move(tempPath, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException ex) {
                System.err.println("[File Error] Could not replace target CSV file: " + ex.getMessage());
                return false;
            }
        }
    }

    /**
     * Parses a single CSV line into a Contact object with support for quoted strings.
     *
     * @param line CSV line text
     * @return parsed Contact, or null if line format is invalid
     */
    public static Contact parseCsvLine(String line) {
        List<String> tokens = parseCsvTokens(line);
        if (tokens.size() < 3) {
            throw new IllegalArgumentException("Expected at least 3 columns (Name, Phone, Email), found " + tokens.size());
        }

        String name = tokens.get(0);
        String phone = tokens.get(1);
        String email = tokens.get(2);
        String createdAt = (tokens.size() >= 4) ? tokens.get(3) : "";

        return new Contact(name, phone, email, createdAt);
    }

    /**
     * Helper to tokenize CSV line respecting quotes and escaped quotes.
     */
    private static List<String> parseCsvTokens(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    // Escaped double quote ("")
                    sb.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }
}
