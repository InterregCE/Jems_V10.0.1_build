package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerVatRecoveryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectTargetGroupData
import io.cloudflight.jems.plugin.contract.models.report.partner.contribution.ProjectPartnerReportContributionWrapperData
import io.cloudflight.jems.plugin.contract.models.report.partner.expenditure.ProjectPartnerReportExpenditureCostData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureCoFinancingBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureCostCategoryBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureInvestmentBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureLumpSumBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureUnitCostBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProgrammeLegalStatusData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProgrammeLegalStatusTypeData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportIdentificationData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ReportStatusData
import io.cloudflight.jems.plugin.contract.models.report.partner.procurement.ProjectPartnerReportProcurementBeneficialOwnerData
import io.cloudflight.jems.plugin.contract.models.report.partner.procurement.ProjectPartnerReportProcurementData
import io.cloudflight.jems.plugin.contract.models.report.partner.procurement.ProjectPartnerReportProcurementSubcontractData
import io.cloudflight.jems.plugin.contract.models.report.partner.procurement.ProjectReportProcurementFileData
import io.cloudflight.jems.plugin.contract.models.report.partner.workPlan.ProjectPartnerReportWorkPackageData
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContributionData
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackage
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ReportPartnerDataProviderMapper::class.java)

fun ProjectPartnerReport.toDataModel(): ProjectPartnerReportData = ProjectPartnerReportData(
    id = id,
    reportNumber = reportNumber,
    status = ReportStatusData.valueOf(status.name),
    version = version,
    firstSubmission = firstSubmission,

    projectIdentifier = identification.projectIdentifier,
    projectAcronym = identification.projectAcronym,
    partnerNumber = identification.partnerNumber,
    partnerAbbreviation = identification.partnerAbbreviation,
    partnerRole = ProjectPartnerRoleData.valueOf(identification.partnerRole.name),
    nameInOriginalLanguage = identification.nameInOriginalLanguage,
    nameInEnglish = identification.nameInEnglish,
    legalStatus = identification.legalStatus?.toDataModel(),
    partnerType = identification.partnerType?.let { ProjectTargetGroupData.valueOf(it.name) },
    vatRecovery = identification.vatRecovery?.let { ProjectPartnerVatRecoveryData.valueOf(it.name) },
    country = identification.country,
    currency = identification.currency,
    coFinancing = identification.coFinancing.map { mapper.map(it) },
)

private fun ProgrammeLegalStatus.toDataModel() = ProgrammeLegalStatusData(
    id = id,
    type = ProgrammeLegalStatusTypeData.valueOf(type.name),
    description = description.mapTo(HashSet()) { InputTranslationData(SystemLanguageData.valueOf(it.language.name), it.translation)}
)

fun ProjectPartnerReportIdentification.toDataModel() = mapper.map(this)

fun ProjectPartnerReportContributionData.toDataModel() = mapper.map(this)
fun List<ProjectPartnerReportExpenditureCost>.toDataModel() = map { mapper.map(it) }
fun ExpenditureCoFinancingBreakdown.toDataModel() = mapper.map(this)
fun ExpenditureCostCategoryBreakdown.toDataModel() = mapper.map(this)
fun ExpenditureInvestmentBreakdown.toDataModel() = mapper.map(this)
fun ExpenditureLumpSumBreakdown.toDataModel() = mapper.map(this)
fun ExpenditureUnitCostBreakdown.toDataModel() = mapper.map(this)
fun ProjectPartnerReportProcurement.toDataModel() = mapper.map(this)
fun List<ProjectPartnerReportProcurement>.toDataModelProcurement() = map { it.toDataModel() }
fun List<ProjectReportProcurementFile>.toDataModelFile() = map { mapper.map(it) }
fun List<ProjectPartnerReportProcurementBeneficialOwner>.toDataModelBeneficial() = map { mapper.map(it) }
fun List<ProjectPartnerReportProcurementSubcontract>.toDataModelSubcontract() = map { mapper.map(it) }
fun List<ProjectPartnerReportWorkPackage>.toDataModelWorkPlan() = map { mapper.map(it) }

@Mapper
interface ReportPartnerDataProviderMapper {
    fun map(model: ProjectPartnerReportIdentification): ProjectPartnerReportIdentificationData
    fun map(model: ProjectPartnerCoFinancing): ProjectPartnerCoFinancingData
    fun map(model: ProjectPartnerReportContributionData): ProjectPartnerReportContributionWrapperData
    fun map(model: ProjectPartnerReportExpenditureCost): ProjectPartnerReportExpenditureCostData
    fun map(model: ExpenditureCoFinancingBreakdown): ExpenditureCoFinancingBreakdownData
    fun map(model: ExpenditureCostCategoryBreakdown): ExpenditureCostCategoryBreakdownData
    fun map(model: ExpenditureInvestmentBreakdown): ExpenditureInvestmentBreakdownData
    fun map(model: ExpenditureLumpSumBreakdown): ExpenditureLumpSumBreakdownData
    fun map(model: ExpenditureUnitCostBreakdown): ExpenditureUnitCostBreakdownData
    fun map(model: ProjectPartnerReportProcurement): ProjectPartnerReportProcurementData
    fun map(model: ProjectReportProcurementFile): ProjectReportProcurementFileData
    fun map(model: ProjectPartnerReportProcurementBeneficialOwner): ProjectPartnerReportProcurementBeneficialOwnerData
    fun map(model: ProjectPartnerReportProcurementSubcontract): ProjectPartnerReportProcurementSubcontractData
    fun map(model: ProjectPartnerReportWorkPackage): ProjectPartnerReportWorkPackageData
}
