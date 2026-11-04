package view;

import model.Contact;
import service.ContactService;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Main application window implementing the professional Blue & White Dashboard.
 * Features sidebar navigation, live search filtering, statistics cards,
 * customized JTable with empty-state handling, and action triggers.
 */
public class MainDashboardFrame extends JFrame {

    private final ContactService contactService;
    private final ContactTableModel tableModel;

    private JTable contactTable;
    private JTextField searchField;
    private JLabel totalCountLabel;
    private JLabel matchingCountLabel;
    private JButton editButton;
    private JButton deleteButton;
    private JPanel tableContainerPanel;
    private CardLayout tableCardLayout;

    public MainDashboardFrame(ContactService contactService) {
        this.contactService = contactService;
        this.tableModel = new ContactTableModel();

        initWindow();
        initComponents();
        refreshData();
    }

    private void initWindow() {
        setTitle("Contact Management System - CodeNova Internship Project 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1140, 720);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null); // Centered on screen
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.APP_BG);

        // 1. Left Sidebar Navigation
        add(createSidebarPanel(), BorderLayout.WEST);

        // 2. Main Content Area
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setBackground(Theme.APP_BG);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Top Header + Search + Stat Cards + Actions
        mainContent.add(createHeaderAndStatsPanel(), BorderLayout.NORTH);

        // Center: Table with Empty State switcher
        mainContent.add(createTableSection(), BorderLayout.CENTER);

        // Bottom Status Bar
        mainContent.add(createStatusBar(), BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Constructs the left navigation sidebar with CodeNova branding.
     */
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(Theme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));

        // Branding Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(Theme.SIDEBAR_BG);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));

        JLabel brandTitle = new JLabel("CodeNova");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandTitle.setForeground(Color.WHITE);

        JLabel brandSubtitle = new JLabel("Contact Management");
        brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandSubtitle.setForeground(Theme.SIDEBAR_MUTED);

        brandPanel.add(brandTitle);
        brandPanel.add(Box.createVerticalStrut(2));
        brandPanel.add(brandSubtitle);

        // Navigation Links
        JPanel navMenu = new JPanel();
        navMenu.setLayout(new BoxLayout(navMenu, BoxLayout.Y_AXIS));
        navMenu.setBackground(Theme.SIDEBAR_BG);
        navMenu.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JButton navDashboard = createSidebarNavButton("●  All Contacts (Dashboard)", true);
        navDashboard.addActionListener(e -> {
            searchField.setText("");
            refreshData();
        });

        JButton navAdd = createSidebarNavButton("+  Add New Contact", false);
        navAdd.addActionListener(e -> openAddDialog());

        JButton navReload = createSidebarNavButton("↻  Reload from CSV", false);
        navReload.addActionListener(e -> {
            contactService.loadContacts();
            refreshData();
            JOptionPane.showMessageDialog(this,
                    "Reloaded " + contactService.getContactCount() + " contact(s) from persistent storage.",
                    "Storage Synchronized", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton navExit = createSidebarNavButton("✕  Exit Application", false);
        navExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit the application?",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        navMenu.add(navDashboard);
        navMenu.add(Box.createVerticalStrut(6));
        navMenu.add(navAdd);
        navMenu.add(Box.createVerticalStrut(6));
        navMenu.add(navReload);
        navMenu.add(Box.createVerticalStrut(6));
        navMenu.add(navExit);

        // Sidebar Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(Theme.SIDEBAR_BG);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));

        JLabel versionLabel = new JLabel("Java 17 Swing GUI");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(Theme.SIDEBAR_MUTED);

        JLabel internLabel = new JLabel("Internship Project 1");
        internLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        internLabel.setForeground(Theme.SIDEBAR_TEXT);

        footerPanel.add(internLabel);
        footerPanel.add(versionLabel);

        sidebar.add(brandPanel, BorderLayout.NORTH);
        sidebar.add(navMenu, BorderLayout.CENTER);
        sidebar.add(footerPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createSidebarNavButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setFont(Theme.FONT_BODY);
        btn.setForeground(active ? Color.WHITE : Theme.SIDEBAR_TEXT);
        btn.setBackground(active ? Theme.SIDEBAR_ACTIVE : Theme.SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) btn.setBackground(Theme.SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) btn.setBackground(Theme.SIDEBAR_BG);
            }
        });

        return btn;
    }

    /**
     * Top header bar containing Title, Live Search bar, Stat cards, and Action buttons.
     */
    private JPanel createHeaderAndStatsPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Theme.APP_BG);

