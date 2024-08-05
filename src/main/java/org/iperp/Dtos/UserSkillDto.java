package org.iperp.Dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSkillDto {

    @NotEmpty(message = "This field is required.")
    private Long id;

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    private String description;

    private Integer years;

    private boolean matched;
}
