package io.cloudflight.jems.server.project.service.partner.get_project_partner

import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface GetProjectPartnerInteractor {

    fun findAllByProjectId(projectId: Long, page: Pageable, version: String? = null): Page<OutputProjectPartner>

    fun findAllByProjectId(projectId: Long): Iterable<OutputProjectPartnerDetail>

    fun getById(partnerId: Long, version: String? = null): OutputProjectPartnerDetail

    fun findAllByProjectIdForDropdown(projectId: Long, sort: Sort, version: String? = null): List<OutputProjectPartner>

}
