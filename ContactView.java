package view;

import model.Contact;
import service.ContactService;
import util.ValidationUtil;

import java.util.List;
import java.util.Scanner;

/**
 * Presentation layer managing Console User Interface, ASCII table rendering,
 * and user input validation loops with graceful cancellation options.
 */
public class ContactView {

    private final ContactService contactService;
    private final Scanner scanner;

    public ContactView(ContactService contactService, Scanner scanner) {
        this.contactService = contactService;
        this.scanner = scanner;
    }

    /**
     * Displays application banner and main menu.
     */
    public void displayMainMenu() {
        System.out.println();
        System.out.println("==========================================================");
        System.out.println("         CODENOVA CONTACT MANAGEMENT APPLICATION          ");
        System.out.println("==========================================================");
        System.out.printf(" Total Contacts Stored: %d%n", contactService.getContactCount());
        System.out.println("----------------------------------------------------------");
        System.out.println("  1. Add New Contact");
        System.out.println("  2. View All Contacts");
        System.out.println("  3. Search Contacts (by Name or Phone)");
        System.out.println("  4. Edit an Existing Contact");
        System.out.println("  5. Delete a Contact");
        System.out.println("  6. Reload Contacts from Storage");
        System.out.println("  7. Exit");
        System.out.println("==========================================================");
        System.out.print("Please enter your choice (1-7): ");
    }

    /**
     * Prompts user through adding a new contact with inline validation.
     */
    public void handleAddContact() {
        System.out.println("\n--- [Add New Contact] ---");
        System.out.println("(Enter '0' at any prompt to cancel and return to main menu)");

        // 1. Name input
        String name;
        while (true) {
            System.out.print("Enter Full Name: ");
            name = scanner.nextLine().trim();
            if (name.equals("0")) {
                System.out.println("-> Action cancelled.");
                return;
            }
            if (ValidationUtil.isValidName(name)) {
                break;
            }
            System.out.println("[!] Invalid Name. Must be 2-50 characters with letters, spaces, or hyphens.");
        }

        // 2. Phone input
        String phone;
        while (true) {
            System.out.print("Enter 10-Digit Mobile Number: ");
            phone = scanner.nextLine().trim();
            if (phone.equals("0")) {
                System.out.println("-> Action cancelled.");
                return;
            }
            if (!ValidationUtil.isValidPhone(phone)) {
                System.out.println("[!] Invalid Mobile Number. Must contain exactly 10 digits (e.g., 9876543210).");
                continue;
            }
            if (contactService.getContactByPhone(phone) != null) {
                System.out.println("[!] Duplicate Error: A contact with phone number " + phone + " already exists!");
                continue;
            }
            break;
        }

        // 3. Email input
        String email;
        while (true) {
            System.out.print("Enter Email Address: ");
            email = scanner.nextLine().trim();
            if (email.equals("0")) {
                System.out.println("-> Action cancelled.");
                return;
            }
            if (ValidationUtil.isValidEmail(email)) {
                break;
            }
            System.out.println("[!] Invalid Email format. Example: user@example.com");
        }

        Contact contact = new Contact(name, phone, email);
        ContactService.ServiceResult result = contactService.addContact(contact);
        if (result.isSuccess()) {
            System.out.println("[SUCCESS] " + result.getMessage());
        } else {
            System.out.println("[ERROR] " + result.getMessage());
        }
    }

    /**
     * Renders all contacts in a formatted tabular layout.
     */
    public void handleViewAllContacts() {
        System.out.println("\n--- [All Contacts Directory] ---");
        List<Contact> contacts = contactService.getAllContacts();
        renderContactTable(contacts);
    }

