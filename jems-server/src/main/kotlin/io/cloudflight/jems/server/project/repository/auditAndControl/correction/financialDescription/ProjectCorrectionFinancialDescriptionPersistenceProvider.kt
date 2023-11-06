package io.cloudflight.jems.server.project.repository.auditAndControl.correction.financialDescription

import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.ProjectCorrectionFinancialDescriptionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescriptionUpdate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectCorrectionFinancialDescriptionPersistenceProvider(
    private val projectCorrectionFinancialDescriptionRepository: ProjectCorrectionFinancialDescriptionRepository
): ProjectCorrectionFinancialDescriptionPersistence {

    @Transactional(readOnly = true)
    override fun getCorrectionFinancialDescription(correctionId: Long): ProjectCorrectionFinancialDescription =
        projectCorrectionFinancialDescriptionRepository.getById(correctionId).toDescriptionModel()

    @Transactional
    override fun updateCorrectionFinancialDescription(
        correctionId: Long,
        financialDescription: ProjectCorrectionFinancialDescriptionUpdate
    ): ProjectCorrectionFinancialDescription {
        return projectCorrectionFinancialDescriptionRepository.getById(correctionId).apply {
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
