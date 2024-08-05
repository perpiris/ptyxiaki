package org.iperp.Services.Implementation;

import org.iperp.Dtos.*;
import org.iperp.Entities.*;
import org.iperp.Enums.JobLocation;
import org.iperp.Enums.JobType;
import org.iperp.Repositories.IAppUserRepository;
import org.iperp.Repositories.IApplicationRepository;
import org.iperp.Repositories.IPostRepository;
import org.iperp.Repositories.ISkillRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService implements IPostService {

    private final IPostRepository postRepository;
    private final IAppUserRepository appUserRepository;
    private final ISkillRepository skillRepository;
    private final IApplicationRepository applicationRepository;

    public PostService(final IPostRepository postRepository,
                       final IAppUserRepository appUserRepository,
                       final ISkillRepository skillRepository, IApplicationRepository applicationRepository) {
        this.postRepository = postRepository;
        this.appUserRepository = appUserRepository;
        this.skillRepository = skillRepository;
        this.applicationRepository = applicationRepository;
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
        post.setAcceptingApplications(true);

        post.setSkills(new ArrayList<>());

        postDto.getSkills().forEach(skillDto -> {
            Skill skill = getOrCreateSkill(skillDto.getDescription().toUpperCase());
            PostSkill postSkill = PostSkill.builder()
                    .post(post)
                    .skill(skill)
                    .years(skillDto.getYears())
                    .build();
            post.getSkills().add(postSkill);
        });

        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }

    @Override
    public void edit(final Long postId, final PostDto postDto) {
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with ID: " + postId));

        if (!isOwner(postId)) {
            throw new UnauthorizedException("Only the creator can edit this post");
        }

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setJobType(postDto.getJobType());
        post.setJobLocation(postDto.getJobLocation());

        post.getSkills().clear();

        postDto.getSkills().forEach(skillDto -> {
            Skill skill = getOrCreateSkill(skillDto.getDescription().toUpperCase());
            PostSkill postSkill = PostSkill.builder()
                    .post(post)
                    .skill(skill)
                    .years(skillDto.getYears())
                    .build();
            post.getSkills().add(postSkill);
        });

        postRepository.save(post);
    }

    @Override
    public void toggleAcceptingApplications(Long postId) throws NotFoundException, UnauthorizedException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with ID: " + postId));

        if (!isOwner(postId)) {
            throw new UnauthorizedException("Only the creator can modify this post");
        }

        post.setAcceptingApplications(!post.isAcceptingApplications());
        postRepository.save(post);
    }

    @Override
    public void toggleArchive(Long postId) throws NotFoundException, UnauthorizedException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with ID: " + postId));

        if (!isOwner(postId)) {
            throw new UnauthorizedException("Only the creator can modify this post");
        }

        post.setArchived(!post.isArchived());
        post.setAcceptingApplications(false);
        postRepository.save(post);
    }

    public List<PostApplicationDto> getApplicationsWithSkillsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with ID: " + postId));

        if (!isOwner(postId)) {
            throw new UnauthorizedException("Only the creator can view applications for this post");
        }

        List<Application> applications = applicationRepository.findByPostId(postId);
        return applications.stream()
                .map(application -> mapToApplicationDtoWithSkills(application, post))
                .collect(Collectors.toList());
    }

    public boolean isOwner(Long postId) {
        String username = SecurityUtility.getSessionUser();
        Optional<Post> postOptional = postRepository.findByIdAndCreatedByUsername(postId, username);
        return postOptional.isPresent();
    }

    private Skill getOrCreateSkill(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill description cannot be null or empty");
        }

        return skillRepository.findByDescriptionIgnoreCase(description.trim().toUpperCase())
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setDescription(description.trim().toUpperCase());
                    return skillRepository.save(newSkill);
                });
    }

    private PostDto mapToDto(final Post post, final PostDto postDto) {
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setJobType(post.getJobType());
        postDto.setJobLocation(post.getJobLocation());
        postDto.setCreatedOn(calculateRelativeTime(post.getCreatedOn()));
        postDto.setSkills(post.getSkills().stream()
                .map(this::mapPostSkillToDto)
                .collect(Collectors.toList()));
        postDto.setApplicationCount(post.getApplications().size());
        postDto.setAcceptingApplications(post.isAcceptingApplications());
        postDto.setArchived(post.isArchived());
        return postDto;
    }

    private PostSkillDto mapPostSkillToDto(PostSkill postSkill) {
        PostSkillDto dto = new PostSkillDto();
        dto.setId(postSkill.getSkill().getId());
        dto.setDescription(postSkill.getSkill().getDescription());
        dto.setYears(postSkill.getYears());
        return dto;
    }

    private void mapToEntity(final PostDto postDto, final Post post) {
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setJobType(postDto.getJobType());
        post.setJobLocation(postDto.getJobLocation());
        post.setAcceptingApplications(postDto.isAcceptingApplications());
        post.setArchived(postDto.isArchived());

        if (post.getSkills() == null) {
            post.setSkills(new ArrayList<>());
        } else {
            post.getSkills().clear();
        }

        postDto.getSkills().forEach(skillDto -> {
            PostSkill postSkill = new PostSkill();
            postSkill.setYears(skillDto.getYears());
            post.getSkills().add(postSkill);
        });
    }

    private PostApplicationDto mapToApplicationDtoWithSkills(Application application, Post post) {
        PostApplicationDto dto = new PostApplicationDto();
        dto.setId(application.getId());
        dto.setStatus(application.getStatus());
        dto.setCreatedOn(application.getCreatedOn());

        AppUserDto userDto = new AppUserDto();
        userDto.setId(application.getUser().getId());
        userDto.setUsername(application.getUser().getUsername());
        dto.setUser(userDto);

        List<UserSkillDto> userSkillDtos = application.getUser().getSkills().stream()
                .map(userSkill -> {
                    UserSkillDto skillDto = new UserSkillDto();
                    skillDto.setId(userSkill.getSkill().getId());
                    skillDto.setDescription(userSkill.getSkill().getDescription());
                    skillDto.setYears(userSkill.getYears());

                    boolean isMatched = post.getSkills().stream()
                            .anyMatch(postSkill -> postSkill.getSkill().getId().equals(userSkill.getSkill().getId()));
                    skillDto.setMatched(isMatched);

                    return skillDto;
                })
                .collect(Collectors.toList());
        dto.setSkills(userSkillDtos);

        return dto;
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
