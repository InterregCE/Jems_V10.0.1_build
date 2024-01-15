package io.cloudflight.jems.server.project.controller.report.project.verification.overview

import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.CertificateVerificationDeductionOverviewDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.FinancingSourceBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.VerificationWorkOverviewDTO
import io.cloudflight.jems.api.project.report.project.verification.ProjectReportVerificationOverviewApi
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getDeductionOverview.GetProjectReportVerificationDeductionOverviewInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.GetProjectReportFinancingSourceBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview.GetProjectReportVerificationWorkOverviewInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportVerificationOverviewController(
    private val getProjectReportVerificationWorkOverview: GetProjectReportVerificationWorkOverviewInteractor,
    private val getProjectReportFinancingSourceBreakdown: GetProjectReportFinancingSourceBreakdownInteractor,
    private val getProjectReportDeductionOverviewInteractor: GetProjectReportVerificationDeductionOverviewInteractor,
) : ProjectReportVerificationOverviewApi {

    override fun getDeductionBreakdown(projectId: Long, reportId: Long): VerificationWorkOverviewDTO =
        getProjectReportVerificationWorkOverview.get(reportId).toDto()

    override fun getFinancingSourceBreakdown(projectId: Long, reportId: Long): FinancingSourceBreakdownDTO =
        getProjectReportFinancingSourceBreakdown.get(projectId = projectId, reportId = reportId).toDto()


    override fun getDeductionsByTypologyOfErrors(
        projectId: Long,
        reportId: Long
    ): List<CertificateVerificationDeductionOverviewDTO> =
        getProjectReportDeductionOverviewInteractor.getDeductionOverview(reportId).toDto()
}
