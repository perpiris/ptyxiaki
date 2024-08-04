package org.iperp.Repositories;

import org.iperp.Entities.AppUser;
import org.iperp.Entities.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IApplicationRepository extends JpaRepository<Application, Long> {

    Page<Application> findAllByUser(AppUser user, Pageable pageable);

    boolean existsByUserUsernameAndPostId(String username, Long postId);
}
