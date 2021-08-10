package io.cloudflight.jems.server.project.service.partner.get_project_partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface GetProjectPartnerInteractor {

    fun findAllByProjectId(projectId: Long, page: Pageable, version: String? = null): Page<ProjectPartnerDTO>

    fun findAllByProjectId(projectId: Long): Iterable<ProjectPartnerDetailDTO>

    fun getById(partnerId: Long, version: String? = null): ProjectPartnerDetailDTO

    fun findAllByProjectIdForDropdown(projectId: Long, sort: Sort, version: String? = null): List<ProjectPartnerDTO>

}
