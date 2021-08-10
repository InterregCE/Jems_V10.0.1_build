package io.cloudflight.jems.server.project.service.partner.get_project_partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartner
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartner(
    private val persistence: PartnerPersistence,
) : GetProjectPartnerInteractor {

    @CanRetrieveProjectForm
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnersByProjectIdException::class)
    override fun findAllByProjectId(projectId: Long, page: Pageable, version: String?): Page<ProjectPartnerSummaryDTO> =
        persistence.findAllByProjectId(projectId, page, version)

    @CanRetrieveProjectPartner
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerByIdException::class)
    override fun getById(partnerId: Long, version: String?): ProjectPartnerDetailDTO =
        persistence.getById(partnerId, version)

    @CanRetrieveProjectForm
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerByProjectIdForDropdownException::class)
    override fun findAllByProjectIdForDropdown(projectId: Long, sort: Sort, version: String?): List<ProjectPartnerSummaryDTO> =
        persistence.findAllByProjectIdForDropdown(projectId, sort, version)

    override fun findAllByProjectId(projectId: Long): Iterable<ProjectPartnerDetailDTO> =
        persistence.findAllByProjectId(projectId)

}
