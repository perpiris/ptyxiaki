package org.iperp.Enums;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("User"),
    ADMIN("Admin"),
    DEVELOPER("Developer"),
    RECRUITER("Recruiter");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }
}
