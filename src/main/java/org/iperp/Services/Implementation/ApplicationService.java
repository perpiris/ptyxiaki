package org.iperp.Services.Implementation;

import org.iperp.Dtos.ApplicationDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService implements IApplicationService {

    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private IAppUserRepository appUserRepository;
    @Autowired
    private IApplicationRepository applicationRepository;

    @Override
    public Page<ApplicationDto> findAllForApplicant(int pageNumber, int pageSize, String sortBy) {
        String username = SecurityUtility.getSessionUser();
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        Page<Application> applications = applicationRepository.findAllByUser(user, pageable);
        return applications.map(application -> mapToDto(application, new ApplicationDto()));
    }

    public void applyToPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(NotFoundException::new);
        String username = SecurityUtility.getSessionUser();
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username);

        if (post.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalArgumentException("The user who created the post cannot apply to it.");
        }

        boolean hasApplied = post.getApplications().stream().anyMatch(application -> application.getUser().getId().equals(user.getId()));
        if (hasApplied) {
            throw new IllegalArgumentException("You have already applied to this post.");
        }

        Application application = Application.builder()
                .user(user)
                .post(post)
                .build();

        application.setStatus(ApplicationStatus.PENDING);

        applicationRepository.save(application);
    }

    private ApplicationDto mapToDto(final Application application, final ApplicationDto applicationDto) {
        applicationDto.setId(application.getId());
        applicationDto.setPostTitle(application.getPost().getTitle());
        applicationDto.setStatus(application.getStatus().getDisplayName());
        return applicationDto;
    }

    private void mapToEntity(final ApplicationDto applicationDto, final Application application) {

    }
}
