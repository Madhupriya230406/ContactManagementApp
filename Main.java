import service.ContactService;
import view.ContactView;
import view.MainDashboardFrame;

import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application entry point for the CodeNova Contact Management Application.
 * Launches the modern Java Swing GUI on the Event Dispatch Thread (EDT) by default,
 * while maintaining backward compatibility with the CLI console mode via '--cli'.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize backend service with persistent CSV storage
        ContactService contactService = new ContactService("data/contacts.csv");

        // Optional CLI mode for scripting or headless terminal environments
        if (args.length > 0 && args[0].equalsIgnoreCase("--cli")) {
            runCliMode(contactService);
            return;
        }

        // Launch Modern Java Swing GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Apply System Look and Feel for native OS aesthetics
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("[GUI Notice] Native look and feel unavailable, using default Swing style.");
            }

            MainDashboardFrame dashboard = new MainDashboardFrame(contactService);
            dashboard.setVisible(true);
        });
    }

    /**
     * Executes the interactive console-based view.
     */
    private static void runCliMode(ContactService contactService) {
        try (Scanner scanner = new Scanner(System.in)) {
            ContactView view = new ContactView(contactService, scanner);

            boolean running = true;
            System.out.println("\nWelcome to CodeNova Contact Management App (CLI Mode)!");
            System.out.println("Persistent CSV storage initialized at: data/contacts.csv");

            while (running) {
                try {
                    view.displayMainMenu();
                    String input = scanner.nextLine().trim();

                    if (input.isEmpty()) {
                        System.out.println("[!] Input cannot be empty. Please enter a number between 1 and 7.");
                        continue;
                    }

                    int choice;
                    try {
                        choice = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        System.out.println("[!] Invalid input: \"" + input + "\" is not a number. Please enter 1-7.");
                        continue;
                    }

                    switch (choice) {
                        case 1:
                            view.handleAddContact();
                            break;
                        case 2:
                            view.handleViewAllContacts();
                            break;
                        case 3:
                            view.handleSearchContacts();
                            break;
                        case 4:
                            view.handleEditContact();
                            break;
                        case 5:
                            view.handleDeleteContact();
                            break;
                        case 6:
                            view.handleReload();
                            break;
                        case 7:
                            System.out.println("\nSaving final state and exiting... Goodbye!");
                            running = false;
                            break;
                        default:
                            System.out.println("[!] Option out of range. Please choose a number between 1 and 7.");
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("\n[Notice] Input stream closed. Exiting application.");
                    running = false;
                } catch (Exception e) {
                    System.err.println("\n[Unexpected Error] An error occurred: " + e.getMessage());
                    System.err.println("The application recovered safely. Returning to main menu.");
                }
            }
        }
    }
}
