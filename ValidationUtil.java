package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing regular expression validations and sanitization.
 * Enforces business constraints for contact names, phone numbers, and emails.
 */
public final class ValidationUtil {

    // Regex requiring exactly 10 numeric digits
    private static final String PHONE_REGEX = "^[0-9]{10}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    // RFC-compliant standard email regex pattern
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Validates person name: letters, spaces, hyphens, periods, between 2 and 50 characters
    private static final String NAME_REGEX = "^[a-zA-Z\\s.'-]{2,50}$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    // Private constructor to prevent instantiation of utility class
    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Validates whether the given phone number consists of exactly 10 digits.
     *
     * @param phone the phone string to check
     * @return true if valid exactly 10 digits, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        String cleaned = phone.trim();
        return PHONE_PATTERN.matcher(cleaned).matches();
    }

    /**
     * Validates an email address against standard email syntax rules.
     *
     * @param email the email string to check
     * @return true if valid email format, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String cleaned = email.trim();
        return EMAIL_PATTERN.matcher(cleaned).matches();
    }

    /**
     * Validates a contact's full name.
     *
     * @param name the name string to check
     * @return true if valid name, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        String cleaned = name.trim();
        return NAME_PATTERN.matcher(cleaned).matches();
    }

    /**
     * Cleans up input string by trimming leading/trailing whitespace.
     *
     * @param input raw input string
     * @return trimmed string or empty string if null
     */
    public static String sanitize(String input) {
        return (input == null) ? "" : input.trim();
    }
}
