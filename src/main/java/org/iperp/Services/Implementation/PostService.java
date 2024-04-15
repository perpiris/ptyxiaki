package org.iperp.Services.Implementation;

import org.iperp.Dtos.PostDto;
import org.iperp.Entities.AppUser;
import org.iperp.Entities.Post;
import org.iperp.Enums.JobLocation;
import org.iperp.Enums.JobType;
import org.iperp.Repositories.IAppUserRepository;
import org.iperp.Repositories.IPostRepository;
import org.iperp.Security.SecurityUtility;
import org.iperp.Services.IPostService;
import org.iperp.Utilities.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PostService implements IPostService {

    private final IPostRepository postRepository;
    private final IAppUserRepository appUserRepository;

    public PostService(final IPostRepository postRepository,
                       final IAppUserRepository appUserRepository) {
        this.postRepository = postRepository;
        this.appUserRepository = appUserRepository;
    }

    public Page<PostDto> findAll(int pageNumber, int pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(post -> mapToDto(post, new PostDto()));
    }

    public Page<PostDto> findByJobType(JobType jobType, int pageNumber, int pageSize, String sortBy) {
        if (jobType == null) {
            return findAll(pageNumber, pageSize, sortBy);
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        Page<Post> postPage = postRepository.findByJobType(jobType, pageable);
        return postPage.map(post -> mapToDto(post, new PostDto()));
    }

    public Page<PostDto> findByJobLocation(JobLocation jobLocation, int pageNumber, int pageSize, String sortBy) {
        if (jobLocation == null) {
            return findAll(pageNumber, pageSize, sortBy);
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        Page<Post> postPage = postRepository.findByJobLocation(jobLocation, pageable);
        return postPage.map(post -> mapToDto(post, new PostDto()));
    }

    public Page<PostDto> findByJobTypeAndJobLocation(JobType jobType, JobLocation jobLocation, int pageNumber, int pageSize, String sortBy) {
        if (jobType == null && jobLocation == null) {
            return findAll(pageNumber, pageSize, sortBy);
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        Page<Post> postPage = postRepository.findByJobTypeAndJobLocation(jobType, jobLocation, pageable);
        return postPage.map(post -> mapToDto(post, new PostDto()));
    }

    public Page<PostDto> findAllForManager(int pageNumber, int pageSize, String sortBy) {
        String username = SecurityUtility.getSessionUser();
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        Page<Post> postPage = postRepository.findAllByCreatedBy(user, pageable);
        return postPage.map(post -> mapToDto(post, new PostDto()));
    }

    @Override
    public PostDto get(final Long id) {
        return postRepository.findById(id)
                .map(post -> mapToDto(post, new PostDto()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public void create(final PostDto postDto) {
        final Post post = new Post();
        mapToEntity(postDto, post);

        String username = SecurityUtility.getSessionUser();
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username);
        post.setCreatedBy(user);
    }

    @Override
    public void update(final Long id, final PostDto postDto) {
        final Post post = postRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(postDto, post);
        postRepository.save(post);
    }

    @Override
    public void delete(final Long id) {
        postRepository.deleteById(id);
    }

    private PostDto mapToDto(final Post post, final PostDto postDTO) {
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setDescription(post.getDescription());
        postDTO.setJobType(post.getJobType());
        postDTO.setJobLocation(post.getJobLocation());
        return postDTO;
    }

    private void mapToEntity(final PostDto postDTO, final Post post) {
        post.setTitle(postDTO.getTitle());
        post.setDescription(postDTO.getDescription());
        post.setJobType(postDTO.getJobType());
        post.setJobLocation(postDTO.getJobLocation());
    }
}
