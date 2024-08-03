package org.iperp.Services.Implementation;

import org.iperp.Dtos.SkillDto;
import org.iperp.Entities.Skill;
import org.iperp.Repositories.ISkillRepository;
import org.iperp.Services.ISkillService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService implements ISkillService {

    private final ISkillRepository skillRepository;

    public SkillService(ISkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<SkillDto> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private SkillDto mapToDto(Skill skill) {
        SkillDto dto = new SkillDto();
        dto.setId(skill.getId());
        dto.setDescription(skill.getDescription());
        return dto;
    }
}
