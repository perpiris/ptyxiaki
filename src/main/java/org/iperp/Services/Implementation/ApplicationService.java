package org.iperp.Services.Implementation;

import org.iperp.Dtos.UserApplicationDto;
import org.iperp.Entities.AppUser;
import org.iperp.Entities.Application;
import org.iperp.Entities.Post;
import org.iperp.Enums.ApplicationStatus;
import org.iperp.Repositories.IAppUserRepository;
import org.iperp.Repositories.IApplicationRepository;
import org.iperp.Repositories.IPostRepository;
import org.iperp.Security.SecurityUtility;
import org.iperp.Services.IApplicationService;
import org.iperp.Utilities.NotFoundException;
import org.iperp.Utilities.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService implements IApplicationService {

    private final IPostRepository postRepository;
    private final IAppUserRepository appUserRepository;
    private final IApplicationRepository applicationRepository;

    public ApplicationService(IPostRepository postRepository, IAppUserRepository appUserRepository, IApplicationRepository applicationRepository) {
        this.postRepository = postRepository;
        this.appUserRepository = appUserRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public Page<UserApplicationDto> findAllForApplicant(int pageNumber, int pageSize, String sortBy) {
        String username = SecurityUtility.getSessionUser();
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        Page<Application> applications = applicationRepository.findAllByUser(user, pageable);
        return applications.map(application -> mapToDto(application, new UserApplicationDto()));
    }

    public void applyToPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(NotFoundException::new);
        String username = SecurityUtility.getSessionUser();
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username);

        if (post.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalArgumentException("The user who created the post cannot apply to it.");
        }

        if (post.isArchived() || !post.isAcceptingApplications()) {
            throw new IllegalArgumentException("This post no longer accepts applications.");
        }

        boolean hasApplied = post.getApplications().stream().anyMatch(application -> 
                application.getUser().getId().equals(user.getId()) && 
                        !application.getStatus().equals(ApplicationStatus.WITHDRAWN));
        if (hasApplied) {
            throw new IllegalArgumentException("You have already applied to this post.");
        }

        Application application = Application.builder().user(user).post(post).build();
        application.setStatus(ApplicationStatus.PENDING);
        applicationRepository.save(application);
    }

    @Override
    public boolean hasUserAppliedToPost(Long postId) {
        String username = SecurityUtility.getSessionUser();
        var application = applicationRepository.findByUserUsernameAndPostIdAndStatusNot(username, postId, ApplicationStatus.WITHDRAWN);
        if (application == null) {
            return false;
        }
        
        return !application.getStatus().equals(ApplicationStatus.WITHDRAWN);
    }

    @Override
    public void cancelApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new NotFoundException("Application not found with ID: " + applicationId));

        String username = SecurityUtility.getSessionUser();
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username);

        if (!application.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to cancel this application");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(application);
    }

    @Override
    public void updateApplicationStatus(Long applicationId, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found with ID: " + applicationId));

        String username = SecurityUtility.getSessionUser();
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username);

        if (!application.getPost().getCreatedBy().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to update this application's status");
        }

        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new IllegalStateException("Cannot update status of a withdrawn application");
        }

        application.setStatus(newStatus);
        applicationRepository.save(application);
    }

    private UserApplicationDto mapToDto(final Application application, final UserApplicationDto userApplicationDto) {
        userApplicationDto.setId(application.getId());
        userApplicationDto.setPostTitle(application.getPost().getTitle());
        userApplicationDto.setStatus(application.getStatus().getDisplayName());
        return userApplicationDto;
    }
}
