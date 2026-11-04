package service;

import model.Contact;
import util.FileUtil;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class managing contact business logic, dual data-structure indexing
 * (ArrayList for sorted ordered iteration, HashMap for O(1) uniqueness and fast lookups),
 * and persistent CSV synchronization.
 */
public class ContactService {

    private final List<Contact> contactList;
    private final Map<String, Contact> phoneMap;
    private final String storageFilePath;

    /**
     * Default constructor using standard data path: "data/contacts.csv".
     */
    public ContactService() {
        this("data/contacts.csv");
    }

    /**
     * Parameterized constructor allowing custom storage file path (ideal for unit testing).
     *
     * @param storageFilePath relative or absolute path to the contacts CSV file
     */
    public ContactService(String storageFilePath) {
        this.storageFilePath = storageFilePath;
        this.contactList = new ArrayList<>();
        this.phoneMap = new HashMap<>();
        loadContacts();
    }

    /**
     * Loads contacts from persistent storage into in-memory data structures.
     * Rebuilds both ArrayList and HashMap indices.
     */
    public synchronized void loadContacts() {
        contactList.clear();
        phoneMap.clear();

        List<Contact> loaded = FileUtil.readContactsFromCsv(storageFilePath);
        for (Contact contact : loaded) {
            // Ignore corrupted duplicates if any exist in the raw file
            if (!phoneMap.containsKey(contact.getPhoneNumber())) {
                contactList.add(contact);
                phoneMap.put(contact.getPhoneNumber(), contact);
            }
        }

        // Sort contacts alphabetically by default
        Collections.sort(contactList);
    }

    /**
     * Persists current in-memory contact list to the CSV file.
     *
     * @return true if save was successful
     */
    public synchronized boolean saveContacts() {
        return FileUtil.writeContactsToCsv(storageFilePath, contactList);
    }

    /**
     * Adds a new contact after validating constraints and uniqueness.
     *
     * @param contact the Contact to add
     * @return ServiceResult containing status flag and descriptive feedback
     */
    public synchronized ServiceResult addContact(Contact contact) {
        if (contact == null) {
            return new ServiceResult(false, "Contact details cannot be empty.");
        }

        if (!ValidationUtil.isValidName(contact.getName())) {
            return new ServiceResult(false, "Invalid Name: Must be 2-50 characters containing letters, spaces, or hyphens.");
        }

        if (!ValidationUtil.isValidPhone(contact.getPhoneNumber())) {
            return new ServiceResult(false, "Invalid Phone: Must contain exactly 10 numeric digits.");
        }

        if (!ValidationUtil.isValidEmail(contact.getEmail())) {
            return new ServiceResult(false, "Invalid Email: Must follow a valid email address format (e.g., name@domain.com).");
        }

        // Check for duplicate phone number using O(1) HashMap lookup
        if (phoneMap.containsKey(contact.getPhoneNumber())) {
            return new ServiceResult(false, "Duplicate Contact: A contact with phone number " 
                    + contact.getPhoneNumber() + " already exists.");
        }

        // Add to both in-memory structures
        contactList.add(contact);
        phoneMap.put(contact.getPhoneNumber(), contact);

        // Maintain alphabetical order in ArrayList
        Collections.sort(contactList);

        // Auto-save to CSV
        boolean saved = saveContacts();
        if (!saved) {
            return new ServiceResult(true, "Contact added successfully, but failed to write to CSV storage.");
        }

        return new ServiceResult(true, "Contact added successfully.");
    }

    /**
     * Retrieves an unmodifiable view of all contacts in alphabetical order.
     *
     * @return copy of contact list
     */
    public synchronized List<Contact> getAllContacts() {
        return new ArrayList<>(contactList);
    }

    /**
     * Retrieves total count of contacts.
     */
    public synchronized int getContactCount() {
        return contactList.size();
    }

    /**
     * Direct O(1) lookup of a contact by exact phone number.
     *
     * @param phone 10-digit phone number
     * @return Contact if found, null otherwise
     */
    public synchronized Contact getContactByPhone(String phone) {
        if (phone == null) return null;
        return phoneMap.get(phone.trim());
    }

