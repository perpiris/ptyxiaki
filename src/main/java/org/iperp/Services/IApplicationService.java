package org.iperp.Services;

import org.iperp.Dtos.ApplicationDto;
import org.springframework.data.domain.Page;

public interface IApplicationService {
    Page<ApplicationDto> findAllForApplicant(int pageNumber, int pageSize, String sortBy);

    void applyToPost(Long postId);

    void cancelApplication(Long applicationId);
}