        // Row 1: Title and Top Search Bar
        JPanel topBar = new JPanel(new BorderLayout(16, 0));
        topBar.setBackground(Theme.APP_BG);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setBackground(Theme.APP_BG);

        JLabel mainTitle = new JLabel("Contact Management System");
        mainTitle.setFont(Theme.FONT_TITLE);
        mainTitle.setForeground(Theme.TEXT_DARK);

        JLabel subTitle = new JLabel("Manage, filter, and organize directory contacts with persistent CSV sync.");
        subTitle.setFont(Theme.FONT_SUBTITLE);
        subTitle.setForeground(Theme.TEXT_MUTED);

        titleBlock.add(mainTitle);
        titleBlock.add(Box.createVerticalStrut(2));
        titleBlock.add(subTitle);
        topBar.add(titleBlock, BorderLayout.WEST);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setBackground(Theme.APP_BG);

        JLabel searchLabel = new JLabel("🔍 Search:");
        searchLabel.setFont(Theme.FONT_BOLD);
        searchLabel.setForeground(Theme.TEXT_DARK);

        searchField = Theme.createStyledTextField(18);
        searchField.setToolTipText("Search contacts by name or 10-digit phone number");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applySearchFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applySearchFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applySearchFilter(); }
        });

        JButton clearSearchBtn = Theme.createStyledButton("Clear", Theme.SECONDARY_GRAY, Color.WHITE);
        clearSearchBtn.setPreferredSize(new Dimension(75, 36));
        clearSearchBtn.addActionListener(e -> searchField.setText(""));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(clearSearchBtn);
        topBar.add(searchPanel, BorderLayout.EAST);

        container.add(topBar);
        container.add(Box.createVerticalStrut(16));

        // Row 2: Stat Cards (Total Contacts, Showing Matches, Storage File)
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setBackground(Theme.APP_BG);

        totalCountLabel = new JLabel("0");
        JPanel cardTotal = createStatCard("TOTAL CONTACTS", totalCountLabel, Theme.PRIMARY_BLUE);

        matchingCountLabel = new JLabel("0");
        JPanel cardMatches = createStatCard("MATCHING FILTER", matchingCountLabel, Theme.SUCCESS_GREEN);

        JLabel storageLabel = new JLabel("contacts.csv");
        storageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JPanel cardStorage = createStatCard("DATA STORAGE", storageLabel, Theme.TEXT_DARK);

        statsRow.add(cardTotal);
        statsRow.add(cardMatches);
        statsRow.add(cardStorage);
        container.add(statsRow);
        container.add(Box.createVerticalStrut(16));

        // Row 3: Action Toolbar Buttons
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionToolbar.setBackground(Theme.APP_BG);

        JButton addButton = Theme.createStyledButton("+ Add Contact", Theme.PRIMARY_BLUE, Color.WHITE);
        addButton.addActionListener(e -> openAddDialog());

        editButton = Theme.createStyledButton("✎ Edit Contact", Theme.PRIMARY_HOVER, Color.WHITE);
        editButton.setEnabled(false);
        editButton.addActionListener(e -> openEditDialog());

        deleteButton = Theme.createStyledButton("🗑 Delete", Theme.DANGER_RED, Color.WHITE);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> handleDelete());

        JButton refreshButton = Theme.createStyledButton("↻ Refresh", Theme.SECONDARY_GRAY, Color.WHITE);
        refreshButton.addActionListener(e -> refreshData());

        actionToolbar.add(addButton);
        actionToolbar.add(editButton);
        actionToolbar.add(deleteButton);
        actionToolbar.add(refreshButton);
        container.add(actionToolbar);

        return container;
    }

    private JPanel createStatCard(String labelText, JLabel valueLabel, Color accentColor) {
        JPanel card = Theme.createCardPanel();
        card.setLayout(new BorderLayout(8, 4));

        JLabel title = new JLabel(labelText);
        title.setFont(Theme.FONT_STAT_LABEL);
        title.setForeground(Theme.TEXT_MUTED);

        valueLabel.setFont(Theme.FONT_STAT_NUM);
        valueLabel.setForeground(accentColor);

        card.add(title, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Section holding the JTable with empty-state card switching.
     */
    private JPanel createTableSection() {
        tableCardLayout = new CardLayout();
        tableContainerPanel = new JPanel(tableCardLayout);
        tableContainerPanel.setBackground(Theme.CARD_BG);
        tableContainerPanel.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER, 1));

        // Card 1: JTable within JScrollPane
        contactTable = new JTable(tableModel);
        contactTable.setRowHeight(38);
        contactTable.setFont(Theme.FONT_BODY);
        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactTable.setGridColor(Theme.CARD_BORDER);
        contactTable.setShowVerticalLines(false);
        contactTable.setShowHorizontalLines(true);
        contactTable.setSelectionBackground(new Color(219, 234, 254)); // Soft light blue highlight
        contactTable.setSelectionForeground(Theme.TEXT_DARK);

        // Header styling
        JTableHeader header = contactTable.getTableHeader();
        header.setFont(Theme.FONT_BOLD);
        header.setBackground(Theme.TABLE_HEADER_BG);
        header.setForeground(Theme.TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Custom Cell Renderer with alternating colors and column alignments
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Theme.TABLE_STRIPE);
                    c.setForeground(Theme.TEXT_DARK);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        };

        // Align # column to center, other columns left
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < contactTable.getColumnCount(); i++) {
            if (i == 0) {
                contactTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                contactTable.getColumnModel().getColumn(i).setMaxWidth(60);
                contactTable.getColumnModel().getColumn(i).setPreferredWidth(50);
            } else {
                contactTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
        }

        // Adjust preferred widths
        contactTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        contactTable.getColumnModel().getColumn(2).setPreferredWidth(130); // Phone
        contactTable.getColumnModel().getColumn(3).setPreferredWidth(230); // Email
        contactTable.getColumnModel().getColumn(4).setPreferredWidth(160); // CreatedAt

        // Selection Listener
        contactTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = contactTable.getSelectedRow() >= 0;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        });

        // Double-click to Edit
        contactTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && contactTable.getSelectedRow() >= 0) {
                    openEditDialog();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(contactTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableContainerPanel.add(scrollPane, "TABLE_VIEW");

        // Card 2: Empty State View
        JPanel emptyStatePanel = new JPanel(new GridBagLayout());
        emptyStatePanel.setBackground(Color.WHITE);

        JPanel emptyContent = new JPanel();
        emptyContent.setLayout(new BoxLayout(emptyContent, BoxLayout.Y_AXIS));
        emptyContent.setBackground(Color.WHITE);

        JLabel emptyIcon = new JLabel("📋");
        emptyIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptyMessage = new JLabel("No Contacts Found");
        emptyMessage.setFont(new Font("Segoe UI", Font.BOLD, 18));
        emptyMessage.setForeground(Theme.TEXT_DARK);
        emptyMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptySubtitle = new JLabel("Click '+ Add Contact' or clear your search query to display records.");
        emptySubtitle.setFont(Theme.FONT_BODY);
        emptySubtitle.setForeground(Theme.TEXT_MUTED);
        emptySubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton emptyAddBtn = Theme.createStyledButton("+ Add Contact", Theme.PRIMARY_BLUE, Color.WHITE);
        emptyAddBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyAddBtn.addActionListener(e -> openAddDialog());

        emptyContent.add(emptyIcon);
        emptyContent.add(Box.createVerticalStrut(10));
        emptyContent.add(emptyMessage);
        emptyContent.add(Box.createVerticalStrut(4));
        emptyContent.add(emptySubtitle);
        emptyContent.add(Box.createVerticalStrut(16));
        emptyContent.add(emptyAddBtn);

        emptyStatePanel.add(emptyContent, new GridBagConstraints());
        tableContainerPanel.add(emptyStatePanel, "EMPTY_VIEW");

        return tableContainerPanel;
    }

    /**
     * Bottom status bar showing persistence file and active thread.
     */
    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(Theme.APP_BG);
        status.setBorder(BorderFactory.createEmptyBorder(6, 4, 0, 4));

        JLabel leftStatus = new JLabel("● Storage: data/contacts.csv (Auto-saved on change)");
        leftStatus.setFont(Theme.FONT_SUBTITLE);
        leftStatus.setForeground(Theme.TEXT_MUTED);

        JLabel rightStatus = new JLabel("Java 17 Swing | CodeNova Internship");
        rightStatus.setFont(Theme.FONT_SUBTITLE);
        rightStatus.setForeground(Theme.TEXT_MUTED);

        status.add(leftStatus, BorderLayout.WEST);
        status.add(rightStatus, BorderLayout.EAST);
        return status;
    }

    // --- Action Handlers ---

    private void openAddDialog() {
        ContactFormDialog dialog = new ContactFormDialog(this, contactService, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }

    private void openEditDialog() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a contact from the table to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Contact contact = tableModel.getContactAt(selectedRow);
        if (contact != null) {
            ContactFormDialog dialog = new ContactFormDialog(this, contactService, contact);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                refreshData();
            }
        }
    }

    private void handleDelete() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a contact from the table to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Contact contact = tableModel.getContactAt(selectedRow);
        if (contact != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete contact:\n\n"
                    + "Name: " + contact.getName() + "\n"
                    + "Phone: " + contact.getPhoneNumber() + "\n\n"
                    + "This action cannot be undone.",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                ContactService.ServiceResult result = contactService.deleteContact(contact.getPhoneNumber());
                if (result.isSuccess()) {
                    refreshData();
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Contact Deleted", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, result.getMessage(), "Error Deleting Contact", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Filters table rows based on the text currently in the search field.
     */
    private void applySearchFilter() {
        String query = searchField.getText().trim();
        List<Contact> filtered;

        if (query.isEmpty()) {
            filtered = contactService.getAllContacts();
        } else {
            // Combine name and phone matching
            List<Contact> byName = contactService.searchByName(query);
            List<Contact> byPhone = contactService.searchByPhone(query);

            List<Contact> combined = new ArrayList<>(byName);
            for (Contact c : byPhone) {
                if (!combined.contains(c)) {
                    combined.add(c);
                }
            }
            filtered = combined;
        }

        tableModel.setContacts(filtered);
        updateCounters(filtered.size());
        switchTableCard(filtered.isEmpty());
    }

    /**
     * Refreshes the table with all contacts and updates counter labels.
     */
    public void refreshData() {
        applySearchFilter();
    }

    private void updateCounters(int displayedCount) {
        int total = contactService.getContactCount();
        totalCountLabel.setText(String.valueOf(total));
        matchingCountLabel.setText(String.valueOf(displayedCount));
    }

    private void switchTableCard(boolean isEmpty) {
        if (isEmpty) {
            tableCardLayout.show(tableContainerPanel, "EMPTY_VIEW");
        } else {
            tableCardLayout.show(tableContainerPanel, "TABLE_VIEW");
        }
    }
}