    /**
     * Searches contacts by name (case-insensitive substring match).
     *
     * @param query search keyword
     * @return list of matching contacts
     */
    public synchronized List<Contact> searchByName(String query) {
        List<Contact> results = new ArrayList<>();
        if (query == null || query.isBlank()) {
            return results;
        }

        String lowerQuery = query.trim().toLowerCase();
        for (Contact contact : contactList) {
            if (contact.getName().toLowerCase().contains(lowerQuery)) {
                results.add(contact);
            }
        }
        return results;
    }

    /**
     * Searches contacts by phone number (substring or exact match).
     *
     * @param phoneQuery phone digits search keyword
     * @return list of matching contacts
     */
    public synchronized List<Contact> searchByPhone(String phoneQuery) {
        List<Contact> results = new ArrayList<>();
        if (phoneQuery == null || phoneQuery.isBlank()) {
            return results;
        }

        String query = phoneQuery.trim();
        for (Contact contact : contactList) {
            if (contact.getPhoneNumber().contains(query)) {
                results.add(contact);
            }
        }
        return results;
    }

    /**
     * Updates an existing contact's attributes.
     *
     * @param originalPhone phone number identifying the existing contact
     * @param newName       updated full name
     * @param newPhone      updated phone number
     * @param newEmail      updated email address
     * @return ServiceResult containing status and message
     */
    public synchronized ServiceResult updateContact(String originalPhone, String newName, String newPhone, String newEmail) {
        if (originalPhone == null || !phoneMap.containsKey(originalPhone.trim())) {
            return new ServiceResult(false, "Contact not found with phone number: " + originalPhone);
        }

        Contact contact = phoneMap.get(originalPhone.trim());

        if (!ValidationUtil.isValidName(newName)) {
            return new ServiceResult(false, "Invalid Name: Must be 2-50 characters containing letters, spaces, or hyphens.");
        }

        if (!ValidationUtil.isValidPhone(newPhone)) {
            return new ServiceResult(false, "Invalid Phone: Must contain exactly 10 numeric digits.");
        }

        if (!ValidationUtil.isValidEmail(newEmail)) {
            return new ServiceResult(false, "Invalid Email: Must follow a valid email address format.");
        }

        String cleanNewPhone = newPhone.trim();
        // If phone number is changing, verify new phone number is not taken by another contact
        if (!originalPhone.trim().equals(cleanNewPhone) && phoneMap.containsKey(cleanNewPhone)) {
            return new ServiceResult(false, "Phone number " + cleanNewPhone + " is already assigned to another contact.");
        }

        // If phone changed, update HashMap index
        if (!originalPhone.trim().equals(cleanNewPhone)) {
            phoneMap.remove(originalPhone.trim());
            contact.setPhoneNumber(cleanNewPhone);
            phoneMap.put(cleanNewPhone, contact);
        }

        contact.setName(newName.trim());
        contact.setEmail(newEmail.trim());

        // Re-sort ArrayList
        Collections.sort(contactList);

        // Persist updates
        saveContacts();

        return new ServiceResult(true, "Contact updated successfully.");
    }

    /**
     * Deletes a contact identified by phone number.
     *
     * @param phone 10-digit phone number
     * @return ServiceResult indicating outcome
     */
    public synchronized ServiceResult deleteContact(String phone) {
        if (phone == null || !phoneMap.containsKey(phone.trim())) {
            return new ServiceResult(false, "Contact not found with phone number: " + phone);
        }

        String cleanPhone = phone.trim();
        Contact removed = phoneMap.remove(cleanPhone);
        contactList.remove(removed);

        // Persist changes
        saveContacts();

        return new ServiceResult(true, "Contact for '" + removed.getName() + "' deleted successfully.");
    }

    /**
     * Inner helper class to convey operation status and user-friendly messages.
     */
    public static class ServiceResult {
        private final boolean success;
        private final String message;

        public ServiceResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
