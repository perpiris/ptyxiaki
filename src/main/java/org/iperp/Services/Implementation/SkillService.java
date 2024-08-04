package org.iperp.Services.Implementation;

import org.iperp.Dtos.SkillDto;
import org.iperp.Entities.AppUser;
import org.iperp.Entities.Skill;
import org.iperp.Entities.UserSkill;
import org.iperp.Repositories.IAppUserRepository;
import org.iperp.Repositories.ISkillRepository;
import org.iperp.Security.SecurityUtility;
import org.iperp.Services.ISkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SkillService implements ISkillService {

    @Autowired
    private ISkillRepository skillRepository;
    @Autowired
    private IAppUserRepository userRepository;

    public List<SkillDto> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<UserSkill> getUserSkills() {
        String username = SecurityUtility.getSessionUser();
        AppUser user = userRepository.findByUsernameIgnoreCase(username);
        return user.getSkills();
    }

    public void addSkillToUser(String skillDescription, int years) {
        String username = SecurityUtility.getSessionUser();
        AppUser user = userRepository.findByUsernameIgnoreCase(username);
        Skill skill = skillRepository.findByDescriptionIgnoreCase(skillDescription.trim().toUpperCase())
                .orElseGet(() -> createNewSkill(skillDescription));

        Optional<UserSkill> existingUserSkill = user.getSkills().stream()
                .filter(us -> us.getSkill().getId().equals(skill.getId()))
                .findFirst();

        if (existingUserSkill.isPresent()) {
            existingUserSkill.get().setYears(years);
        } else {
            UserSkill userSkill = new UserSkill();
            userSkill.setUser(user);
            userSkill.setSkill(skill);
            userSkill.setYears(years);
            user.getSkills().add(userSkill);
        }

        userRepository.save(user);
    }

    private Skill createNewSkill(String description) {
        Skill newSkill = new Skill();
        newSkill.setDescription(description);
        return skillRepository.save(newSkill);
    }

    public void removeSkillFromUser(Long skillId) {
        String username = SecurityUtility.getSessionUser();
        AppUser user = userRepository.findByUsernameIgnoreCase(username);
        user.getSkills().removeIf(userSkill -> userSkill.getSkill().getId().equals(skillId));
        userRepository.save(user);
    }

    private SkillDto mapToDto(Skill skill) {
        SkillDto dto = new SkillDto();
        dto.setId(skill.getId());
        dto.setDescription(skill.getDescription());
        return dto;
    }
}
