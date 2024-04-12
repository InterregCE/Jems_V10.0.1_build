package io.cloudflight.jems.server.project.repository.auditAndControl.correction.finance

import io.cloudflight.jems.server.project.service.auditAndControl.correction.finance.AuditControlCorrectionFinancePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.finance.AuditControlCorrectionFinance
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.finance.AuditControlCorrectionFinanceUpdate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class AuditControlCorrectionFinancePersistenceProvider(
    private val projectCorrectionFinancialDescriptionRepository: ProjectCorrectionFinancialDescriptionRepository
): AuditControlCorrectionFinancePersistence {

    @Transactional(readOnly = true)
    override fun getCorrectionFinancialDescription(correctionId: Long): AuditControlCorrectionFinance =
        projectCorrectionFinancialDescriptionRepository.getReferenceById(correctionId).toDescriptionModel()

    @Transactional
    override fun updateCorrectionFinancialDescription(
        correctionId: Long,
        financialDescription: AuditControlCorrectionFinanceUpdate
    ): AuditControlCorrectionFinance {
        return projectCorrectionFinancialDescriptionRepository.getReferenceById(correctionId).apply {
            deduction = financialDescription.deduction
            fundAmount = financialDescription.fundAmount
            publicContribution = financialDescription.publicContribution
            autoPublicContribution = financialDescription.autoPublicContribution
            privateContribution = financialDescription.privateContribution
            infoSentBeneficiaryDate = financialDescription.infoSentBeneficiaryDate
            infoSentBeneficiaryComment = financialDescription.infoSentBeneficiaryComment
            correctionType = financialDescription.correctionType
            clericalTechnicalMistake = financialDescription.clericalTechnicalMistake
            goldPlating = financialDescription.goldPlating
            suspectedFraud = financialDescription.suspectedFraud
            correctionComment = financialDescription.correctionComment
        }.toDescriptionModel()
    }
}
