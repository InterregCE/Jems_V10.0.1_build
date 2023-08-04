package io.cloudflight.jems.server.project.controller.report.project.financialOverview

import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateInvestmentBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateLumpSumBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.PerPartnerCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateUnitCostBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.FinancingSourceBreakdownDTO
import io.cloudflight.jems.api.project.report.project.ProjectReportFinancialOverviewApi
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCertificateInvestmentsBreakdownInteractor.GetReportCertificateInvestmentsBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown.GetReportCertificateCostCategoryBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown.GetReportCertificateCoFinancingBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown.GetReportCertificateLumpSumBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.financialOverview.perPartner.GetPerPartnerCostCategoryBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportUnitCostBreakdown.GetReportCertificateUnitCostsBreakdownInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.GetProjectReportFinancingSourceBreakdownInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportFinancialOverviewController(
    private val getReportCertificateCoFinancingBreakdown: GetReportCertificateCoFinancingBreakdownInteractor,
    private val getReportCertificateLumpSumBreakdown: GetReportCertificateLumpSumBreakdownInteractor,
    private val getReportCertificateCostCategoryBreakdown: GetReportCertificateCostCategoryBreakdownInteractor,
    private val getPerPartnerCostCategoryBreakdown: GetPerPartnerCostCategoryBreakdownInteractor,
    private val getReportCertificateUnitCostBreakdown: GetReportCertificateUnitCostsBreakdownInteractor,
    private val getReportCertificateInvestmentsBreakdown: GetReportCertificateInvestmentsBreakdownInteractor,
    private val getProjectReportFinancingSourceBreakdown: GetProjectReportFinancingSourceBreakdownInteractor,
) : ProjectReportFinancialOverviewApi {

    override fun getCoFinancingBreakdown(projectId: Long, reportId: Long): CertificateCoFinancingBreakdownDTO =
        getReportCertificateCoFinancingBreakdown.get(projectId = projectId, reportId = reportId).toDto()

    override fun getCostCategoriesBreakdown(projectId: Long, reportId: Long): CertificateCostCategoryBreakdownDTO =
        getReportCertificateCostCategoryBreakdown.get(projectId = projectId, reportId = reportId).toDto()

    override fun getLumpSumsBreakdown(projectId: Long, reportId: Long): CertificateLumpSumBreakdownDTO =
        getReportCertificateLumpSumBreakdown.get(projectId = projectId, reportId = reportId).toDto()

    override fun getCostCategoriesPerPartnerBreakdown(projectId: Long, reportId: Long): PerPartnerCostCategoryBreakdownDTO =
        getPerPartnerCostCategoryBreakdown.get(projectId = projectId, reportId = reportId).toDto()

    override fun getUnitCostsBreakdown(projectId: Long, reportId: Long): CertificateUnitCostBreakdownDTO =
        getReportCertificateUnitCostBreakdown.get(projectId = projectId, reportId = reportId).toDto()

    override fun getInvestmentsBreakdown(projectId: Long, reportId: Long): CertificateInvestmentBreakdownDTO =
        getReportCertificateInvestmentsBreakdown.get(projectId = projectId, reportId = reportId).toDto()

    override fun getFinancingSourceBreakdown(projectId: Long, reportId: Long): FinancingSourceBreakdownDTO =
        getProjectReportFinancingSourceBreakdown.get(projectId = projectId, reportId = reportId).toDto()

}