    /**
     * Prompts user for search strategy (by name or phone number) and displays results.
     */
    public void handleSearchContacts() {
        System.out.println("\n--- [Search Contacts] ---");
        System.out.println("1. Search by Name (Partial or Full)");
        System.out.println("2. Search by Phone Number");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose search method: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.print("Enter Name keyword to search: ");
                String nameQuery = scanner.nextLine().trim();
                if (nameQuery.isBlank()) {
                    System.out.println("[!] Search keyword cannot be blank.");
                    return;
                }
                List<Contact> nameResults = contactService.searchByName(nameQuery);
                System.out.printf("%nFound %d matching contact(s) for name \"%s\":%n", nameResults.size(), nameQuery);
                renderContactTable(nameResults);
                break;

            case "2":
                System.out.print("Enter Phone number digits to search: ");
                String phoneQuery = scanner.nextLine().trim();
                if (phoneQuery.isBlank()) {
                    System.out.println("[!] Phone search query cannot be blank.");
                    return;
                }
                List<Contact> phoneResults = contactService.searchByPhone(phoneQuery);
                System.out.printf("%nFound %d matching contact(s) for phone \"%s\":%n", phoneResults.size(), phoneQuery);
                renderContactTable(phoneResults);
                break;

            case "0":
                return;

            default:
                System.out.println("[!] Invalid option selected.");
        }
    }

    /**
     * Prompts user to edit an existing contact by phone number.
     */
    public void handleEditContact() {
        System.out.println("\n--- [Edit Contact] ---");
        System.out.print("Enter the 10-Digit Phone Number of the contact to edit (or '0' to cancel): ");
        String phone = scanner.nextLine().trim();

        if (phone.equals("0")) {
            System.out.println("-> Action cancelled.");
            return;
        }

        Contact existing = contactService.getContactByPhone(phone);
        if (existing == null) {
            System.out.println("[!] No contact found with phone number: " + phone);
            return;
        }

        System.out.println("\nCurrent Contact Details:");
        System.out.println("  Name : " + existing.getName());
        System.out.println("  Phone: " + existing.getPhoneNumber());
        System.out.println("  Email: " + existing.getEmail());
        System.out.println("(Press [ENTER] without typing to retain the current value)");

        // 1. Edit Name
        String newName;
        while (true) {
            System.out.print("Enter New Name [" + existing.getName() + "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                newName = existing.getName();
                break;
            }
            if (ValidationUtil.isValidName(input)) {
                newName = input;
                break;
            }
            System.out.println("[!] Invalid Name. Must be 2-50 characters with letters, spaces, or hyphens.");
        }

        // 2. Edit Phone Number
        String newPhone;
        while (true) {
            System.out.print("Enter New 10-Digit Phone Number [" + existing.getPhoneNumber() + "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                newPhone = existing.getPhoneNumber();
                break;
            }
            if (!ValidationUtil.isValidPhone(input)) {
                System.out.println("[!] Invalid Mobile Number. Must contain exactly 10 digits.");
                continue;
            }
            if (!input.equals(existing.getPhoneNumber()) && contactService.getContactByPhone(input) != null) {
                System.out.println("[!] Duplicate Error: Phone number " + input + " belongs to another contact.");
                continue;
            }
            newPhone = input;
            break;
        }

        // 3. Edit Email
        String newEmail;
        while (true) {
            System.out.print("Enter New Email [" + existing.getEmail() + "]: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                newEmail = existing.getEmail();
                break;
            }
            if (ValidationUtil.isValidEmail(input)) {
                newEmail = input;
                break;
            }
            System.out.println("[!] Invalid Email format. Example: user@example.com");
        }

        ContactService.ServiceResult result = contactService.updateContact(existing.getPhoneNumber(), newName, newPhone, newEmail);
        if (result.isSuccess()) {
            System.out.println("[SUCCESS] " + result.getMessage());
        } else {
            System.out.println("[ERROR] " + result.getMessage());
        }
    }

    /**
     * Prompts user to delete a contact with explicit confirmation.
     */
    public void handleDeleteContact() {
        System.out.println("\n--- [Delete Contact] ---");
        System.out.print("Enter the 10-Digit Phone Number of the contact to delete (or '0' to cancel): ");
        String phone = scanner.nextLine().trim();

        if (phone.equals("0")) {
            System.out.println("-> Action cancelled.");
            return;
        }

        Contact existing = contactService.getContactByPhone(phone);
        if (existing == null) {
            System.out.println("[!] No contact found with phone number: " + phone);
            return;
        }

        System.out.printf("Are you sure you want to delete '%s' (%s)? (y/N): ", existing.getName(), existing.getPhoneNumber());
        String confirmation = scanner.nextLine().trim();
        if (confirmation.equalsIgnoreCase("y") || confirmation.equalsIgnoreCase("yes")) {
            ContactService.ServiceResult result = contactService.deleteContact(phone);
            if (result.isSuccess()) {
                System.out.println("[SUCCESS] " + result.getMessage());
            } else {
                System.out.println("[ERROR] " + result.getMessage());
            }
        } else {
            System.out.println("-> Deletion cancelled by user.");
        }
    }

    /**
     * Reloads contacts from CSV storage.
     */
    public void handleReload() {
        System.out.println("\n--- [Reload Contacts from Storage] ---");
        contactService.loadContacts();
        System.out.printf("[SUCCESS] Reload complete. Loaded %d contact(s) from CSV.%n", contactService.getContactCount());
    }

    /**
     * Formats and prints a clean ASCII table of contacts.
     */
    private void renderContactTable(List<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            System.out.println("+---------------------------------------------------------------------------------------------+");
            System.out.println("|                                   No contacts found.                                        |");
            System.out.println("+---------------------------------------------------------------------------------------------+");
            return;
        }

        String border = "+-----+----------------------+--------------+--------------------------------+---------------------+";
        System.out.println(border);
        System.out.printf("| %-3s | %-20s | %-12s | %-30s | %-19s |%n",
                "#", "Full Name", "Phone", "Email Address", "Created At");
        System.out.println(border);

        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            String displayCreated = (c.getCreatedAt() != null && !c.getCreatedAt().isBlank()) ? c.getCreatedAt() : "N/A";
            System.out.printf("| %-3d | %-20s | %-12s | %-30s | %-19s |%n",
                    (i + 1),
                    truncate(c.getName(), 20),
                    truncate(c.getPhoneNumber(), 12),
                    truncate(c.getEmail(), 30),
                    truncate(displayCreated, 19));
        }
        System.out.println(border);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return "";
        if (value.length() <= maxLength) return value;
        return value.substring(0, maxLength - 3) + "...";
    }
}
