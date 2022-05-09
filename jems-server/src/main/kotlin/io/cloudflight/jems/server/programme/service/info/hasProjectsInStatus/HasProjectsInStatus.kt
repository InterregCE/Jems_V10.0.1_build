package io.cloudflight.jems.server.programme.service.info.hasProjectsInStatus

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.project.repository.ProjectStatusHistoryRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HasProjectsInStatus(
    private val projectStatusHistoryRepository: ProjectStatusHistoryRepository
): HasProjectsInStatusInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    override fun programmeHasProjectsInStatus(projectStatus: ApplicationStatusDTO): Boolean =
        projectStatusHistoryRepository.existsByStatus(ApplicationStatus.valueOf(projectStatus.name))

}
