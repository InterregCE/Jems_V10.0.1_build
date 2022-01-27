package io.cloudflight.jems.server.project.service.partner.get_project_partner_reporting

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.data.domain.Sort

interface GetProjectPartnerReportingInteractor {
    fun findAllByProjectIdForReporting(projectId: Long, sort: Sort, version: String? = null): List<ProjectPartnerSummary>
}
