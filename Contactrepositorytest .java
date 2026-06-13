package contactmanager;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContactRepositoryTest {

    static ContactRepository repo;
    static String aliceId;

    @BeforeAll
    static void setup() {
        repo = new ContactRepository();
    }

    // ================================================================ ADD

    @Test @Order(1)
    void addValidContact() {
        Contact c = repo.add("Alice Smith", "+14155550100", "alice@example.com");
        aliceId = c.getId();
        assertNotNull(c.getId());
        assertEquals("Alice Smith", c.getName());
        assertEquals(1, repo.size());
    }

    @Test @Order(2)
    void addSecondContact() {
        Contact c = repo.add("Bob Jones", "+14155550200", "bob@example.com");
        assertNotNull(c.getId());
        assertEquals(2, repo.size());
    }

    @Test @Order(3)
    void addDuplicatePhoneThrows() {
        assertThrows(IllegalStateException.class,
            () -> repo.add("Fake Alice", "+14155550100", "fake@example.com"));
    }

    @Test @Order(4)
    void addInvalidEmailThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> repo.add("Carol", "+14155550300", "not-an-email"));
    }

    @Test @Order(5)
    void addInvalidPhoneThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> repo.add("Dave", "abcdefgh", "dave@example.com"));
    }

    @Test @Order(6)
    void addEmptyNameThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> repo.add("  ", "+14155550400", "empty@example.com"));
    }

    // ============================================================== FIND

    @Test @Order(7)
    void findAllReturnsAllContacts() {
        List<Contact> all = repo.findAll();
        assertEquals(2, all.size());
    }

    @Test @Order(8)
    void findByIdSuccess() {
        Optional<Contact> c = repo.findById(aliceId);
        assertTrue(c.isPresent());
        assertEquals("Alice Smith", c.get().getName());
    }

    @Test @Order(9)
    void findByIdNotFound() {
        Optional<Contact> c = repo.findById("ZZZZZZ");
        assertFalse(c.isPresent());
    }

    // ============================================================= SEARCH

    @Test @Order(10)
    void searchByNameCaseInsensitive() {
        List<Contact> results = repo.search("alice");
        assertEquals(1, results.size());
        assertEquals("Alice Smith", results.get(0).getName());
    }

    @Test @Order(11)
    void searchByPartialName() {
        List<Contact> results = repo.search("smi");
        assertFalse(results.isEmpty());
    }

    @Test @Order(12)
    void searchByEmail() {
        List<Contact> results = repo.search("bob@example");
        assertEquals(1, results.size());
    }

    @Test @Order(13)
    void searchByPhone() {
        List<Contact> results = repo.search("550200");
        assertEquals(1, results.size());
    }

    @Test @Order(14)
    void searchNoResults() {
        List<Contact> results = repo.search("zzz_nobody_zzz");
        assertTrue(results.isEmpty());
    }

    // ============================================================== UPDATE

    @Test @Order(15)
    void updateName() {
        Contact updated = repo.update(aliceId, "Alice Cooper", null, null);
        assertEquals("Alice Cooper", updated.getName());
    }

    @Test @Order(16)
    void updateEmail() {
        Contact updated = repo.update(aliceId, null, null, "alicecooper@example.com");
        assertEquals("alicecooper@example.com", updated.getEmail());
    }

    @Test @Order(17)
    void updateWithInvalidEmailThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> repo.update(aliceId, null, null, "bademail"));
    }

    @Test @Order(18)
    void updateWithDuplicatePhoneThrows() {
        assertThrows(IllegalStateException.class,
            () -> repo.update(aliceId, null, "+14155550200", null));
    }

    @Test @Order(19)
    void updateNonExistentIdThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> repo.update("BADID", "Ghost", null, null));
    }

    // ============================================================== DELETE

    @Test @Order(20)
    void deleteExistingContact() {
        int sizeBefore = repo.size();
        boolean deleted = repo.delete(aliceId);
        assertTrue(deleted);
        assertEquals(sizeBefore - 1, repo.size());
    }

    @Test @Order(21)
    void deletedContactNotFoundAfterDelete() {
        Optional<Contact> c = repo.findById(aliceId);
        assertFalse(c.isPresent());
    }

    @Test @Order(22)
    void deleteNonExistentReturnsFalse() {
        assertFalse(repo.delete("BADID"));
    }

    @Test @Order(23)
    void phoneFreedAfterDelete() {
        assertDoesNotThrow(
            () -> repo.add("New Alice", "+14155550100", "newalice@example.com"));
    }

    // ============================================================== VCARD

    @Test @Order(24)
    void vCardFormat() {
        Contact c = repo.findAll().stream()
            .filter(x -> x.getName().equals("New Alice"))
            .findFirst().orElseThrow();
        String vcf = c.toVCard();
        assertTrue(vcf.contains("BEGIN:VCARD"));
        assertTrue(vcf.contains("VERSION:3.0"));
        assertTrue(vcf.contains("FN:New Alice"));
        assertTrue(vcf.contains("TEL"));
        assertTrue(vcf.contains("EMAIL:"));
        assertTrue(vcf.contains("END:VCARD"));
    }
}
