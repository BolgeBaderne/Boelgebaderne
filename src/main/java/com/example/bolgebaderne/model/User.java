package com.example.bolgebaderne.model;


public class User
{
    private int userId;

    private String name;
    private String email;
    private Role role;
    private String passwordHash;
    private boolean membershipStatus;

    public boolean isMember() {
        return membershipStatus;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }


    public User(int userId, String name, String email, Role role, String passwordHash, boolean membershipStatus)
    {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
        this.membershipStatus = membershipStatus;
    }


    public int      getUserId()                 { return userId; }
    public void     setUserId(int userId)       { this.userId = userId; }

    public String   getName()                   { return name; }
    public void     setName(String name)        { this.name = name; }

    public String   getEmail()                  { return email; }
    public void     setEmail(String email)      { this.email = email; }

    public Role     getRole()                   { return role; }
    public void     setRole(Role role)          { this.role = role; }

    public String   getPasswordHash()           { return passwordHash; }
    public void     setPasswordHash(String passwordHash)
    { this.passwordHash = passwordHash; }

    public boolean  isMembershipStatus()        { return membershipStatus; }
    public void     setMembershipStatus(boolean membershipStatus)
    { this.membershipStatus = membershipStatus; }


}
