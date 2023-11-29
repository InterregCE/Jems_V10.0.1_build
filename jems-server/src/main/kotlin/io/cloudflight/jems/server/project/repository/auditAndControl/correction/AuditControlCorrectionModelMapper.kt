package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.toModel
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionCostItem
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact
import org.springframework.data.domain.Page

fun AuditControlCorrectionEntity.toModel() = AuditControlCorrectionDetail(
    id = id,
    orderNr = orderNr,
    status = status,
    type = correctionType,
    auditControlId = auditControl.id,
    auditControlNr = auditControl.number,
    followUpOfCorrectionId = followUpOfCorrection?.id,
    correctionFollowUpType = followUpOfCorrectionType,
    repaymentFrom = repaymentDate,
    lateRepaymentTo = lateRepayment,
    partnerId = partnerReport?.partnerId,
    partnerReportId = partnerReport?.id,
    programmeFundId = programmeFund?.id,
    impact = AuditControlCorrectionImpact(
        action = impact,
        comment = impactComment,
    ),
    costCategory =  costCategory,
    expenditureCostItem = expenditure?.toCorrectionCostItem(),
    procurementId = procurementId,
)

fun AuditControlCorrectionEntity.toSimpleModel() = AuditControlCorrection(
    id = id,
    orderNr = orderNr,
    status = status,
    type = correctionType,
    auditControlId = auditControl.id,
    auditControlNr = auditControl.number,
)

fun Page<AuditControlCorrection>.toLineModel() =
    map { it.toLineModel() }
fun AuditControlCorrection.toLineModel() = AuditControlCorrectionLine(
    id = id,
    orderNr = orderNr,
    status = status,
    type = type,
    auditControlId = auditControlId,
    auditControlNr = auditControlNr,
    canBeDeleted = !status.isClosed(),
)


fun AuditControlCorrectionCreate.toEntity(auditControlEntity: AuditControlEntity) = AuditControlCorrectionEntity(
    auditControl = auditControlEntity,
    orderNr = orderNr,
    status = status,
    correctionType = type,
    followUpOfCorrection = null,
    followUpOfCorrectionType = followUpOfCorrectionType,
    repaymentDate = null,
    lateRepayment = null,
    partnerReport = null,
    programmeFund = null,
    impact = defaultImpact,
    impactComment = "",
    expenditure = null,
    costCategory = null,
    procurementId = null,
    projectModificationId = null,
)

fun Page<PartnerReportExpenditureCostEntity>.toPagedModel() = map { it.toCorrectionCostItem() }

fun PartnerReportExpenditureCostEntity.toCorrectionCostItem() = CorrectionCostItem(
    id = id,
    number = number,
    partnerReportNumber = partnerReport.number,
    lumpSum = reportLumpSum?.toModel(),
    unitCost = reportUnitCost?.toModel(),
    costCategory = costCategory,
    investmentId = reportInvestment?.id,
    investmentNumber = reportInvestment?.investmentNumber,
    investmentWorkPackageNumber = reportInvestment?.workPackageNumber,
    contractId = procurementId,
    internalReferenceNumber = internalReferenceNumber,
    invoiceNumber = invoiceNumber,
    invoiceDate = invoiceDate,
    declaredAmount = declaredAmount,
    currencyCode = currencyCode,
    declaredAmountAfterSubmission = declaredAmountAfterSubmission,
    comment = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.comment) },
    description = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.description) }
)

