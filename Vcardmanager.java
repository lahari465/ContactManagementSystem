import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * VCard Import/Export — Tier 2 Upgrade.
 *
 * File Parsing Strategy:
 *  1. Export: serialize each Contact to the vCard 3.0 spec and write to a .vcf file.
 *     Multiple contacts are concatenated (standard multi-card VCF format).
 *  2. Import: read the .vcf file line by line, detect BEGIN:VCARD / END:VCARD blocks,
 *     extract FN (full name), TEL, and EMAIL fields, then add them to the repository.
 *
 * Why vCard 3.0?
 *  - Supported by Google Contacts, Apple Contacts, Outlook, and all Android/iOS devices.
 *  - Human-readable plain text — easy to debug and version-control.
 */
public class Vcardmanager {

    private final ContactRepository repo;

    public Vcardmanager(ContactRepository repo) {
        this.repo = repo;
    }

    // ----------------------------------------------------------------- EXPORT

    /**
     * Exports all contacts to a .vcf file at the given path.
     * @return number of contacts written
     */
    public int exportAll(Path outputPath) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<Contact> all = repo.findAll();
        for (Contact c : all) {
            sb.append(c.toVCard()).append("\n");
        }
        Files.writeString(outputPath, sb.toString());
        return all.size();
    }

    /**
     * Exports a single contact by ID to a .vcf file.
     */
    public void exportOne(String contactId, Path outputPath) throws IOException {
        Contact c = repo.findById(contactId)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + contactId));
        Files.writeString(outputPath, c.toVCard());
    }

    // ----------------------------------------------------------------- IMPORT

    /**
     * Imports contacts from a .vcf file into the repository.
     * Skips contacts whose phone numbers already exist (duplicate-safe).
     * @return ImportResult with counts of imported and skipped contacts
     */
    public ImportResult importFrom(Path vcfPath) throws IOException {
        List<String> lines = Files.readAllLines(vcfPath);
        int imported = 0, skipped = 0;
        List<String> errors = new ArrayList<>();

        String name = null, phone = null, email = null;
        boolean inCard = false;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.equals("BEGIN:VCARD")) {
                inCard = true;
                name = phone = email = null;
            } else if (line.equals("END:VCARD") && inCard) {
                inCard = false;
                if (name != null && phone != null && email != null) {
                    try {
                        repo.add(name, phone, email);
                        imported++;
                    } catch (Exception e) {
                        skipped++;
                        errors.add(name + ": " + e.getMessage());
                    }
                } else {
                    skipped++;
                    errors.add("Incomplete vCard for: " + name);
                }
            } else if (inCard) {
                if (line.startsWith("FN:"))
                    name = line.substring(3).trim();
                else if (line.startsWith("TEL"))
                    phone = line.substring(line.indexOf(':') + 1).trim();
                else if (line.startsWith("EMAIL:"))
                    email = line.substring(6).trim();
            }
        }
        return new ImportResult(imported, skipped, errors);
    }

    // ---------------------------------------------------------------- RECORD

    public record ImportResult(int imported, int skipped, List<String> errors) {
        @Override public String toString() {
            return String.format("Imported: %d | Skipped: %d", imported, skipped);
        }
    }
}