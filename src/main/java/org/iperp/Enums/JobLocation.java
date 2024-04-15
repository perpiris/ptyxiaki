package org.iperp.Enums;

import lombok.Getter;

@Getter
public enum JobLocation {
    REMOTE("Remote"),
    HYBRID("Hybrid"),
    ON_SITE("On Site");

    private final String displayName;

    JobLocation(String displayName) {
        this.displayName = displayName;
    }
}
