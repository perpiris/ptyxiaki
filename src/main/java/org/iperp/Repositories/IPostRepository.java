package org.iperp.Repositories;

import org.iperp.Entities.AppUser;
import org.iperp.Entities.Post;
import org.iperp.Enums.JobLocation;
import org.iperp.Enums.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByCreatedBy(AppUser user, Pageable pageable);

    Page<Post> findByJobType(JobType jobType, Pageable pageable);

    Page<Post> findByJobLocation(JobLocation jobLocation, Pageable pageable);

    Page<Post> findByJobTypeAndJobLocation(JobType jobType, JobLocation jobLocation, Pageable pageable);
}