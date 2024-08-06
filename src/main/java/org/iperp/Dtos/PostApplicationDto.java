package org.iperp.Dtos;

import lombok.Getter;
import lombok.Setter;
import org.iperp.Enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostApplicationDto {
    
    private Long id;
    
    private ApplicationStatus status;
    
    private LocalDateTime createdOn;
    
    private AppUserDto user;
    
    private List<UserSkillDto> skills;
}
