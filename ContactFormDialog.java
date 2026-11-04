package view;

import model.Contact;
import service.ContactService;
import util.ValidationUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Reusable modal dialog for Adding and Editing contacts.
 * Features inline error reporting, regex validation, and duplicate phone checks.
 */
public class ContactFormDialog extends JDialog {

    private final ContactService contactService;
    private final Contact contactToEdit;
    private boolean saved = false;

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JLabel errorLabel;

    /**
     * Constructs dialog for either Add (contactToEdit = null) or Edit (contactToEdit != null).
     */
    public ContactFormDialog(Frame parent, ContactService contactService, Contact contactToEdit) {
        super(parent, (contactToEdit == null ? "Add New Contact" : "Edit Contact"), true);
        this.contactService = contactService;
        this.contactToEdit = contactToEdit;

        initComponents();
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.CARD_BG);

        // --- Header Banner ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Theme.SIDEBAR_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel titleLabel = new JLabel(contactToEdit == null ? "Add New Contact" : "Edit Contact Details");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel(contactToEdit == null 
                ? "Enter contact information below to store in the system."
                : "Modify contact attributes. Phone number must remain unique.");
        subtitleLabel.setFont(Theme.FONT_SUBTITLE);
        subtitleLabel.setForeground(Theme.SIDEBAR_MUTED);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- Form Fields Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Theme.CARD_BG);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        // Error message banner
        errorLabel = new JLabel(" ");
        errorLabel.setFont(Theme.FONT_BODY);
        errorLabel.setForeground(Theme.DANGER_RED);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(errorLabel, gbc);

        // Row 1: Full Name
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel nameLabel = new JLabel("Full Name *");
        nameLabel.setFont(Theme.FONT_BOLD);
        nameLabel.setForeground(Theme.TEXT_DARK);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        nameField = Theme.createStyledTextField(20);
        formPanel.add(nameField, gbc);

        // Row 2: Mobile Number
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel phoneLabel = new JLabel("Mobile Number (10 Digits) *");
        phoneLabel.setFont(Theme.FONT_BOLD);
        phoneLabel.setForeground(Theme.TEXT_DARK);
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        phoneField = Theme.createStyledTextField(20);
        formPanel.add(phoneField, gbc);

        // Row 3: Email Address
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel emailLabel = new JLabel("Email Address *");
        emailLabel.setFont(Theme.FONT_BOLD);
        emailLabel.setForeground(Theme.TEXT_DARK);
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        emailField = Theme.createStyledTextField(20);
        formPanel.add(emailField, gbc);

        // Prepopulate if in Edit mode
        if (contactToEdit != null) {
            nameField.setText(contactToEdit.getName());
            phoneField.setText(contactToEdit.getPhoneNumber());
            emailField.setText(contactToEdit.getEmail());
        }

        add(formPanel, BorderLayout.CENTER);

        // --- Action Buttons Footer ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        buttonPanel.setBackground(Theme.APP_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.CARD_BORDER));

        JButton cancelButton = Theme.createStyledButton("Cancel", Theme.SECONDARY_GRAY, Color.WHITE);
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = Theme.createStyledButton(contactToEdit == null ? "Save Contact" : "Update Contact", 
                Theme.PRIMARY_BLUE, Color.WHITE);
        saveButton.addActionListener(e -> handleSave());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Enter key activates Save
        getRootPane().setDefaultButton(saveButton);
    }

    private void handleSave() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        // 1. Validation
        if (!ValidationUtil.isValidName(name)) {
            showError("Invalid Name: Must be 2-50 characters containing letters, spaces, or hyphens.");
            nameField.requestFocus();
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            showError("Invalid Phone: Must contain exactly 10 numeric digits (e.g. 9876543210).");
            phoneField.requestFocus();
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            showError("Invalid Email: Must follow a valid email format (e.g. user@example.com).");
            emailField.requestFocus();
            return;
        }

        // 2. Service Invocation
        if (contactToEdit == null) {
            // Add Mode
            Contact newContact = new Contact(name, phone, email);
            ContactService.ServiceResult result = contactService.addContact(newContact);
            if (!result.isSuccess()) {
                showError(result.getMessage());
                phoneField.requestFocus();
                return;
            }
        } else {
            // Edit Mode
            ContactService.ServiceResult result = contactService.updateContact(
                    contactToEdit.getPhoneNumber(), name, phone, email);
            if (!result.isSuccess()) {
                showError(result.getMessage());
                phoneField.requestFocus();
                return;
            }
        }

        this.saved = true;
        dispose();
    }

    private void showError(String message) {
        errorLabel.setText(message);
    }

    public boolean isSaved() {
        return saved;
    }
}
