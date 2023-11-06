package io.cloudflight.jems.server.project.repository.auditAndControl.correction.financialDescription

import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionFinancialDescriptionEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription

fun ProjectCorrectionFinancialDescriptionEntity.toDescriptionModel() = ProjectCorrectionFinancialDescription(
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

fun ProjectCorrectionFinancialDescription.toEntity(
    correctionEntity: ProjectAuditControlCorrectionEntity,
) = ProjectCorrectionFinancialDescriptionEntity(
    correctionId = correctionEntity.id,
    correction = correctionEntity,
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
