package io.cloudflight.jems.server.project.controller.report

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.report.ProjectReportApi
import io.cloudflight.jems.server.project.controller.partner.toDto
import io.cloudflight.jems.server.project.service.report.getProjectReportPartnerList.GetProjectReportPartnerListInteractor
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportController(
    private val getPartnerList: GetProjectReportPartnerListInteractor,
) : ProjectReportApi {

    override fun getProjectPartnersForReporting(
        projectId: Long,
        sort: Sort,
        version: String?
    ): List<ProjectPartnerSummaryDTO> =
        getPartnerList.findAllByProjectId(projectId, sort, version).toDto()

}
