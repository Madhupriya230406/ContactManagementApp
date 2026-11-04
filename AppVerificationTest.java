package test;

import model.Contact;
import service.ContactService;
import util.FileUtil;
import util.ValidationUtil;
import view.ContactTableModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Automated Verification Suite for CodeNova Contact Management App.
 * Verifies all 10 project requirements, data structures, regex validations,
 * persistence roundtrips, and exception handling.
 */
public class AppVerificationTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   CODENOVA CONTACT MANAGEMENT APP - VERIFICATION SUITE   ");
        System.out.println("==========================================================");

        try {
            testPhoneValidation();
            testEmailValidation();
            testNameValidation();
            testFileUtilPersistence();
            testContactServiceOperations();
            testPersistenceAndAutoReload();
            testGuiTableModel();

            System.out.println("\n----------------------------------------------------------");
            System.out.printf("Test Summary: %d / %d Tests Passed Successfully!%n", testsPassed, testsRun);
            System.out.println("----------------------------------------------------------");

            if (testsPassed == testsRun) {
                System.out.println("[ALL CHECKS PASSED] Application meets 100% of the project requirements.");
            } else {
                System.err.println("[FAILURES DETECTED] Some tests failed.");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("[FATAL] Test execution crashed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition, String failureDetails) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName + " -> " + failureDetails);
        }
    }

    private static void testPhoneValidation() {
        System.out.println("\n[Testing 10-Digit Mobile Number Validation]");
        assertTrue("Valid 10-digit number passes",
                ValidationUtil.isValidPhone("9876543210"), "Expected 9876543210 to be valid");
        assertTrue("Another valid 10-digit number passes",
                ValidationUtil.isValidPhone("8123456789"), "Expected 8123456789 to be valid");
        assertTrue("9 digits rejected",
                !ValidationUtil.isValidPhone("987654321"), "Expected 9 digits to be rejected");
        assertTrue("11 digits rejected",
                !ValidationUtil.isValidPhone("98765432100"), "Expected 11 digits to be rejected");
        assertTrue("Alphabetic string rejected",
                !ValidationUtil.isValidPhone("98765abcde"), "Expected non-digits to be rejected");
        assertTrue("Special characters rejected",
                !ValidationUtil.isValidPhone("98765-43210"), "Expected dashed string to be rejected");
        assertTrue("Null phone rejected",
                !ValidationUtil.isValidPhone(null), "Expected null to be rejected");
    }

    private static void testEmailValidation() {
        System.out.println("\n[Testing Email Address Validation]");
        assertTrue("Standard email passes",
                ValidationUtil.isValidEmail("john.doe@example.com"), "Expected john.doe@example.com to be valid");
        assertTrue("Email with subdomain passes",
                ValidationUtil.isValidEmail("user@corp.tech.org"), "Expected subdomain email to be valid");
        assertTrue("Missing @ symbol rejected",
                !ValidationUtil.isValidEmail("johndoeexample.com"), "Expected missing @ to be rejected");
        assertTrue("Missing domain extension rejected",
                !ValidationUtil.isValidEmail("john@example"), "Expected missing domain extension to be rejected");
        assertTrue("Spaces in email rejected",
                !ValidationUtil.isValidEmail("john doe@example.com"), "Expected space in email to be rejected");
        assertTrue("Null email rejected",
                !ValidationUtil.isValidEmail(null), "Expected null to be rejected");
    }

    private static void testNameValidation() {
        System.out.println("\n[Testing Name Validation]");
        assertTrue("Valid name passes",
                ValidationUtil.isValidName("Ananya Sen"), "Expected Ananya Sen to be valid");
        assertTrue("Name with hyphen passes",
                ValidationUtil.isValidName("Mary-Jane Watson"), "Expected Mary-Jane Watson to be valid");
        assertTrue("Single character rejected",
                !ValidationUtil.isValidName("A"), "Expected single char to be rejected");
        assertTrue("Blank name rejected",
                !ValidationUtil.isValidName("   "), "Expected blank name to be rejected");
        assertTrue("Numeric name rejected",
                !ValidationUtil.isValidName("Robot123"), "Expected numbers in name to be rejected");
    }

    private static void testFileUtilPersistence() throws IOException {
        System.out.println("\n[Testing CSV File I/O & Parsing]");
        String testFile = "data/test_temp_contacts.csv";
        File file = new File(testFile);
        if (file.exists()) {
            file.delete();
        }

        FileUtil.initializeFile(testFile);
        assertTrue("CSV file initialized", file.exists(), "File should be created");

        Contact c1 = new Contact("Test User", "9998887776", "test@test.com", "2026-10-30 12:00:00");
        Contact c2 = new Contact("Comma, User", "9998887775", "comma@test.com", "2026-10-30 12:05:00");

        boolean written = FileUtil.writeContactsToCsv(testFile, List.of(c1, c2));
        assertTrue("Contacts written to CSV successfully", written, "writeContactsToCsv should return true");

        List<Contact> read = FileUtil.readContactsFromCsv(testFile);
        assertTrue("Read exactly 2 contacts", read.size() == 2, "Expected 2 contacts, got " + read.size());
        assertTrue("Preserved contact with comma in name",
                read.get(1).getName().equals("Comma, User"), "Name with comma should parse accurately");

        file.delete();
        File temp = new File(testFile + ".tmp");
        if (temp.exists()) temp.delete();
    }

    private static void testContactServiceOperations() {
        System.out.println("\n[Testing ContactService Business Logic & Dual Data Structures]");
        String testDb = "data/test_service_db.csv";
        new File(testDb).delete();

        ContactService service = new ContactService(testDb);
        assertTrue("Initial service has 0 contacts", service.getContactCount() == 0, "Expected empty service");

        // 1. Add Contacts
        Contact c1 = new Contact("Zara Khan", "9870001111", "zara@khan.com");
        Contact c2 = new Contact("Abhinav Gupta", "9870002222", "abhinav@gupta.com");

        ContactService.ServiceResult r1 = service.addContact(c1);
        ContactService.ServiceResult r2 = service.addContact(c2);
        assertTrue("Contact 1 added", r1.isSuccess(), r1.getMessage());
        assertTrue("Contact 2 added", r2.isSuccess(), r2.getMessage());
        assertTrue("Total count is 2", service.getContactCount() == 2, "Expected 2 contacts");

        // 2. Duplicate Check
        Contact duplicate = new Contact("Fake Zara", "9870001111", "fake@domain.com");
        ContactService.ServiceResult dupResult = service.addContact(duplicate);
        assertTrue("Duplicate phone rejected", !dupResult.isSuccess(), "Duplicate should be rejected");

        // 3. Alphabetical Order in ArrayList
        List<Contact> all = service.getAllContacts();
        assertTrue("Contacts sorted alphabetically (Abhinav before Zara)",
                all.get(0).getName().equals("Abhinav Gupta") && all.get(1).getName().equals("Zara Khan"),
                "List should be sorted");

        // 4. O(1) Lookup by Phone
        Contact found = service.getContactByPhone("9870001111");
        assertTrue("Found contact via phone HashMap lookup",
                found != null && found.getName().equals("Zara Khan"), "Should find Zara Khan");

        // 5. Search by Name
        List<Contact> searchName = service.searchByName("abhi");
        assertTrue("Search by name substring returns matching contact",
                searchName.size() == 1 && searchName.get(0).getName().equals("Abhinav Gupta"),
                "Expected to find Abhinav Gupta");

        // 6. Search by Phone
        List<Contact> searchPhone = service.searchByPhone("0002222");
        assertTrue("Search by partial phone returns matching contact",
                searchPhone.size() == 1 && searchPhone.get(0).getName().equals("Abhinav Gupta"),
                "Expected to find Abhinav Gupta");

        // 7. Edit Contact
        ContactService.ServiceResult updateResult = service.updateContact(
                "9870002222", "Abhinav G. Verma", "9870003333", "abhinav.verma@newmail.com");
        assertTrue("Contact updated successfully", updateResult.isSuccess(), updateResult.getMessage());
        assertTrue("Old phone lookup returns null",
                service.getContactByPhone("9870002222") == null, "Old phone should be removed");
        assertTrue("New phone lookup returns updated contact",
                service.getContactByPhone("9870003333") != null &&
                        service.getContactByPhone("9870003333").getName().equals("Abhinav G. Verma"),
                "New phone should be present");

        // 7b. Attempt to edit to an already existing phone number (should be rejected)
        Contact c3 = new Contact("Devendra Sharma", "9870009999", "dev@sharma.in");
        service.addContact(c3);
        ContactService.ServiceResult dupEdit = service.updateContact(
                "9870009999", "Devendra S.", "9870003333", "dev.s@sharma.in");
        assertTrue("Duplicate phone rejected during edit", !dupEdit.isSuccess(), "Should reject existing phone 9870003333");
        service.deleteContact("9870009999");

        // 8. Delete Contact
        ContactService.ServiceResult delResult = service.deleteContact("9870001111");
        assertTrue("Contact deleted successfully", delResult.isSuccess(), delResult.getMessage());
        assertTrue("Count decremented to 1", service.getContactCount() == 1, "Expected 1 contact remaining");
        assertTrue("Deleted contact absent in HashMap", service.getContactByPhone("9870001111") == null, "HashMap should not have deleted contact");
        assertTrue("Deleted contact absent in ArrayList", service.getAllContacts().stream().noneMatch(c -> c.getPhoneNumber().equals("9870001111")), "ArrayList should not have deleted contact");

        new File(testDb).delete();
    }

    private static void testPersistenceAndAutoReload() {
        System.out.println("\n[Testing Automatic Persistence & Restart Reload]");
        String persistDb = "data/test_persist_db.csv";
        new File(persistDb).delete();

        // Session 1: Create and add contact
        ContactService session1 = new ContactService(persistDb);
        session1.addContact(new Contact("Rohan Mehra", "9112223334", "rohan@mehra.org"));
        session1.addContact(new Contact("Kavita Rao", "9556667778", "kavita.rao@mail.com"));
        assertTrue("Session 1 saved 2 contacts", session1.getContactCount() == 2, "Expected 2 contacts");

        // Session 2: Simulate application restart with brand new service instance
        ContactService session2 = new ContactService(persistDb);
        assertTrue("Session 2 auto-loaded contacts on startup",
                session2.getContactCount() == 2, "Expected 2 contacts restored from disk");
        assertTrue("Session 2 loaded Rohan Mehra",
                session2.getContactByPhone("9112223334") != null, "Rohan should exist");
        assertTrue("Session 2 loaded Kavita Rao",
                session2.getContactByPhone("9556667778") != null, "Kavita should exist");

        new File(persistDb).delete();
    }

    private static void testGuiTableModel() {
        System.out.println("\n[Testing Swing GUI ContactTableModel]");
        Contact c1 = new Contact("Anita Roy", "9111112222", "anita@roy.in", "2026-10-30 10:00:00");
        Contact c2 = new Contact("Bikram Das", "9333334444", "bikram@das.in", "2026-10-30 11:00:00");

        ContactTableModel model = new ContactTableModel(List.of(c1, c2));
        assertTrue("Table model has 5 columns", model.getColumnCount() == 5, "Expected 5 columns");
        assertTrue("Table model has 2 rows", model.getRowCount() == 2, "Expected 2 rows");
        assertTrue("Column 0 is #", model.getColumnName(0).equals("#"), "Col 0 name check");
        assertTrue("Column 1 is Full Name", model.getColumnName(1).equals("Full Name"), "Col 1 name check");
        assertTrue("Row 0 # index is 1", model.getValueAt(0, 0).equals(1), "Index check");
        assertTrue("Row 0 Name is Anita Roy", model.getValueAt(0, 1).equals("Anita Roy"), "Name check");
        assertTrue("Row 0 Phone is 9111112222", model.getValueAt(0, 2).equals("9111112222"), "Phone check");
        assertTrue("Cells are non-editable", !model.isCellEditable(0, 1), "Non-editable check");

        // Test update
        model.setContacts(List.of(c1));
        assertTrue("Table model updated to 1 row", model.getRowCount() == 1, "Expected 1 row after update");
        assertTrue("Lookup contact at 0 returns Anita", model.getContactAt(0).getName().equals("Anita Roy"), "Lookup check");
    }
}
