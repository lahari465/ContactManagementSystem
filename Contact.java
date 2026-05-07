import java.util.regex.Pattern;
 
/**
 * Contact POJO with encapsulated fields and input validation.
 * Demonstrates: Encapsulation, Regex validation, POJO design pattern.
 */
public class Contact {
 
    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[+]?[0-9]{7,15}$");
 
    private final String id;        // Immutable unique ID
    private String name;
    private String phoneNumber;
    private String email;
 
    public Contact(String id, String name, String phoneNumber, String email) {
        this.id = id;
        setName(name);
        setPhoneNumber(phoneNumber);
        setEmail(email);
    }
 
    // --- Getters ---
 
    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail()       { return email; }
 
    // --- Setters with validation ---
 
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name.trim();
    }
 
    public void setPhoneNumber(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone.trim()).matches())
            throw new IllegalArgumentException(
                "Invalid phone number. Use digits only (7–15 chars), optional leading +.");
        this.phoneNumber = phone.trim();
    }
 
    public void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches())
            throw new IllegalArgumentException(
                "Invalid email format. Expected: user@domain.com");
        this.email = email.trim().toLowerCase();
    }
 
    // --- Static validation helpers (usable before object creation) ---
 
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
 
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }
 
    // --- VCard export (Tier 2 upgrade) ---
 
    /**
     * Exports contact data as a standard VCF (vCard 3.0) string.
     * Can be written to a .vcf file and imported into any phone or Outlook.
     */
    public String toVCard() {
        return "BEGIN:VCARD\n" +
               "VERSION:3.0\n" +
               "FN:" + name + "\n" +
               "TEL;TYPE=CELL:" + phoneNumber + "\n" +
               "EMAIL:" + email + "\n" +
               "END:VCARD\n";
    }
 
    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s", id, name, phoneNumber, email);
    }
}