package org.iperp.Repositories;

import org.iperp.Entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISkillRepository extends JpaRepository<Skill, Long> {
    
    Optional<Skill> findByDescriptionIgnoreCase(String description);
}
