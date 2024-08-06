package org.iperp.Services.Implementation;

import org.iperp.Dtos.SkillDto;
import org.iperp.Entities.AppUser;
import org.iperp.Entities.Skill;
import org.iperp.Entities.UserSkill;
import org.iperp.Repositories.IAppUserRepository;
import org.iperp.Repositories.ISkillRepository;
import org.iperp.Repositories.IUserSkillRepository;
import org.iperp.Security.SecurityUtility;
import org.iperp.Services.ISkillService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService implements ISkillService {

    private final ISkillRepository skillRepository;
    private final IAppUserRepository userRepository;
    private final IUserSkillRepository userSkillRepository;

    public SkillService(ISkillRepository skillRepository, IAppUserRepository userRepository, IUserSkillRepository userSkillRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
    }

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

    public void addSkillToUser(String skillDescription, int years) throws Exception {
        if (skillDescription == null || skillDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill description cannot be null or empty");
        }

        String username = SecurityUtility.getSessionUser();
        AppUser user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null) {
            throw new Exception("User not found");
        }

        if (user.getSkills() == null) {
            user.setSkills(new ArrayList<>());
        }

        boolean userHasSkill = user.getSkills().stream()
                .anyMatch(us -> us.getSkill().getDescription().equalsIgnoreCase(skillDescription.trim().toUpperCase()));

        if (userHasSkill) {
            throw new Exception("User already has this skill");
        }

        Skill skill = getOrCreateSkill(skillDescription);
        UserSkill userSkill = new UserSkill();
        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkill.setYears(years);

        user.getSkills().add(userSkill);

        userRepository.save(user);
    }

    private Skill getOrCreateSkill(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill description cannot be null or empty");
        }

        return skillRepository.findByDescriptionIgnoreCase(description.trim().toUpperCase())
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setDescription(description.trim().toUpperCase());
                    return skillRepository.save(newSkill);
                });
    }

    public void updateUserSkillYears(Long userSkillId, int years) throws Exception {
        String username = SecurityUtility.getSessionUser();
        AppUser user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null) {
            throw new Exception("User not found");
        }

        UserSkill userSkill = userSkillRepository.findById(userSkillId)
                .orElseThrow(() -> new Exception("UserSkill not found"));

        if (!userSkill.getUser().getId().equals(user.getId())) {
            throw new Exception("Unauthorized: This skill does not belong to the current user");
        }

        userSkill.setYears(years);
        userSkillRepository.save(userSkill);
    }

    public void removeSkillFromUser(Long userSkillId) throws Exception {
        String username = SecurityUtility.getSessionUser();
        AppUser user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null) {
            throw new Exception("User not found");
        }

        boolean skillBelongsToUser = user.getSkills().stream()
                .anyMatch(skill -> skill.getId().equals(userSkillId));
        if (!skillBelongsToUser) {
            throw new Exception("Unauthorized: This skill does not belong to the current user");
        }

        List<UserSkill> updatedSkills = user.getSkills().stream()
                .filter(skill -> !skill.getId().equals(userSkillId))
                .toList();

        user.getSkills().clear();
        user.getSkills().addAll(updatedSkills);
        userRepository.save(user);
    }

    private SkillDto mapToDto(Skill skill) {
        SkillDto dto = new SkillDto();
        dto.setId(skill.getId());
        dto.setDescription(skill.getDescription());
        return dto;
    }
}
