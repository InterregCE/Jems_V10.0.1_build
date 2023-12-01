package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionMeasureEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.toModel
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionCostItem
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact
import org.springframework.data.domain.Page
import java.math.BigDecimal

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
    costCategory = costCategory,
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
    number = originalNumber ?: number,
    partnerReportNumber = reportOfOrigin?.number ?: partnerReport.number,
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

fun AuditControlCorrectionEntity.toAuditControlCorrectionLine(
    finance: AuditControlCorrectionFinanceEntity,
    measure: AuditControlCorrectionMeasureEntity,
    partner: ProjectPartnerEntity?,
    total: BigDecimal?,
): AuditControlCorrectionLine =
    AuditControlCorrectionLine(
        id = id,
        orderNr = orderNr,
        status = status,
        type = correctionType,
        auditControlId = auditControl.id,
        auditControlNr = auditControl.number,
        canBeDeleted = !status.isClosed(),
        partnerRole = partnerReport?.identification?.partnerRole,
        partnerNumber = partnerReport?.identification?.partnerNumber,
        partnerDisabled = partner?.active?.not(),
        partnerReport = partnerReport?.number,
        followUpAuditNr = followUpOfCorrection?.auditControl?.number,
        followUpCorrectionNr = followUpOfCorrection?.orderNr,
        fundType = programmeFund?.type,
        fundAmount = finance.fundAmount.negateIf(finance.deduction),
        publicContribution = finance.publicContribution.negateIf(finance.deduction),
        autoPublicContribution = finance.autoPublicContribution.negateIf(finance.deduction),
        privateContribution = finance.privateContribution.negateIf(finance.deduction),
        total = total ?: BigDecimal.ZERO,
        impactProjectLevel = impact,
        scenario = measure.scenario,
    )

private fun BigDecimal.negateIf(deduction: Boolean): BigDecimal =
    if (deduction) negate() else this
