package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectHorizontalPrinciplesData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.BudgetCostsCalculationResultFullData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCoFinancingBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCostCategoryBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCostCategoryBreakdownLinePerPartnerData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateCostCategoryBreakdownPerPartnerData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateInvestmentBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateLumpSumBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.financialOverview.CertificateUnitCostBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportData
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportIdentificationData
import io.cloudflight.jems.plugin.contract.models.report.project.partnerCertificates.PartnerReportCertificateData
import io.cloudflight.jems.plugin.contract.models.report.project.projectResults.ProjectReportResultData
import io.cloudflight.jems.plugin.contract.models.report.project.projectResults.ProjectReportResultPrincipleData
import io.cloudflight.jems.plugin.contract.models.report.project.workPlan.ProjectReportWorkPackageActivityData
import io.cloudflight.jems.plugin.contract.models.report.project.workPlan.ProjectReportWorkPackageData
import io.cloudflight.jems.plugin.contract.models.report.project.workPlan.ProjectReportWorkPackageOutputData
import io.cloudflight.jems.plugin.contract.models.report.project.workPlan.ProjectReportWorkPackageStatusData
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectReportDataProviderMapper::class.java)

fun ProjectReport.toDataModel() = mapper.map(this)
fun ProjectReportIdentification.toDataModel() = mapper.map(this)
fun List<ProjectReportWorkPackage>.toWorkPlanDataModel() = map { mapper.mapWorkPackage(it) }
fun ProjectReportResultPrinciple.toDataModel() = mapper.map(this)
fun List<PartnerReportCertificate>.toCertificateDataModel() = map { mapper.mapCertificate(it) }
fun CertificateCoFinancingBreakdown.toDataModel() = mapper.map(this)
fun CertificateCostCategoryBreakdown.toDataModel() = mapper.map(this)
fun PerPartnerCostCategoryBreakdown.toDataModel() = mapper.map(this)
fun BudgetCostsCalculationResultFull.toDataModel() = mapper.map(this)
fun CertificateInvestmentBreakdown.toDataModel() = mapper.map(this)
fun CertificateLumpSumBreakdown.toDataModel() = mapper.map(this)
fun CertificateUnitCostBreakdown.toDataModel() = mapper.map(this)

@Mapper
interface ProjectReportDataProviderMapper {
    fun map(model: ProjectReport): ProjectReportData
    fun map(model: ProjectReportIdentification): ProjectReportIdentificationData
    fun mapWorkPackage(model: ProjectReportWorkPackage): ProjectReportWorkPackageData
    fun map(model: ProjectReportWorkPlanStatus): ProjectReportWorkPackageStatusData
    fun map(model: ProjectReportWorkPackageActivity): ProjectReportWorkPackageActivityData
    fun map(model: ProjectReportWorkPackageOutput): ProjectReportWorkPackageOutputData
    fun map(model: ProjectReportResultPrinciple): ProjectReportResultPrincipleData
    fun mapCertificate(model: PartnerReportCertificate): PartnerReportCertificateData
    fun map(model: ProjectPartnerRole): ProjectPartnerRoleData
    fun map(model: ProjectReportProjectResult): ProjectReportResultData
    fun map(model: ProjectHorizontalPrinciples): ProjectHorizontalPrinciplesData
    fun map(model: CertificateCoFinancingBreakdown): CertificateCoFinancingBreakdownData
    fun map(model: CertificateCostCategoryBreakdown): CertificateCostCategoryBreakdownData
    fun map(model: PerPartnerCostCategoryBreakdown): CertificateCostCategoryBreakdownPerPartnerData
    fun map(model: PerPartnerCostCategoryBreakdownLine): CertificateCostCategoryBreakdownLinePerPartnerData
    fun map(model: BudgetCostsCalculationResultFull): BudgetCostsCalculationResultFullData
    fun map(model: CertificateInvestmentBreakdown): CertificateInvestmentBreakdownData
    fun map(model: CertificateLumpSumBreakdown): CertificateLumpSumBreakdownData
    fun map(model: CertificateUnitCostBreakdown): CertificateUnitCostBreakdownData
    fun map(model: InputTranslation): InputTranslationData

}
