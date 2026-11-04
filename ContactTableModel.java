package view;

import model.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Type-safe TableModel backed by an in-memory List of Contact entities.
 * Supports efficient updates, row lookups, and clear column mappings.
 */
public class ContactTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {
        "#", "Full Name", "Phone Number", "Email Address", "Created At"
    };

    private static final Class<?>[] COLUMN_CLASSES = {
        Integer.class, String.class, String.class, String.class, String.class
    };

    private final List<Contact> contacts;

    public ContactTableModel() {
        this.contacts = new ArrayList<>();
    }

    public ContactTableModel(List<Contact> initialContacts) {
        this.contacts = new ArrayList<>();
        if (initialContacts != null) {
            this.contacts.addAll(initialContacts);
        }
    }

    /**
     * Replaces the current contacts with a new list and notifies the view.
     */
    public void setContacts(List<Contact> newContacts) {
        this.contacts.clear();
        if (newContacts != null) {
            this.contacts.addAll(newContacts);
        }
        fireTableDataChanged();
    }

    /**
     * Retrieves the Contact entity represented at the given row index.
     */
    public Contact getContactAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < contacts.size()) {
            return contacts.get(rowIndex);
        }
        return null;
    }

    /**
     * Returns an unmodifiable list of currently displayed contacts.
     */
    public List<Contact> getDisplayedContacts() {
        return Collections.unmodifiableList(contacts);
    }

    @Override
    public int getRowCount() {
        return contacts.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Cells are not directly edited in table; editing is managed via the edit modal
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= contacts.size()) {
            return null;
        }

        Contact contact = contacts.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return rowIndex + 1; // 1-based index number
            case 1:
                return contact.getName();
            case 2:
                return contact.getPhoneNumber();
            case 3:
                return contact.getEmail();
            case 4:
                return (contact.getCreatedAt() != null && !contact.getCreatedAt().isBlank()) 
                        ? contact.getCreatedAt() 
                        : "N/A";
            default:
                return null;
        }
    }
}
