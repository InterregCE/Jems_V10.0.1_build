package io.cloudflight.jems.server.plugin.services.auditAndControl


import io.cloudflight.jems.plugin.contract.models.payments.export.AuditControlCorrectionTypeData
import io.cloudflight.jems.plugin.contract.models.payments.export.AuditControlStatusData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlCorrectionBulkObjectData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlCorrectionDetailData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlCorrectionImpactData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlCorrectionLineData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.AuditControlTypeData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.ControllingBodyData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.CorrectionCostItemData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.CorrectionFollowUpTypeData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.CorrectionImpactActionData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.CorrectionTypeData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.ProjectCorrectionFinancialDescriptionData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.ProjectCorrectionProgrammeMeasureData
import io.cloudflight.jems.plugin.contract.models.project.auditAndControl.ProjectCorrectionProgrammeMeasureScenarioData
import io.cloudflight.jems.plugin.contract.models.project.budget.BudgetCostCategoryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.plugin.contract.models.report.partner.expenditure.ProjectPartnerReportLumpSumData
import io.cloudflight.jems.plugin.contract.models.report.partner.expenditure.ProjectPartnerReportUnitCostData
import io.cloudflight.jems.plugin.contract.models.report.partner.expenditure.ReportBudgetCategoryData
import io.cloudflight.jems.server.plugin.services.payments.toDataModel
import io.cloudflight.jems.server.plugin.services.toDataModel
import io.cloudflight.jems.server.plugin.services.toPluginPage
import io.cloudflight.jems.server.project.entity.auditAndControl.temporaryModels.AuditControlCorrectionBulkTmpObject
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.CorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.finance.AuditControlCorrectionFinance
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionCostItem
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page


fun AuditControl.toDataModel() = AuditControlData(
    id = id,
    number = number,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    status = status.toDataModel(),
    controllingBody = ControllingBodyData.valueOf(controllingBody.name),
    controlType =  AuditControlTypeData.valueOf(controlType.name),
    startDate = startDate,
    endDate = endDate,
    finalReportDate = finalReportDate,
    totalControlledAmount = totalControlledAmount,
    totalCorrectionsAmount = totalCorrectionsAmount,
    existsOngoing = existsOngoing,
    existsClosed = existsClosed,
    comment = comment
)


fun AuditControlCorrectionLine.toModelData() = AuditControlCorrectionLineData(
    id = id,
    orderNr = orderNr,
    status = status.toDataModel(),
    type = type.toDataModel(),
    auditControlId = auditControlId,
    auditControlNr = auditControlNr,
    canBeDeleted = canBeDeleted,
    partnerReport = partnerReport,
    partnerId = partnerId,
    partnerRole = if(partnerRole != null) ProjectPartnerRoleData.valueOf(partnerRole!!.name) else null,
    partnerNumber = partnerNumber,
    partnerDisabled = partnerDisabled,
    lumpSumOrderNr = lumpSumOrderNr,
    followUpAuditNr = followUpAuditNr,
    followUpCorrectionNr = followUpCorrectionNr,
    fund = fund?.toDataModel(),
    fundAmount = fundAmount,
    publicContribution = publicContribution,
    autoPublicContribution = autoPublicContribution,
    privateContribution = privateContribution,
    total = total,
    impactProjectLevel = CorrectionImpactActionData.valueOf(impactProjectLevel.name),
    scenario = scenario.toDataModel()

)


fun AuditControlCorrectionDetail.toDataModel() = AuditControlCorrectionDetailData (
    id = id,
    orderNr = orderNr,
    status = status.toDataModel(),
    type = type.toDataModel(),
    auditControlId = auditControlId,
    auditControlNr = auditControlNr,
    followUpOfCorrectionId = followUpOfCorrectionId,
    correctionFollowUpType =  CorrectionFollowUpTypeData.valueOf(correctionFollowUpType.name),
    repaymentFrom = repaymentFrom,
    lateRepaymentTo = lateRepaymentTo,
    partnerId = partnerId,
    partnerReportId = partnerReportId,
    lumpSumOrderNr = lumpSumOrderNr,
    programmeFundId = programmeFundId,
    impact = impact.toDataModel(),
    costCategory = costCategory?.toDataModel(),
    expenditureCostItem = expenditureCostItem?.toModelData(),
    procurementId = procurementId

)


