package com.example.bolgebaderne.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String passwordHash;

    private String membershipStatus;

    // ===== Dine helper-metoder =====

    // Alle roller er har medlemstilgang undtagen GUEST
    public boolean isMember() { return role == Role.MEMBER || role == Role.ADMIN; }


    // ===== Contructor =====

    public User() { }

    public User(int userId, String name, String email, Role role,
                String passwordHash, String membershipStatus) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.membershipStatus = membershipStatus;
    }

    // ===== Getters/setters  =====

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

    // ===== UserDetails-implementation til Spring Security =====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring forventer "ROLE_MEMBER", "ROLE_ADMIN", ...
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash; //læser fra kolonnen password_hash
    }

    @Override
    public String getUsername() {
        // Vi logger ind med email
        return email;
    }

    // De her kan bare være true for nu
    @Override public boolean isAccountNonExpired() {
        return true;
    }
    @Override public boolean isAccountNonLocked() {
        return true;
    }
    @Override public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override public boolean isEnabled() {
        return true;
    }
}
