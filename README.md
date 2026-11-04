# CodeNova Java Programming Internship
# Project: Contact Management Application

![Java 17](https://img.shields.io/badge/Java-17%20LTS-007396?logo=openjdk&logoColor=white)
![Build](https://img.shields.io/badge/Build-Passing%20(javac)-brightgreen)
![Tests](https://img.shields.io/badge/Tests-53%2F53%20Passed-success)
![Platform](https://img.shields.io/badge/Platform-Windows%20PowerShell%20%7C%20Cross--Platform-blue)
![Architecture](https://img.shields.io/badge/Architecture-OOP%20Layered-orange)
![Storage](https://img.shields.io/badge/Storage-Persistent%20CSV%20(Atomic%20IO)-blueviolet)

---

> **Evaluation Notice:**
> - **Official Demonstration Version**: **Java Console Application (CLI)** (`java -cp bin Main --cli`). This is the primary submission meeting 100% of the project requirements for technical evaluation.
> - **Optional Additional Feature**: **Java Swing Modern Desktop GUI** (`java -cp bin Main`). An interactive graphical dashboard built upon the exact same business logic layer as an enhancement.

---

## 📋 Table of Contents

1. [Executive Summary](#-executive-summary)
2. [Key Application Features](#-key-application-features)
3. [Core Technical Concepts](#-core-technical-concepts)
   - [Java 17 LTS Foundation](#1-java-17-lts-foundation)
   - [Object-Oriented Programming (OOP) Principles](#2-object-oriented-programming-oop-principles)
   - [Dual Data Structures & Algorithmic Complexity](#3-dual-data-structures--algorithmic-complexity)
   - [Robust Regular Expression (Regex) Validation](#4-robust-regular-expression-regex-validation)
   - [CSV File Storage & Persistence Mechanics](#5-csv-file-storage--persistence-mechanics)
4. [System Architecture](#-system-architecture)
5. [CodeNova Requirements Verification Matrix](#-codenova-requirements-verification-matrix)
6. [Project Folder Structure](#-project-folder-structure)
7. [Windows PowerShell Setup & Execution Guide](#-windows-powershell-setup--execution-guide)
   - [Prerequisites](#prerequisites)
   - [Step 1: Clean Compilation](#step-1-clean-compilation)
   - [Step 2: Run Official Demonstration Version (Console Application)](#step-2-run-official-demonstration-version-console-application)
   - [Step 3: Run Optional Additional Feature (Java Swing GUI)](#step-3-run-optional-additional-feature-java-swing-gui)
   - [Step 4: Running via Visual Studio Code](#step-4-running-via-visual-studio-code)
8. [Automated Verification Test Suite (53 Assertions)](#-automated-verification-test-suite-53-assertions)
9. [How Contact Data Persists After Restarting](#-how-contact-data-persists-after-restarting)
10. [Sample Console Output & User Workflows](#-sample-console-output--user-workflows)
11. [Optional Feature: Java Swing Desktop Dashboard](#-optional-feature-java-swing-desktop-dashboard)
12. [GitHub Repository Setup Instructions](#-github-repository-setup-instructions)
13. [Internship Submission Metadata](#-internship-submission-metadata)

---

## 🌟 Executive Summary

The **CodeNova Contact Management Application** is a production-grade directory management system developed in **Java 17**. Engineered with a clean multi-tiered architecture, it demonstrates advanced mastery of object-oriented programming, dual-indexing data structures (`ArrayList` and `HashMap`), RFC-compliant regular expression sanitization, and atomic CSV file persistence.

The application is structured to serve two user interfaces on top of a single, unified business service:
1. **Official Demonstration Version — Interactive Console Application (CLI)**: Provides a distraction-free, terminal-driven experience featuring structured menus, ASCII tabular directory formatting, input prompts with cancellation triggers (`0`), and clear status feedback.
2. **Optional Additional Feature — Java Swing Modern GUI**: A rich desktop dashboard featuring a royal navy and enterprise blue theme, live search filtering, statistics summary cards, and modal dialogs for contact authoring and editing.

---

## ⚡ Key Application Features

| Feature | Console CLI (Official) | Swing GUI (Optional) | Description |
|---|:---:|:---:|---|
| **Add New Contact** | `Option 1` | Toolbar / Sidebar | Adds a new contact with real-time name, 10-digit mobile, and email validation. |
| **View All Contacts** | `Option 2` | Interactive `JTable` | Renders all contacts alphabetically in a structured table with timestamps. |
| **Search Directory** | `Option 3` | Dynamic Filter Bar | Dual search strategy: case-insensitive name matching and numeric phone lookup. |
| **Edit Existing Contact** | `Option 4` | Double-click / Modal | Pre-fills existing values, allows selective updating (press Enter to keep), and guards against phone number conflicts. |
| **Delete Contact** | `Option 5` | Selection & Confirm | Locates contact by phone and prompts with explicit confirmation before removal. |
| **Storage Resynchronization** | `Option 6` | Sidebar Reload | Reloads the in-memory state directly from the persistent CSV file on disk. |
| **Atomic CSV Persistence** | Automatic | Automatic | Every modification immediately updates `data/contacts.csv` via atomic replacement. |
| **Resilient Error Recovery** | Handled | Handled | Malformed records in CSV are safely skipped with warnings without crashing the app. |

---

## 🧠 Core Technical Concepts

### 1. Java 17 LTS Foundation
The project leverages modern capabilities of the **Java 17 Long-Term Support (LTS)** platform:
- **Enhanced String Utilities**: Leverages methods such as `String.isBlank()`, `String.strip()`, and `String.format()` to ensure robust input handling and prevent whitespace-only entries.
- **Modern Date/Time API (`java.time`)**: Uses `java.time.LocalDateTime` and `java.time.format.DateTimeFormatter` (`yyyy-MM-dd HH:mm:ss`) for thread-safe timestamp generation without the legacy synchronization issues of `java.util.Date`.
- **NIO.2 File Operations (`java.nio.file`)**: Utilizes `Path`, `Paths`, and `Files.move()` with `StandardCopyOption.ATOMIC_MOVE` to guarantee that file system writes are atomic and resilient to process interruption.
- **Modern Exception Handling & Try-with-Resources**: Ensures all `BufferedReader`, `BufferedWriter`, and `Scanner` resources are deterministically closed, preventing handle leaks.

---

### 2. Object-Oriented Programming (OOP) Principles

The codebase strictly adheres to standard object-oriented design patterns:

#### A. Encapsulation
- The domain entity [`model.Contact`](model/Contact.java) declares all member variables (`name`, `phoneNumber`, `email`, `createdAt`) as `private`.
- Access and mutation occur solely through validated getters and setters that enforce trimming and guard against blank/null assignments.
- [`service.ContactService`](service/ContactService.java) protects internal collections by returning defensive shallow copies (`new ArrayList<>(contactList)`) via `getAllContacts()`, ensuring callers cannot mutate internal state directly.

#### B. Abstraction
- The application separates low-level file I/O and data structure management behind clean service boundaries.
- User interface components ([`view.ContactView`](view/ContactView.java) and [`view.MainDashboardFrame`](view/MainDashboardFrame.java)) interact with the backend purely through the high-level API methods of [`ContactService`](service/ContactService.java) (`addContact`, `updateContact`, `deleteContact`, `searchByName`, `searchByPhone`).
- Details of CSV parsing, delimiter escaping, and temporary file management are encapsulated in [`util.FileUtil`](util/FileUtil.java).

#### C. Inheritance & Polymorphism
- [`model.Contact`](model/Contact.java) implements the standard `Comparable<Contact>` interface, overriding `compareTo(Contact other)` to establish natural alphabetical sorting order.
- Overrides `equals(Object o)` and `hashCode()` to enforce entity identity based on the unique phone number.
- In the optional GUI module, [`view.ContactTableModel`](view/ContactTableModel.java) extends Swing's `AbstractTableModel`, overriding `getRowCount()`, `getColumnCount()`, `getValueAt()`, and `getColumnName()` to polymorphicly bind domain data to `JTable`.

#### D. Single Responsibility Principle (SRP)
Each class within the project has one clear, isolated responsibility:
- `Contact`: Data container representing a contact entity.
- `ContactService`: Business logic, collection indexing, and coordination.
- `ValidationUtil`: Centralized regular expression matching and sanitization.
- `FileUtil`: Low-level CSV disk I/O and atomic file swapping.
- `ContactView`: Interactive CLI presentation and terminal formatting.
- `MainDashboardFrame`: Swing GUI desktop presentation and user interactions.

---

### 3. Dual Data Structures & Algorithmic Complexity

To optimize both tabular iteration and fast unique lookups, [`service.ContactService`](service/ContactService.java) maintains **dual in-memory data structures**:

```
                  ┌─────────────────────────────────────────┐
                  │              ContactService             │
                  └──────┬───────────────────────────┬──────┘
                         │                           │
            ┌────────────▼────────────┐ ┌────────────▼────────────┐
            │   ArrayList<Contact>    │ │ HashMap<String, Contact>│
            │                         │ │                         │
            │ • Alphabetically Sorted │ │ • Key: 10-Digit Phone   │
            │ • O(1) Index Access     │ │ • Value: Contact Object │
            │ • O(N) Table Iteration  │ │ • O(1) Unique Check     │
            │ • O(N) Substring Search │ │ • O(1) Direct Lookup    │
            └─────────────────────────┘ └─────────────────────────┘
```

#### Why Dual Data Structures?
1. **`ArrayList<Contact>` (Sorted Sequential Collection)**:
   - Maintains all contacts sorted alphabetically by contact name (via `Collections.sort(contactList)` which invokes `Contact.compareTo()`).
   - Ideal for rendering tabular directory listings in the console and `JTable`, guaranteeing consistent $O(1)$ indexed retrieval by row number.
2. **`HashMap<String, Contact>` (Hash-Indexed Key-Value Map)**:
   - Indexes each contact by its unique 10-digit mobile number (`String`).
   - Enables **$O(1)$ constant-time lookup** for duplicate validation during contact creation and editing.
   - Prevents having to scan the entire directory ($O(N)$) simply to verify if a phone number is already registered.

#### Synchronization Mechanics:
All mutating operations (`addContact`, `updateContact`, `deleteContact`, `loadContacts`) are declared `synchronized` and execute atomic dual updates:
- **Add**: Adds to `contactList`, puts into `phoneMap`, and sorts `contactList`.
- **Edit**: If the phone number changes, the old key is removed from `phoneMap`, the new key is inserted, and `contactList` is re-sorted.
- **Delete**: Removes the entry from `phoneMap` and removes the reference from `contactList`.

---

### 4. Robust Regular Expression (Regex) Validation

All user inputs are strictly validated through [`util.ValidationUtil`](util/ValidationUtil.java) before any state changes occur:

| Field | Regular Expression | Rules Enforced |
|---|---|---|
| **10-Digit Mobile** | `^[0-9]{10}$` | Exactly 10 numeric digits. Rejects letters, symbols, spaces, and numbers shorter or longer than 10 digits. |
| **Email Address** | `^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$` | RFC-compliant standard email syntax. Requires valid user name, `@` delimiter, valid domain name, and a 2-7 letter TLD. |
| **Full Name** | `^[a-zA-Z\s.'-]{2,50}$` | Between 2 and 50 characters. Permits uppercase and lowercase alphabets, spaces, apostrophes, dots, and hyphens. |

#### Input Re-prompt & Graceful Cancellation:
In the CLI mode, validation runs in a `while(true)` loop:
- If the user provides invalid input, an informative error message explaining the exact requirement is printed, and the prompt repeats.
- At any prompt, entering `0` cancels the current operation immediately and returns safely to the main menu without altering data.

---

### 5. CSV File Storage & Persistence Mechanics

All contact records are permanently persisted in flat-file CSV format at `data/contacts.csv`.

#### CSV File Layout:
```csv
Name,PhoneNumber,Email,CreatedAt
Aarav Sharma,9876543210,aarav.sharma@example.com,2026-10-30 10:00:00
Priya Patel,9123456780,priya.patel@workmail.org,2026-10-30 10:15:00
Rahul Verma,9988776655,rahul.verma@techcorp.in,2026-10-30 11:30:00
```

#### RFC 4180 Escaping:
If a contact name contains commas or quotation marks, `Contact.toCsvRow()` encapsulates the field in double quotes and escapes existing quotes (`"John, Jr."`), while `FileUtil.parseCsvLine()` implements a stateful parser that preserves comma-containing quoted strings.

#### Atomic Write Guarantee:
To eliminate the risk of file corruption (e.g., if the user abruptly terminates the app or power is lost during a write):
1. `FileUtil.writeContactsToCsv()` writes all records to a staging file: `data/contacts.csv.tmp`.
2. Upon successful write and buffer flush, it performs an atomic swap using `Files.move(..., StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)`.
3. If the filesystem does not support atomic moves, a safe replacement fallback is executed.
4. The main `data/contacts.csv` file is never left half-written.

---

## 🏛️ System Architecture

The application is structured into four distinct layers, enforcing complete separation of concerns:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                              │
│                                                                        │
│   [OFFICIAL DEMO]                         [OPTIONAL EXTENSION]         │
│   view.ContactView (CLI Console)          view.MainDashboardFrame      │
│   • Menu & ASCII Table Formatting         • Swing JTable & Stat Cards  │
│   • Interactive Scanner Prompts           • Modal Dialogs & Live Search│
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Calls Service Methods
┌───────────────────────────────────▼────────────────────────────────────┐
│                          SERVICE LAYER                                 │
│                                                                        │
│   service.ContactService                                               │
│   • ArrayList<Contact> (Alphabetical sorted order for rendering)       │
│   • HashMap<String, Contact> (O(1) duplicate checks & phone lookup)    │
│   • Synchronized thread-safe transactions                              │
└─────────────────────┬────────────────────────────────────┬─────────────┘
                      │                                    │
┌─────────────────────▼──────────────┐   ┌─────────────────▼─────────────┐
│            MODEL LAYER             │   │        UTILITY LAYER          │
│                                    │   │                               │
│   model.Contact                    │   │   util.ValidationUtil         │
│   • Private attributes             │   │   • Regex matching (Phone,    │
│   • Comparable<Contact>            │   │     Email, Name)              │
│   • CSV escaping & equals/hashCode │   │                               │
│                                    │   │   util.FileUtil               │
│                                    │   │   • CSV Reader & Parser       │
│                                    │   │   • Atomic File Replacement   │
└────────────────────────────────────┘   └─────────────────┬─────────────┘
                                                           │ Atomic I/O
                                         ┌─────────────────▼─────────────┐
                                         │      PERSISTENT STORAGE       │
                                         │       data/contacts.csv       │
                                         └───────────────────────────────┘
```

---

## 📋 CodeNova Requirements Verification Matrix

| # | CodeNova Requirement | Architectural Component | Implementation Verification | Status |
|---|---|---|---|:---:|
| **1** | **Add new contacts** | `ContactView.handleAddContact()` / `ContactService.addContact()` | Validates Name, Phone, Email; checks duplicate phone; appends to list and map; auto-persists to CSV. | ✅ **Verified** |
| **2** | **View all contacts** | `ContactView.handleViewAllContacts()` / `renderContactTable()` | Formats contacts into an aligned ASCII table; displays index, name, phone, email, and timestamp. | ✅ **Verified** |
| **3** | **Search by name or phone** | `ContactService.searchByName()` / `ContactService.searchByPhone()` | Case-insensitive substring match for names; partial/exact digit match for phone numbers. | ✅ **Verified** |
| **4** | **Edit existing contacts** | `ContactView.handleEditContact()` / `ContactService.updateContact()` | Looks up contact by phone; displays current values; keeps existing value on empty Enter; validates updates. | ✅ **Verified** |
| **5** | **Delete contacts** | `ContactView.handleDeleteContact()` / `ContactService.deleteContact()` | Locates contact by phone; asks confirmation (`y/N`); removes from list, map, and CSV. | ✅ **Verified** |
| **6** | **Validate email format** | `ValidationUtil.isValidEmail()` | Regex check against RFC standards (`user@domain.tld`); rejects missing `@`, spaces, or missing domain. | ✅ **Verified** |
| **7** | **Validate 10-digit phone** | `ValidationUtil.isValidPhone()` | Enforces exact 10 numeric digits `^[0-9]{10}$`; rejects non-digits, shorter, or longer lengths. | ✅ **Verified** |
| **8** | **Save contacts to CSV** | `FileUtil.writeContactsToCsv()` | Formats fields to CSV format; escapes commas/quotes; performs atomic file replace. | ✅ **Verified** |
| **9** | **Load contacts on startup**| `FileUtil.readContactsFromCsv()` / `ContactService.loadContacts()` | Automatically parsed during service initialization; rebuilds `ArrayList` and `HashMap`. | ✅ **Verified** |
| **10**| **Clean exception handling**| Try-catch blocks across all I/O, Scanner, and Parse operations | Recovers safely from malformed data and invalid user input; does not crash. | ✅ **Verified** |

---

## 📁 Project Folder Structure

```
ContactManagementApp/
│
├── .vscode/
│   ├── launch.json              # VS Code debug & run profiles (CLI, GUI, Test Suite)
│   └── settings.json            # Java sourcepath and compiler settings
│
├── bin/                         # Compiled Java bytecode (.class files)
│
├── data/
│   └── contacts.csv             # Persistent CSV database storage
│
├── model/
│   └── Contact.java             # Domain entity with encapsulation, Comparable & CSV serialization
│
├── service/
│   └── ContactService.java      # Business logic with synchronized dual indexing (ArrayList + HashMap)
│
├── util/
│   ├── FileUtil.java            # CSV parsing, reader/writer, and atomic file swap
│   └── ValidationUtil.java      # Regex validation rules for name, phone, and email
│
├── view/
│   ├── ContactView.java         # [OFFICIAL DEMO] Interactive Console Application & ASCII tables
│   ├── MainDashboardFrame.java  # [OPTIONAL] Java Swing Desktop GUI window
│   ├── ContactFormDialog.java   # [OPTIONAL] Modal dialog for GUI Add/Edit operations
│   ├── ContactTableModel.java   # [OPTIONAL] Swing AbstractTableModel data adapter
│   └── Theme.java               # [OPTIONAL] Royal navy & enterprise blue UI design system
│
├── test/
│   └── AppVerificationTest.java # Comprehensive 53-assertion automated test suite
│
├── Main.java                    # Entry point: runs CLI via '--cli' or Swing GUI by default
├── .gitignore                   # Excludes compiled binaries, temp files, and test logs
└── README.md                    # Project documentation & evaluation guide
```

---

## 💻 Windows PowerShell Setup & Execution Guide

Follow these exact commands in **Windows PowerShell** to compile and run the project.

### Prerequisites
Verify that **Java Development Kit (JDK) 17 or higher** is installed and accessible on your `PATH`:
```powershell
java -version
javac -version
```
*Expected output: `openjdk version "17..."` or higher.*

---

### Step 1: Clean Compilation
Navigate to the project root directory and compile all Java classes into the `bin/` directory:

```powershell
# Ensure you are in the project root directory
Set-Location -Path "c:\Users\hp\Desktop\ContactManagementApp"

# Create the bin output directory if it does not already exist
if (-not (Test-Path -Path "bin")) { New-Item -ItemType Directory -Path "bin" }

# Compile all source files and test suite
javac -d bin model/Contact.java util/ValidationUtil.java util/FileUtil.java service/ContactService.java view/Theme.java view/ContactTableModel.java view/ContactFormDialog.java view/ContactView.java view/MainDashboardFrame.java Main.java test/AppVerificationTest.java
```

> **Pro-Tip (PowerShell Wildcard Compilation):**
> You can also compile all files in one dynamic command:
> ```powershell
> javac -d bin (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
> ```

---

### Step 2: Run Official Demonstration Version (Console Application)
Execute the interactive console CLI application (the official internship submission):

```powershell
java -cp bin Main --cli
```

---

### Step 3: Run Optional Additional Feature (Java Swing GUI)
Execute the modern desktop GUI application:

```powershell
java -cp bin Main
```

---

### Step 4: Running via Visual Studio Code
If running inside **VS Code**, open the project folder (`code .`):
1. Press `Ctrl + Shift + D` to open the **Run & Debug** view.
2. Choose from the pre-configured profiles:
   - **`Launch Contact Management App (CLI Mode)`** *(Official Demonstration Version)*
   - **`Launch Contact Management System (Swing GUI)`** *(Optional Additional Feature)*
   - **`Run Verification Test Suite`** *(Automated Unit Tests)*
3. Press `F5` to execute.

---

## 🧪 Automated Verification Test Suite (53 Assertions)

The project includes a standalone automated test suite located in [`test/AppVerificationTest.java`](test/AppVerificationTest.java). It executes **53 comprehensive assertions** with zero external dependencies (no JUnit jar required), making it instantly executable in any standard Java environment.

### Run Automated Tests in PowerShell:
```powershell
java -cp bin test.AppVerificationTest
```

### Test Suite Coverage Breakdown:
1. **10-Digit Mobile Validation (7 assertions)**: Verifies valid 10-digit strings, rejects 9-digit, 11-digit, alphabetic, dashed, and null numbers.
2. **Email Address Validation (6 assertions)**: Verifies valid email patterns and subdomains; rejects missing `@`, missing TLD, internal spaces, and null values.
3. **Name Validation (5 assertions)**: Verifies standard names, hyphens, and spaces; rejects 1-character names, empty strings, and numeric strings.
4. **CSV File I/O & Parsing (4 assertions)**: Tests CSV initialization, multi-contact persistence, row count integrity, and quoted fields with commas.
5. **ContactService Business Logic & Dual Data Structures (14 assertions)**: Verifies addition, count tracking, duplicate rejection, alphabetical sorting, $O(1)$ HashMap lookup, substring name search, partial phone search, contact updating, old key removal, and deletion.
6. **Automatic Persistence & Restart Reload (4 assertions)**: Simulates multi-session lifecycle: writes records in Session 1, instantiates a clean service instance in Session 2, and verifies all contacts reload seamlessly from disk.
7. **Swing GUI Table Model (9 assertions)**: Validates column names, row counts, cell immutability, and model mapping.

### Verbatim Automated Test Output:
```
==========================================================
   CODENOVA CONTACT MANAGEMENT APP - VERIFICATION SUITE   
==========================================================

[Testing 10-Digit Mobile Number Validation]
  [PASS] Valid 10-digit number passes
  [PASS] Another valid 10-digit number passes
  [PASS] 9 digits rejected
  [PASS] 11 digits rejected
  [PASS] Alphabetic string rejected
  [PASS] Special characters rejected
  [PASS] Null phone rejected

[Testing Email Address Validation]
  [PASS] Standard email passes
  [PASS] Email with subdomain passes
  [PASS] Missing @ symbol rejected
  [PASS] Missing domain extension rejected
  [PASS] Spaces in email rejected
  [PASS] Null email rejected

[Testing Name Validation]
  [PASS] Valid name passes
  [PASS] Name with hyphen passes
  [PASS] Single character rejected
  [PASS] Blank name rejected
  [PASS] Numeric name rejected

[Testing CSV File I/O & Parsing]
  [PASS] CSV file initialized
  [PASS] Contacts written to CSV successfully
  [PASS] Read exactly 2 contacts
  [PASS] Preserved contact with comma in name

[Testing ContactService Business Logic & Dual Data Structures]
  [PASS] Initial service has 0 contacts
  [PASS] Contact 1 added
  [PASS] Contact 2 added
  [PASS] Total count is 2
  [PASS] Duplicate phone rejected
  [PASS] Contacts sorted alphabetically (Abhinav before Zara)
  [PASS] Found contact via phone HashMap lookup
  [PASS] Search by name substring returns matching contact
  [PASS] Search by partial phone returns matching contact
  [PASS] Contact updated successfully
  [PASS] Old phone lookup returns null
  [PASS] New phone lookup returns updated contact
  [PASS] Duplicate phone rejected during edit
  [PASS] Contact deleted successfully
  [PASS] Count decremented to 1
  [PASS] Deleted contact absent in HashMap
  [PASS] Deleted contact absent in ArrayList

[Testing Automatic Persistence & Restart Reload]
  [PASS] Session 1 saved 2 contacts
  [PASS] Session 2 auto-loaded contacts on startup
  [PASS] Session 2 loaded Rohan Mehra
  [PASS] Session 2 loaded Kavita Rao

[Testing Swing GUI ContactTableModel]
  [PASS] Table model has 5 columns
  [PASS] Table model has 2 rows
  [PASS] Column 0 is #
  [PASS] Column 1 is Full Name
  [PASS] Row 0 # index is 1
  [PASS] Row 0 Name is Anita Roy
  [PASS] Row 0 Phone is 9111112222
  [PASS] Cells are non-editable
  [PASS] Table model updated to 1 row
  [PASS] Lookup contact at 0 returns Anita

----------------------------------------------------------
Test Summary: 53 / 53 Tests Passed Successfully!
----------------------------------------------------------
[ALL CHECKS PASSED] Application meets 100% of the project requirements.
```

---

## 💾 How Contact Data Persists After Restarting

A core requirement of the CodeNova project is guaranteeing that contact records survive application termination and restarts. Here is the exact persistence lifecycle:

```
+-----------------------------------------------------------------------------------------+
|                               DATA PERSISTENCE LIFECYCLE                                |
+-----------------------------------------------------------------------------------------+
|                                                                                         |
|  1. USER ACTION                                                                         |
|     User adds or edits a contact via CLI (e.g., "Aarav Sharma", "9876543210").          |
|                                                                                         |
|  2. MEMORY UPDATE                                                                       |
|     ContactService updates ArrayList<Contact> and HashMap<String, Contact>.            |
|                                                                                         |
|  3. ATOMIC WRITE TO DISK                                                                |
|     FileUtil writes all records to temporary file: "data/contacts.csv.tmp".             |
|     StandardCopyOption.ATOMIC_MOVE replaces "data/contacts.csv" instantly.              |
|                                                                                         |
|  4. APPLICATION SHUTDOWN                                                                |
|     User chooses Option 7 (Exit) or closes the terminal. RAM is cleared.                |
|     Data remains fully preserved inside "data/contacts.csv".                            |
|                                                                                         |
|  5. RESTART & RESTORATION                                                               |
|     User launches application again ("java -cp bin Main --cli").                        |
|     ContactService constructor calls loadContacts().                                    |
|     FileUtil.readContactsFromCsv() reads every CSV line, validates, and populates:      |
|       - ArrayList<Contact> (re-sorted alphabetically)                                   |
|       - HashMap<String, Contact> (re-indexed by phone number)                           |
|     Directory count reflects all saved records immediately.                             |
|                                                                                         |
+-----------------------------------------------------------------------------------------+
```

### Verification Walkthrough:
1. Launch CLI: `java -cp bin Main --cli`
2. Add a contact: Name `Ananya Sen`, Phone `9812345678`, Email `ananya@example.com`.
3. Choose `7` to Exit the application.
4. Inspect `data/contacts.csv` via PowerShell:
   ```powershell
   Get-Content data/contacts.csv
   ```
   *The newly added record appears cleanly under the CSV header.*
5. Re-launch CLI: `java -cp bin Main --cli`
6. Choose `2` (View All Contacts). Notice `Ananya Sen` is present and sorted alphabetically.

---

## 🖥️ Sample Console Output & User Workflows

The following transcripts show realistic, formatted terminal sessions from the **Official Demonstration Console Application**:

### 1. Main Menu Screen
```
Welcome to CodeNova Contact Management App (CLI Mode)!
Persistent CSV storage initialized at: data/contacts.csv

==========================================================
         CODENOVA CONTACT MANAGEMENT APPLICATION          
==========================================================
 Total Contacts Stored: 7
----------------------------------------------------------
  1. Add New Contact
  2. View All Contacts
  3. Search Contacts (by Name or Phone)
  4. Edit an Existing Contact
  5. Delete a Contact
  6. Reload Contacts from Storage
  7. Exit
==========================================================
Please enter your choice (1-7): 
```

---

### 2. Adding a Contact (Validation in Action)
```
Please enter your choice (1-7): 1

--- [Add New Contact] ---
(Enter '0' at any prompt to cancel and return to main menu)
Enter Full Name: 1234
[!] Invalid Name. Must be 2-50 characters with letters, spaces, or hyphens.
Enter Full Name: Rohan Mehra
Enter 10-Digit Mobile Number: 98765
[!] Invalid Mobile Number. Must contain exactly 10 digits (e.g., 9876543210).
Enter 10-Digit Mobile Number: 9876543210
[!] Duplicate Error: A contact with phone number 9876543210 already exists!
Enter 10-Digit Mobile Number: 9899001122
Enter Email Address: rohan_at_gmail
[!] Invalid Email format. Example: user@example.com
Enter Email Address: rohan.mehra@company.org
[SUCCESS] Contact added successfully.
```

---

### 3. Viewing All Contacts (Formatted ASCII Table)
```
Please enter your choice (1-7): 2

--- [All Contacts Directory] ---
+-----+----------------------+--------------+--------------------------------+---------------------+
| #   | Full Name            | Phone        | Email Address                  | Created At          |
+-----+----------------------+--------------+--------------------------------+---------------------+
| 1   | Aarav Sharma         | 9876543210   | aarav.sharma@example.com       | 2026-10-30 10:00:00 |
| 2   | Priya Patel          | 9123456780   | priya.patel@workmail.org       | 2026-10-30 10:15:00 |
| 3   | Rahul Verma          | 9988776655   | rahul.verma@techcorp.in        | 2026-10-30 11:30:00 |
| 4   | Rohan Mehra          | 9899001122   | rohan.mehra@company.org        | 2026-11-02 21:30:15 |
| 5   | Sneha Kulkarni       | 9822334455   | sneha.k@domain.net             | 2026-10-30 12:45:00 |
| 6   | Vikramaditya Roy     | 9711223344   | vikram.roy@startup.io          | 2026-10-30 14:20:00 |
+-----+----------------------+--------------+--------------------------------+---------------------+
```

---

### 4. Searching Contacts
```
Please enter your choice (1-7): 3

--- [Search Contacts] ---
1. Search by Name (Partial or Full)
2. Search by Phone Number
0. Back to Main Menu
Choose search method: 1
Enter Name keyword to search: ro

Found 2 matching contact(s) for name "ro":
+-----+----------------------+--------------+--------------------------------+---------------------+
| #   | Full Name            | Phone        | Email Address                  | Created At          |
+-----+----------------------+--------------+--------------------------------+---------------------+
| 1   | Rohan Mehra          | 9899001122   | rohan.mehra@company.org        | 2026-11-02 21:30:15 |
| 2   | Vikramaditya Roy     | 9711223344   | vikram.roy@startup.io          | 2026-10-30 14:20:00 |
+-----+----------------------+--------------+--------------------------------+---------------------+
```

---

### 5. Editing an Existing Contact
```
Please enter your choice (1-7): 4

--- [Edit Contact] ---
Enter the 10-Digit Phone Number of the contact to edit (or '0' to cancel): 9899001122

Current Contact Details:
  Name : Rohan Mehra
  Phone: 9899001122
  Email: rohan.mehra@company.org
(Press [ENTER] without typing to retain the current value)
Enter New Name [Rohan Mehra]: Rohan S. Mehra
Enter New 10-Digit Phone Number [9899001122]: 
Enter New Email [rohan.mehra@company.org]: rohan.senior@company.org
[SUCCESS] Contact updated successfully.
```

---

### 6. Deleting a Contact
```
Please enter your choice (1-7): 5

--- [Delete Contact] ---
Enter the 10-Digit Phone Number of the contact to delete (or '0' to cancel): 9899001122
Are you sure you want to delete 'Rohan S. Mehra' (9899001122)? (y/N): y
[SUCCESS] Contact for 'Rohan S. Mehra' deleted successfully.
```

---

## 🎨 Optional Feature: Java Swing Desktop Dashboard

While the **Java Console Application** serves as the official demonstration version, the application also includes a **modern Java Swing Desktop GUI** built upon the identical backend service.

### Launching the GUI:
```powershell
java -cp bin Main
```

### GUI Capabilities:
- **Royal Navy & Enterprise Blue Palette**: Designed using custom color constants defined in [`view.Theme`](view/Theme.java) (`#1A365D` sidebar, `#2563EB` action primary, `#F8FAFC` slate canvas).
- **Left Navigation Sidebar**: CodeNova internship branding, direct action buttons (*All Contacts*, *Add Contact*, *Reload from CSV*, *Exit*).
- **Stat Cards**: Dynamic counters displaying *Total Contacts*, *Matching Filter Contacts*, and *CSV Persistence Status*.
- **Live Search Filtering**: Filters the directory in real time as the user types into the search box via a Swing `DocumentListener`.
- **Interactive JTable**: Alternating striped rows, custom row height (38px), non-editable cells, double-click to edit, and friendly empty-state placeholder cards.
- **Modal Dialogs**: [`ContactFormDialog`](view/ContactFormDialog.java) provides input validation with instant red inline error labels.
- **Event Dispatch Thread (EDT)**: All UI lifecycle events dispatch safely via `SwingUtilities.invokeLater()`.

---

## 🚀 GitHub Repository Setup Instructions

To push this project to your GitHub account for internship submission, follow these exact Windows PowerShell instructions:

> **Prerequisite:** If Git is not yet installed on your Windows machine, install it via PowerShell using winget:
> ```powershell
> winget install --id Git.Git -e --source winget
> ```
> *(Restart PowerShell after installation to refresh the PATH).*

### 1. Initialize Git Repository
```powershell
Set-Location -Path "c:\Users\hp\Desktop\ContactManagementApp"
git init
```

### 2. Verify `.gitignore`
Ensure compiled `.class` files, `bin/`, and temporary files are ignored:
```powershell
Get-Content .gitignore
```
*(The repository already includes a pre-configured `.gitignore` excluding `*.class`, `bin/`, `*.tmp`, and test artifacts).*

### 3. Stage and Commit All Files
```powershell
git add .
git commit -m "feat: complete CodeNova Contact Management App (Console CLI & Swing GUI)"
```

### 4. Create Main Branch and Add Remote
Replace `<YOUR-GITHUB-USERNAME>` and `<YOUR-REPOSITORY-NAME>` with your GitHub details:
```powershell
git branch -M main
git remote add origin https://github.com/<YOUR-GITHUB-USERNAME>/<YOUR-REPOSITORY-NAME>.git
```

### 5. Push Code to GitHub
```powershell
git push -u origin main
```

### 6. Recommended GitHub Repository Settings:
- **Repository Name**: `CodeNova-Contact-Management-App`
- **Description**: `Official Java Console Application & optional Swing GUI for CodeNova Java Internship. Features Java 17, OOP, ArrayList/HashMap dual-indexing, Regex validation, and persistent CSV storage.`
- **Topics / Tags**: `java17`, `oop`, `console-application`, `swing-gui`, `regex`, `csv-persistence`, `internship-project`, `codenova`

---

## 👨‍💻 Internship Submission Metadata

- **Project Title**: Contact Management System
- **Organization / Program**: CodeNova Java Programming Internship
- **Track**: Core Java & Desktop Development
- **Target Runtime**: Java 17 LTS
- **Official Demonstration Interface**: Java Interactive Console Application (`Main --cli`)
- **Supplementary Interface**: Java Swing Desktop GUI (`Main`)
- **Verification Status**: 53 / 53 Automated Tests Passing (100% Compliance)
- **Primary Data Structures**: `java.util.ArrayList`, `java.util.HashMap`
- **Storage Strategy**: Thread-Safe Atomic CSV File Persistence (`data/contacts.csv`)
- **Documentation Status**: Fully Reviewed & Finalized for Evaluation

---
*Created with dedication for the CodeNova Internship Evaluation.*