fun CorrectionCostItem.toModelData() = CorrectionCostItemData(
    id = id,
    number = number,
    partnerReportNumber = partnerReportNumber,
    lumpSum = lumpSum?.toDataModel(),
    unitCost = unitCost?.toDataModel(),
    costCategory = costCategory.toDataModel(),
    investmentId = investmentId,
    investmentNumber = investmentNumber,
    investmentWorkPackageNumber = investmentWorkPackageNumber,
    contractId = contractId,
    internalReferenceNumber = internalReferenceNumber,
    invoiceNumber = invoiceNumber,
    invoiceDate = invoiceDate,
    description = description.toDataModel(),
    comment = comment.toDataModel(),
    declaredAmount = declaredAmount,
    currencyCode = currencyCode,
    declaredAmountAfterSubmission = declaredAmountAfterSubmission

)


fun ProjectPartnerReportLumpSum.toDataModel() = ProjectPartnerReportLumpSumData(
    id = id,
    lumpSumProgrammeId = lumpSumProgrammeId,
    fastTrack = fastTrack,
    orderNr = orderNr,
    period = period,
    cost = cost,
    name = name.toDataModel()

)


fun ProjectPartnerReportUnitCost.toDataModel() =  ProjectPartnerReportUnitCostData(
    id = id,
    unitCostProgrammeId = unitCostProgrammeId,
    projectDefined = projectDefined,
    costPerUnit = costPerUnit,
    numberOfUnits = numberOfUnits,
    total = total,
    costPerUnitForeignCurrency = costPerUnitForeignCurrency,
    foreignCurrencyCode = foreignCurrencyCode,
    name = name.toDataModel(),
    category = category.toDataModel()

)


fun AuditControlCorrectionMeasure.toDataModel() = ProjectCorrectionProgrammeMeasureData(
    correctionId = correctionId,
    scenario = scenario.toDataModel(),
    comment = comment,
    includedInAccountingYear = includedInAccountingYear?.toDataModel()

)

fun AuditControlCorrectionFinance.toDataModel() = ProjectCorrectionFinancialDescriptionData(
    correctionId = correctionId,
    deduction = deduction,
    fundAmount = fundAmount,
    publicContribution = publicContribution,
    autoPublicContribution = autoPublicContribution,
    privateContribution = privateContribution,
    infoSentBeneficiaryDate = infoSentBeneficiaryDate,
    infoSentBeneficiaryComment = infoSentBeneficiaryComment,
    correctionType = correctionType?.toDataModel(),
    clericalTechnicalMistake = clericalTechnicalMistake,
    goldPlating = goldPlating,
    suspectedFraud = suspectedFraud,
    correctionComment = correctionComment
)


fun AuditControlStatus.toDataModel() = AuditControlStatusData.valueOf(this.name)
fun AuditControlCorrectionType.toDataModel() = AuditControlCorrectionTypeData.valueOf(this.name)
fun ReportBudgetCategory.toDataModel() = ReportBudgetCategoryData.valueOf(this.name)
fun ProjectCorrectionProgrammeMeasureScenario.toDataModel() = ProjectCorrectionProgrammeMeasureScenarioData.valueOf(this.name)
fun CorrectionType.toDataModel() = CorrectionTypeData.valueOf(this.name)

fun BudgetCostCategory.toDataModel() = BudgetCostCategoryData.valueOf(this.name)

fun AuditControlCorrectionImpact.toDataModel() = AuditControlCorrectionImpactData(
    action = CorrectionImpactActionData.valueOf(action.name), comment = comment
)
 fun Page<AuditControl>.toModelData() = this.toPluginPage { it.toDataModel() }

fun Page<AuditControlCorrectionLine>.toLineModelData() = this.toPluginPage { it.toModelData() }

private val mapper: AuditAndControlDataProviderMapper = Mappers.getMapper(AuditAndControlDataProviderMapper::class.java)

fun AuditControlCorrectionBulkTmpObject.toDataModel() = mapper.map(this)

@Mapper
interface AuditAndControlDataProviderMapper {
    fun map(model: AuditControlCorrectionBulkTmpObject): AuditControlCorrectionBulkObjectData
}


