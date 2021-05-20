package io.cloudflight.jems.server.project.service.partner.get_project_partner

import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProject
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

    @CanRetrieveProject
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerException::class)
    override fun findAllByProjectId(projectId: Long, page: Pageable, version: String?): Page<OutputProjectPartner> =
        persistence.findAllByProjectId(projectId, page, version)

    @CanRetrieveProjectPartner
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerException::class)
    override fun getById(partnerId: Long, version: String?): OutputProjectPartnerDetail =
        persistence.getById(partnerId, version)

    @CanRetrieveProject
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerException::class)
    override fun findAllByProjectIdForDropdown(projectId: Long, sort: Sort, version: String?): List<OutputProjectPartner> =
        persistence.findAllByProjectIdForDropdown(projectId, sort, version)

    override fun findAllByProjectId(projectId: Long): Iterable<OutputProjectPartnerDetail> =
        persistence.findAllByProjectId(projectId)

    // used for authorization
    override fun getProjectIdForPartnerId(id: Long): Long =
        persistence.getProjectIdForPartnerId(id)
}