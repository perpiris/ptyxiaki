package org.iperp.Enums;

import lombok.Getter;

@Getter
public enum ApplicationStatus {
    PENDING("Pending"),
    REJECTED("Rejected"),
    APPROVED("Approved"),
    WITHDRAWN("Withdrawn");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }
}
