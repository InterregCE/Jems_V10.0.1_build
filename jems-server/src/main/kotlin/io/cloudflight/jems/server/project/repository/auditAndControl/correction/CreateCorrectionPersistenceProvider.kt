package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionFinancialDescriptionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionIdentificationEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionProgrammeMeasureEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.financialDescription.ProjectCorrectionFinancialDescriptionRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.identification.CorrectionIdentificationRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.programmeMeasure.CorrectionProgrammeMeasureRepository
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCreateCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal


@Repository
class CreateCorrectionPersistenceProvider(
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
    private val auditControlRepository: AuditControlRepository,
    private val auditControlCorrectionIdentificationRepository: CorrectionIdentificationRepository,
    private val projectCorrectionFinancialDescriptionRepository: ProjectCorrectionFinancialDescriptionRepository,
    private val programmeMeasureRepository: CorrectionProgrammeMeasureRepository,
) : AuditControlCreateCorrectionPersistence {

    @Transactional
    override fun createCorrection(correction: ProjectAuditControlCorrection): ProjectAuditControlCorrection {
        val correctionEntity = persistCorrection(correction)

        persistIdentification(correctionEntity)
        persistFinancialDescription(correctionEntity)
        persistProgrammeMeasure(correctionEntity)

        return correctionEntity.toModel()
    }

    private fun persistCorrection(correction: ProjectAuditControlCorrection): ProjectAuditControlCorrectionEntity =
        auditControlCorrectionRepository.save(
            correction.toEntity(auditControlResolver = { auditControlRepository.getById(it) })
        )


    private fun persistIdentification(correction: ProjectAuditControlCorrectionEntity) {
        auditControlCorrectionIdentificationRepository.save(
            ProjectCorrectionIdentificationEntity(
                correctionEntity = correction,
                followUpOfCorrectionId = null,
                correctionFollowUpType = CorrectionFollowUpType.No,
                repaymentFrom = null,
                lateRepaymentTo = null,
                partnerId = null,
                partnerReportId = null,
                programmeFundId = null
            )
        )
    }

    private fun persistFinancialDescription(correction: ProjectAuditControlCorrectionEntity) {
        projectCorrectionFinancialDescriptionRepository.save(
            ProjectCorrectionFinancialDescriptionEntity(
                correctionId = correction.id,
                correction = correction,
                deduction = true,
                fundAmount = BigDecimal.ZERO,
                autoPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                infoSentBeneficiaryComment = null,
                infoSentBeneficiaryDate = null,
                correctionType = null,
                clericalTechnicalMistake = false,
                goldPlating = false,
                suspectedFraud = false,
                correctionComment = null
            )
        )
    }

    private fun persistProgrammeMeasure(correction: ProjectAuditControlCorrectionEntity) =
        programmeMeasureRepository.save(
            ProjectCorrectionProgrammeMeasureEntity(
                correctionEntity = correction,
                scenario = ProjectCorrectionProgrammeMeasureScenario.NA,
                comment = null,
            )
        )

}
