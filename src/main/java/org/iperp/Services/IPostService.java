package org.iperp.Services;

import org.iperp.Dtos.PostDto;
import org.iperp.Enums.JobLocation;
import org.iperp.Enums.JobType;
import org.iperp.Utilities.UnauthorizedException;
import org.springframework.data.domain.Page;

public interface IPostService {
    Page<PostDto> findAll(int pageNumber, int pageSize, String sortBy);

    Page<PostDto> findByJobType(JobType jobType, int pageNumber, int pageSize, String sortBy);

    Page<PostDto> findByJobLocation(JobLocation jobLocation, int pageNumber, int pageSize, String sortBy);

    Page<PostDto> findByJobTypeAndJobLocation(JobType jobType, JobLocation jobLocation, int pageNumber, int pageSize, String sortBy);

    Page<PostDto> findAllForManager(int pageNumber, int pageSize, String sortBy);

    PostDto get(final Long postId);

    Long create(PostDto postDto);

    void edit(final Long postId, final PostDto postDto);

    void toggleAcceptingApplications(Long postId);

    void toggleArchive(Long postId);

    boolean isOwner(Long postId);
}
