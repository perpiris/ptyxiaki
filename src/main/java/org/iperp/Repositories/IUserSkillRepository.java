package org.iperp.Repositories;

import org.iperp.Entities.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserSkillRepository extends JpaRepository<UserSkill, Long> {
    
}
