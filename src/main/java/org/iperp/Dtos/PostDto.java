package org.iperp.Dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.iperp.Enums.JobLocation;
import org.iperp.Enums.JobType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostDto {

    private Long id;

    @NotEmpty(message = "This field is required.")
    @Size(max = 255)
    private String title;

    @NotEmpty(message = "This field is required.")
    private String description;

    @NotNull(message = "This field is required.")
    private JobType jobType;

    @NotNull(message = "This field is required.")
    private JobLocation jobLocation;

    private String createdOn;

    private List<PostSkillDto> skills = new ArrayList<>();

}
