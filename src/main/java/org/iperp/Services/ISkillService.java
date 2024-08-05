package org.iperp.Services;

import org.iperp.Dtos.SkillDto;
import org.iperp.Entities.UserSkill;

import java.util.List;

public interface ISkillService {

    List<SkillDto> getAllSkills();

    List<UserSkill> getUserSkills();

    void addSkillToUser(String skillDescription, int years) throws Exception;

    void updateUserSkillYears(Long userSkillId, int years) throws Exception;

    void removeSkillFromUser(Long userSkillId) throws Exception;
}
