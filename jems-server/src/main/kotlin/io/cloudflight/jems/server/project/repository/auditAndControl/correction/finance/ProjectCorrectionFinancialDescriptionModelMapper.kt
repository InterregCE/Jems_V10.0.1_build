package io.cloudflight.jems.server.project.repository.auditAndControl.correction.finance

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.finance.AuditControlCorrectionFinance

fun AuditControlCorrectionFinanceEntity.toDescriptionModel() = AuditControlCorrectionFinance(
    correctionId = correction.id,
    deduction = deduction,
    fundAmount = fundAmount,
    autoPublicContribution = autoPublicContribution,
    privateContribution = privateContribution,
    publicContribution = publicContribution,
    infoSentBeneficiaryDate = infoSentBeneficiaryDate,
    infoSentBeneficiaryComment = infoSentBeneficiaryComment,
    clericalTechnicalMistake = clericalTechnicalMistake,
    correctionComment = correctionComment,
    correctionType = correctionType,
    goldPlating = goldPlating,
    suspectedFraud = suspectedFraud
)
