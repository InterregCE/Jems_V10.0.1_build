package io.cloudflight.jems.server.project.service.partner.get_project_partner

import io.cloudflight.jems.server.project.service.partner.model.ProjectBudgetPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerPaymentSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

interface GetProjectPartnerInteractor {

    fun findAllByProjectId(projectId: Long, page: Pageable, version: String? = null): Page<ProjectBudgetPartnerSummary>

    fun getById(partnerId: Long, version: String? = null): ProjectPartnerDetail

    fun findAllByProjectIdForDropdown(projectId: Long, sort: Sort, version: String? = null): List<ProjectPartnerSummary>

    fun findAllByProjectIdWithContributionsForDropdown(projectId: Long, version: String?): List<ProjectPartnerPaymentSummary>
}
