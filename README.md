Here’s a cleaner and more professional README structure that looks much better on GitHub:

````markdown
# 📇 Contact Manager

A terminal-based Contact Manager application built using Java.  
This project allows users to create, search, update, delete, import, and export contacts using the vCard 3.0 format.

The main goal of this project was to practice:
- Core Java
- OOP principles
- File handling
- Data structures
- Clean project architecture

---

# 🚀 Features

✔ Add Contacts  
✔ View All Contacts  
✔ Search Contacts  
✔ Update Existing Contacts  
✔ Delete Contacts  
✔ Prevent Duplicate Phone Numbers  
✔ Import Contacts from `.vcf` Files  
✔ Export Contacts to `.vcf` Files  
✔ Regex-based Input Validation  
✔ Unit Testing with JUnit 5  

---

# 🛠 Tech Stack

## Core Java
- Java 20
- ArrayList
- HashSet
- Optional
- Stream API
- UUID
- Records (`record`)
- Regex / Pattern
- Scanner

## File Handling
- `java.nio.file.Files`
- `java.nio.file.Path`

## Concepts Used
- OOP
- Encapsulation
- CRUD Operations
- Repository Pattern
- vCard 3.0 Specification

## Tools
- VS Code
- Java Extension Pack
- Maven
- JUnit 5

---

# 📂 Project Structure

```bash
contact-manager/
│
├── src/
│   ├── model/
│   │   └── Contact.java
│   │
│   ├── repository/
│   │   └── ContactRepository.java
│   │
│   ├── service/
│   │   └── VCardManager.java
│   │
│   ├── ui/
│   │   │── Main.java
│   │
│   └── test/
│       └── ContactRepositoryTest.java
│
├── contacts.vcf
├── pom.xml
└── README.md
```

---

# ⚙️ How It Works

## Contact Storage
Contacts are stored in memory using an `ArrayList`.

## Duplicate Detection
A `HashSet` is used to detect duplicate phone numbers in O(1) time complexity.

## Searching
The Stream API is used for filtering and searching contacts efficiently.

Example:

```java
contacts.stream()
        .filter(contact -> contact.getName().contains(keyword))
        .toList();
```

## Validation
Regex patterns are used to validate:
- Phone numbers
- Email addresses

---

# 📄 vCard Support

This project supports the **vCard 3.0** format, making it compatible with:
- Android Contacts
- iPhone Contacts
- Google Contacts
- Microsoft Outlook

Example:

```vcf
BEGIN:VCARD
VERSION:3.0
FN:John Doe
TEL:9876543210
EMAIL:john@example.com
END:VCARD
```

---

# ▶️ Getting Started

## Clone the Repository

```bash
git clone <your-repository-url>
cd contact-manager
```

---

# 🔧 Run the Project

## Using VS Code
1. Install Java Extension Pack
2. Open the project folder
3. Run `Main.java`

## Using Maven

### Compile
```bash
mvn compile
```

### Run
```bash
mvn exec:java
```

### Test
```bash
mvn test
```

---

# 🧪 Testing

JUnit 5 is used for unit testing repository operations and validation logic.

Run tests using:

```bash
mvn test
```

---

# 📈 Future Improvements

- Database Integration (MySQL/PostgreSQL)
- GUI using JavaFX
- REST API with Spring Boot
- Contact Groups & Favorites
- Cloud Backup Support
- CSV Import/Export

---

# 📚 Learning Outcomes

Through this project, I learned:
- Clean Java project structuring
- Applying OOP principles
- File handling with Java NIO
- Using Streams & Optional effectively
- Regex-based validation
- Repository Pattern implementation
- Writing testable and maintainable code

---

# 👩‍💻 Author

**Lahari S**

Java backend practice project focused on strengthening Core Java and software design fundamentals.
````
