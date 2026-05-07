import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Command-Line Interface for the Contact Management System.
 * Demonstrates: CRUD lifecycle, input validation, encapsulation in action.
 */
public class ContactManagerCLI {

    private static final ContactRepository repo = new ContactRepository();
    private static final Vcardmanager vcardManager = new Vcardmanager(repo);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║   Contact Management System  ║");
        System.out.println("╚══════════════════════════════╝\n");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> addContact();
                case "2" -> viewAll();
                case "3" -> searchContact();
                case "4" -> updateContact();
                case "5" -> deleteContact();
                case "6" -> exportContacts();
                case "7" -> importContacts();
                case "0" -> { running = false; System.out.println("Goodbye!"); }
                default  -> System.out.println("Invalid option. Try again.\n");
            }
        }
    }

    // ------------------------------------------------------------ MENU

    private static void printMenu() {
        System.out.println("─────────────────────────────");
        System.out.println(" 1. Add contact");
        System.out.println(" 2. View all contacts");
        System.out.println(" 3. Search contacts");
        System.out.println(" 4. Update contact");
        System.out.println(" 5. Delete contact");
        System.out.println(" 6. Export to .vcf");
        System.out.println(" 7. Import from .vcf");
        System.out.println(" 0. Exit");
        System.out.print("Choice: ");
    }

    // ------------------------------------------------------------ ADD

    private static void addContact() {
        System.out.println("\n-- Add Contact --");
        try {
            System.out.print("Name:  ");  String name  = scanner.nextLine().trim();
            System.out.print("Phone: ");  String phone = scanner.nextLine().trim();
            System.out.print("Email: ");  String email = scanner.nextLine().trim();
            Contact c = repo.add(name, phone, email);
            System.out.println("✓ Added: " + c + "\n");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage() + "\n");
        }
    }

    // ------------------------------------------------------------ VIEW

    private static void viewAll() {
        List<Contact> all = repo.findAll();
        if (all.isEmpty()) {
            System.out.println("\nNo contacts yet.\n");
            return;
        }
        System.out.println("\n-- All Contacts (" + all.size() + ") --");
        all.forEach(c -> System.out.println(c));
        System.out.println();
    }

    // ----------------------------------------------------------- SEARCH

    private static void searchContact() {
        System.out.print("\nSearch query: ");
        String q = scanner.nextLine().trim();
        List<Contact> results = repo.search(q);
        if (results.isEmpty()) {
            System.out.println("No contacts found for: \"" + q + "\"\n");
        } else {
            System.out.println("Found " + results.size() + " result(s):");
            results.forEach(c -> System.out.println("  " + c));
            System.out.println();
        }
    }

    // ---------------------------------------------------------- UPDATE

    private static void updateContact() {
        System.out.print("\nEnter contact ID to update: ");
        String id = scanner.nextLine().trim();
        try {
            System.out.println("(Leave blank to keep current value)");
            System.out.print("New name:  ");  String name  = blankToNull(scanner.nextLine());
            System.out.print("New phone: ");  String phone = blankToNull(scanner.nextLine());
            System.out.print("New email: ");  String email = blankToNull(scanner.nextLine());
            Contact c = repo.update(id.toUpperCase(), name, phone, email);
            System.out.println("✓ Updated: " + c + "\n");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage() + "\n");
        }
    }

    // ---------------------------------------------------------- DELETE

    private static void deleteContact() {
        System.out.print("\nEnter contact ID to delete: ");
        String id = scanner.nextLine().trim().toUpperCase();
        if (repo.delete(id)) {
            System.out.println("✓ Contact " + id + " deleted.\n");
        } else {
            System.out.println("✗ Contact not found: " + id + "\n");
        }
    }

    // ----------------------------------------------------------- VCARD

    private static void exportContacts() {
        System.out.print("\nExport file path (e.g. contacts.vcf): ");
        String path = scanner.nextLine().trim();
        try {
            int count = vcardManager.exportAll(Path.of(path));
            System.out.println("✓ Exported " + count + " contact(s) to " + path + "\n");
        } catch (IOException e) {
            System.out.println("✗ Export failed: " + e.getMessage() + "\n");
        }
    }

    private static void importContacts() {
        System.out.print("\nImport file path (e.g. contacts.vcf): ");
        String path = scanner.nextLine().trim();
        try {
            Vcardmanager.ImportResult result = vcardManager.importFrom(Path.of(path));
            System.out.println("✓ " + result);
            if (!result.errors().isEmpty()) {
                result.errors().forEach(e -> System.out.println("  ⚠ " + e));
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("✗ Import failed: " + e.getMessage() + "\n");
        }
    }

    // ---------------------------------------------------------- UTIL

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}