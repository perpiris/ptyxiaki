package org.iperp.Enums;

import lombok.Getter;

@Getter
public enum JobType {
    FULL_TIME("Full Time"),
    PART_TIME("Part Time");

    private final String displayName;

    JobType(String displayName) {
        this.displayName = displayName;
    }
}
