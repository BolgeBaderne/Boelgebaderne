package com.example.bolgebaderne.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // matcher _schema.sql (CREATE TABLE users)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT i H2/MySQL
    @Column(name = "user_id")
    private int userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "membership_status", nullable = false, length = 20)
    private String membershipStatus;

    // JPA kræver en no-arg constructor
 public User() {
    }

    public User(int userId, String name, String email, Role role,
                String passwordHash, String membershipStatus) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.membershipStatus = membershipStatus;
    }

    public boolean isMember() {
        return role == Role.MEMBER;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    // Getters & setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getMembershipStatus() { return membershipStatus; }
    public void setMembershipStatus(String membershipStatus) { this.membershipStatus = membershipStatus; }
}
