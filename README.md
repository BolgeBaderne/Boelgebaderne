# Bølgebaderne 🌊

A Spring Boot web application for managing sauna events and memberships.

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- A running database (H2 / MySQL / PostgreSQL depending on config)

### Run the Application

```bash
mvn spring-boot:run
```

The app will start on [http://localhost:8080](http://localhost:8080)

---

## 👤 Test Users

The following users are pre-seeded via `data.sql`:

| Name        | Email                  | Password   | Role       | Membership  |
|-------------|------------------------|------------|------------|-------------|
| Member One  | member1@example.com    | `password` | MEMBER     | MEMBER      |
| Guest One   | guest1@example.com     | `password` | NON_MEMBER | NON_MEMBER  |
| Admin One   | admin1@example.com     | `admin`    | ADMIN      | MEMBER      |

> ⚠️ These are development credentials only. Do **not** use in production.

---

## 🗂️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── ...
│   │       ├── BolgebaderneApplication.java   # Main entry point
│   │       ├── security/                       # Spring Security config
│   │       ├── service/                        # Business logic
│   │       └── LoginController.java            # Login handling
│   └── resources/
│       ├── templates/                          # Thymeleaf HTML templates
│       ├── static/                             # CSS, JS, images
│       ├── application.properties              # App configuration
│       ├── data.sql                            # Seed data
│       └── schema.sql                          # Database schema
```

---

## ✨ Features

- User authentication & role-based access (MEMBER, NON_MEMBER, ADMIN)
- Sauna event management (`sauna_event` table)
- Thymeleaf-rendered frontend
- Spring Security integration
- Hibernate ORM

---

## 🛠️ Tech Stack

| Layer      | Technology             |
|------------|------------------------|
| Backend    | Java + Spring Boot     |
| Frontend   | Thymeleaf + HTML/CSS   |
| ORM        | Hibernate / JPA        |
| Security   | Spring Security        |
| Build Tool | Maven                  |

---

## 📄 License

This project is for internal/educational use.
