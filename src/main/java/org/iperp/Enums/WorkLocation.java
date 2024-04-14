package org.iperp.Enums;

import lombok.Getter;

@Getter
public enum WorkLocation {
    REMOTE("Remote"),
    HYBRID("Hybrid"),
    ON_SITE("On Site");

    private final String displayName;

    WorkLocation(String displayName) {
        this.displayName = displayName;
    }
}
