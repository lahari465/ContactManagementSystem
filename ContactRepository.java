import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
 
/**
 * In-memory contact store.
 *
 * Data Structure choices explained:
 *  - ArrayList<Contact>: O(n) search is acceptable for typical contact counts
 *    (< 10,000). ArrayList beats LinkedList here because we search far more
 *    often than we insert/delete, and ArrayList has superior cache locality.
 *
 *  - HashSet<String> phoneIndex: O(1) average duplicate detection by phone.
 *    Prevents two contacts sharing the same phone number without requiring
 *    a full O(n) scan of the list on every add/update.
 */
public class ContactRepository {
 
    private final ArrayList<Contact> contacts = new ArrayList<>();
    private final HashSet<String> phoneIndex = new HashSet<>();  // O(1) dupe guard
 
    // ------------------------------------------------------------------ CREATE
 
    /**
     * Adds a new contact. Throws if phone number is already registered.
     */
    public Contact add(String name, String phone, String email) {
        if (phoneIndex.contains(normalizePhone(phone))) {
            throw new IllegalStateException(
                "A contact with phone number " + phone + " already exists.");
        }
        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Contact c = new Contact(id, name, phone, email);   // validation happens here
        contacts.add(c);
        phoneIndex.add(normalizePhone(phone));
        return c;
    }
 
    // -------------------------------------------------------------------- READ
 
    /** Returns all contacts (unmodifiable view). */
    public List<Contact> findAll() {
        return List.copyOf(contacts);
    }
 
    /** Finds a contact by ID. Returns Optional.empty() if not found. */
    public Optional<Contact> findById(String id) {
        return contacts.stream()
            .filter(c -> c.getId().equalsIgnoreCase(id))
            .findFirst();
    }
 
    /**
     * Case-insensitive search across name, phone, and email.
     * "john" will match "John", "JOHN", "johnny@email.com", etc.
     */
    public List<Contact> search(String query) {
        String q = query.toLowerCase();
        return contacts.stream()
            .filter(c ->
                c.getName().toLowerCase().contains(q) ||
                c.getPhoneNumber().contains(q) ||
                c.getEmail().toLowerCase().contains(q))
            .collect(Collectors.toList());
    }
 
    // ------------------------------------------------------------------ UPDATE
 
    /**
     * Updates a contact by ID. Only non-null fields are changed.
     * If phone is updated, checks for duplicates against other contacts.
     */
    public Contact update(String id, String newName, String newPhone, String newEmail) {
        Contact c = findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + id));
 
        if (newName != null)  c.setName(newName);
        if (newEmail != null) c.setEmail(newEmail);
 
        if (newPhone != null) {
            String normalized = normalizePhone(newPhone);
            if (!normalized.equals(normalizePhone(c.getPhoneNumber()))) {
                if (phoneIndex.contains(normalized)) {
                    throw new IllegalStateException(
                        "Phone number " + newPhone + " belongs to another contact.");
                }
                phoneIndex.remove(normalizePhone(c.getPhoneNumber()));
                c.setPhoneNumber(newPhone);
                phoneIndex.add(normalized);
            }
        }
        return c;
    }
 
    // ------------------------------------------------------------------ DELETE
 
    /**
     * Removes a contact by ID. Returns true if removed, false if not found.
     */
    public boolean delete(String id) {
        Optional<Contact> found = findById(id);
        if (found.isEmpty()) return false;
        phoneIndex.remove(normalizePhone(found.get().getPhoneNumber()));
        contacts.remove(found.get());
        return true;
    }
 
    /** Returns the total number of stored contacts. */
    public int size() {
        return contacts.size();
    }
 
    // ----------------------------------------------------------------- HELPERS
 
    private String normalizePhone(String phone) {
        return phone == null ? "" : phone.trim().replaceAll("[\\s()-]", "");
    }
}
 
