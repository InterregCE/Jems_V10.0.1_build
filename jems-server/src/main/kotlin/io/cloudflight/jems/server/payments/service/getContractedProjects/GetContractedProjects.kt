package io.cloudflight.jems.server.payments.service.getContractedProjects

import io.cloudflight.jems.api.project.dto.ProjectSearchRequestDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.server.payments.authorization.CanRetrieveAdvancePayments
import io.cloudflight.jems.server.project.controller.toModel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class GetContractedProjects(private val projectPersistence: ProjectPersistence): GetContractedProjectsInteractor {

    @CanRetrieveAdvancePayments
    override fun getContractedProjects(searchId: String): Page<ProjectSummary> =
         projectPersistence.getProjects(
            Pageable.ofSize(30),
            ProjectSearchRequestDTO(id = searchId, statuses = setOf(ApplicationStatusDTO.CONTRACTED, ApplicationStatusDTO.IN_MODIFICATION)).toModel()
         )

}
