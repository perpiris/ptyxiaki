package org.iperp.Dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSkillDto {

    private Long skillId;

    @NotEmpty(message = "This field is required.")
    private String description;

    @NotEmpty(message = "This field is required.")
    private Integer years;
}
