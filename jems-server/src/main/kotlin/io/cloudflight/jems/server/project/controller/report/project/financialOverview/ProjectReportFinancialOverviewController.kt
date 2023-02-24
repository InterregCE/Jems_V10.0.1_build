package io.cloudflight.jems.server.project.controller.report.project.financialOverview

import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.report.project.ProjectReportFinancialOverviewApi
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown.GetReportCertificateCoFinancingBreakdownInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportFinancialOverviewController(
    private val getReportCertificateCoFinancingBreakdown: GetReportCertificateCoFinancingBreakdownInteractor,
) : ProjectReportFinancialOverviewApi {
    override fun getCoFinancingBreakdown(projectId: Long, reportId: Long): CertificateCoFinancingBreakdownDTO =
        getReportCertificateCoFinancingBreakdown.get(projectId = projectId, reportId = reportId).toDto()

    override fun getCostCategoriesBreakdown(projectId: Long, reportId: Long): CertificateCoFinancingBreakdownDTO {
        TODO("Not yet implemented")
    }
}
