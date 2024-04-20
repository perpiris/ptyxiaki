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
import org.iperp.Utilities.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

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
    public PostDto get(final Long postId) {
        return postRepository.findById(postId)
                .map(post -> mapToDto(post, new PostDto()))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Long create(final PostDto postDto) {
        final Post post = new Post();
        mapToEntity(postDto, post);

        String username = SecurityUtility.getSessionUser();
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username);
        post.setCreatedBy(user);

        return postRepository.save(post).getId();
    }

    @Override
    public void edit(final Long postId, final PostDto postDto) {
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with ID: " + postId));

        if (!isOwner(postId)) {
            throw new UnauthorizedException("Only the creator can edit this post");
        }

        mapToEntity(postDto, post);
        postRepository.save(post);
    }

    @Override
    public void delete(final Long postId) {
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with ID: " + postId));

        if (!isOwner(postId)) {
            throw new UnauthorizedException("Only the creator can delete this post");
        }

        postRepository.delete(post);
    }

    public boolean isOwner(Long postId) {
        String username = SecurityUtility.getSessionUser();
        Optional<Post> postOptional = postRepository.findByIdAndCreatedByUsername(postId, username);
        return postOptional.isPresent();
    }

    private PostDto mapToDto(final Post post, final PostDto postDto) {
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setJobType(post.getJobType());
        postDto.setJobLocation(post.getJobLocation());
        postDto.setCreatedOn(calculateRelativeTime(post.getCreatedOn()));
        return postDto;
    }

    private void mapToEntity(final PostDto postDto, final Post post) {
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setJobType(postDto.getJobType());
        post.setJobLocation(postDto.getJobLocation());
    }

    private String calculateRelativeTime(LocalDateTime createdDate) {
        LocalDateTime now = LocalDateTime.now();
        long seconds = now.atZone(ZoneId.systemDefault()).toEpochSecond() - createdDate.atZone(ZoneId.systemDefault()).toEpochSecond();
        long days = seconds / (60 * 60 * 24);
        long hours = seconds / (60 * 60);
        long minutes = seconds / 60;
        if (days > 0) {
            return days + (days == 1 ? " day ago" : " days ago");
        } else if (hours > 0) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        } else if (minutes > 0) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        } else {
            return seconds + (seconds == 1 ? " second ago" : " seconds ago");
        }
    }
}
