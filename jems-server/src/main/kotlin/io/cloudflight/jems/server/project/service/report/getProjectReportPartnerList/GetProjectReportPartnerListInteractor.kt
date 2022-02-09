package io.cloudflight.jems.server.project.service.report.getProjectReportPartnerList

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.data.domain.Sort

interface GetProjectReportPartnerListInteractor {

    fun findAllByProjectId(
        projectId: Long,
        sort: Sort,
        version: String? = null
    ): List<ProjectPartnerSummary>

}
