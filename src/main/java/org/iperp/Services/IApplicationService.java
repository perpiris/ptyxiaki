package org.iperp.Services;

import org.iperp.Dtos.UserApplicationDto;
import org.iperp.Enums.ApplicationStatus;
import org.springframework.data.domain.Page;

public interface IApplicationService {
    Page<UserApplicationDto> findAllForApplicant(int pageNumber, int pageSize, String sortBy);

    boolean hasUserAppliedToPost(Long postId);

    void applyToPost(Long postId);

    void cancelApplication(Long applicationId);

    void updateApplicationStatus(Long applicationId, ApplicationStatus newStatus);
}
