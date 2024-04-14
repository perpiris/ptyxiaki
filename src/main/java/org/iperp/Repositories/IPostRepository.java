package org.iperp.Repositories;

import org.iperp.Entities.AppUser;
import org.iperp.Entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPostRepository extends JpaRepository<Post, Long> {

}