package org.iperp.Enums;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum UserRole {

    @FieldNameConstants.Include
    DEVELOPER,
    @FieldNameConstants.Include
    RECRUITER,
    USER,
    ADMIN

}
