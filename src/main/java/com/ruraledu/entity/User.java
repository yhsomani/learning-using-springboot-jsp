package com.ruraledu.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "Username is required")
    @jakarta.validation.constraints.Size(min = 4, max = 50)
    @Column(unique = true, nullable = false)
    private String username;

    @jakarta.validation.constraints.NotBlank(message = "Password is required")
    @jakarta.validation.constraints.Size(min = 6)
    @Column(nullable = false)
    private String password;

    @jakarta.validation.constraints.NotBlank(message = "Full name is required")
    @Column(nullable = false)
    private String fullName;

    @jakarta.validation.constraints.NotBlank(message = "Email is required")
    @jakarta.validation.constraints.Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private int points = 0;

    private String location;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String language = "Hindi";

    private boolean enabled = true;
    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User parent;

    public enum Role {
        STUDENT, TEACHER, PARENT, ADMIN
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public User getParent() { return parent; }
    public void setParent(User parent) { this.parent = parent; }
}
