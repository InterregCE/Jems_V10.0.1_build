package io.cloudflight.jems.server.project.controller.partner.report

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerReportDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.partner.ProjectPartnerReportApi
import io.cloudflight.jems.server.project.controller.partner.toDto
import io.cloudflight.jems.server.project.service.partner.get_project_partner_reporting.GetProjectPartnerReportingInteractor
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportController(private val getProjectPartnerReporting: GetProjectPartnerReportingInteractor)
    : ProjectPartnerReportApi {

    override fun getProjectPartnerReports(
        partnerId: Long, version: String?
    ): List<ProjectPartnerReportDTO> {
        val partnerReport1 = ProjectPartnerReportDTO(1, "R1" + partnerId, "Draft", version.toString())
        val partnerReport2 = ProjectPartnerReportDTO(2, "R2" + partnerId, "Draft", version.toString())
        val partnerReport3 = ProjectPartnerReportDTO(3, "R3" + partnerId, "Draft", version.toString())
        return mutableListOf(partnerReport1, partnerReport2, partnerReport3)
    }

    override fun getProjectPartnersForReporting(projectId: Long, sort: Sort, version: String?): List<ProjectPartnerSummaryDTO> =
        getProjectPartnerReporting.findAllByProjectIdForReporting(projectId, sort, version).toDto()

}
