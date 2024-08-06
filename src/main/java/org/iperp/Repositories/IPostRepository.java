package org.iperp.Repositories;

import org.iperp.Entities.AppUser;
import org.iperp.Entities.Post;
import org.iperp.Enums.JobLocation;
import org.iperp.Enums.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IPostRepository extends JpaRepository<Post, Long> {

    @Override
    @Query("SELECT p FROM Post p WHERE p.archived = false")
    List<Post> findAll();

    @Override
    @Query("SELECT p FROM Post p WHERE p.archived = false")
    Page<Post> findAll(Pageable pageable);

    Optional<Post> findByIdAndCreatedByUsername(Long postId, String username);

    Page<Post> findAllByCreatedBy(AppUser user, Pageable pageable);

    Page<Post> findByJobTypeAndArchivedFalse(JobType jobType, Pageable pageable);

    Page<Post> findByJobLocationAndArchivedFalse(JobLocation jobLocation, Pageable pageable);

    Page<Post> findByJobTypeAndJobLocationAndArchivedFalse(JobType jobType, JobLocation jobLocation, Pageable pageable);

    Page<Post> findBySkillsSkillIdInAndArchivedFalse(Set<Long> skillIds, Pageable pageable);
    
    Page<Post> findByJobTypeAndSkillsSkillIdInAndArchivedFalse(JobType jobType, Set<Long> skillIds, Pageable pageable);
    
    Page<Post> findByJobLocationAndSkillsSkillIdInAndArchivedFalse(JobLocation jobLocation, Set<Long> skillIds, Pageable pageable);
    
    Page<Post> findByJobTypeAndJobLocationAndSkillsSkillIdInAndArchivedFalse(JobType jobType, JobLocation jobLocation, Set<Long> skillIds, Pageable pageable);
}