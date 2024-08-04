package org.iperp.Dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSkillDto {

    private Long id;

    @NotEmpty(message = "This field is required.")
    private String description;

    @NotEmpty(message = "This field is required.")
    private Integer years;
}
