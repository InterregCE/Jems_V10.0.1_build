package io.cloudflight.jems.server.project.controller.report

import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportSummaryDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi
import io.cloudflight.jems.server.project.service.report.partner.createProjectPartnerReport.CreateProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.getProjectPartnerReport.GetProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.submitProjectPartnerReport.SubmitProjectPartnerReportInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportController(
    private val createPartnerReport: CreateProjectPartnerReportInteractor,
    private val submitPartnerReport: SubmitProjectPartnerReportInteractor,
    private val getPartnerReport: GetProjectPartnerReportInteractor,
) : ProjectPartnerReportApi {

    override fun getProjectPartnerReports(
        partnerId: Long,
        pageable: Pageable,
    ): Page<ProjectPartnerReportSummaryDTO> =
        getPartnerReport.findAll(partnerId = partnerId, pageable = pageable).toDto()

    override fun getProjectPartnerReport(partnerId: Long, reportId: Long) =
        getPartnerReport.findById(partnerId = partnerId, reportId = reportId).toDto()

    override fun createProjectPartnerReport(partnerId: Long) =
        createPartnerReport.createReportFor(partnerId = partnerId).toDto()

    override fun submitProjectPartnerReport(partnerId: Long, reportId: Long): ProjectPartnerReportSummaryDTO =
        submitPartnerReport.submit(partnerId = partnerId, reportId = reportId).toDto()

}
