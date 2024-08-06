package org.iperp.Repositories;

import org.iperp.Entities.AppUser;
import org.iperp.Entities.Application;
import org.iperp.Enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IApplicationRepository extends JpaRepository<Application, Long> {

    Page<Application> findAllByUser(AppUser user, Pageable pageable);

    Application findByUserUsernameAndPostIdAndStatusNot(String username, Long postId, ApplicationStatus status);

    List<Application> findByPostId(Long postId);
}
