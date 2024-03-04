package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionMeasureEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.measure.CorrectionProgrammeMeasureRepository
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCreateCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal


@Repository
class AuditControlCreateCorrectionPersistenceProvider(
    private val auditControlRepository: AuditControlRepository,
    private val auditControlCorrectionRepository: AuditControlCorrectionRepository,
    private val auditControlCorrectionFinanceRepository: ProjectCorrectionFinancialDescriptionRepository,
    private val auditControlCorrectionMeasureRepository: CorrectionProgrammeMeasureRepository,
) : AuditControlCreateCorrectionPersistence {

    @Transactional
    override fun createCorrection(auditControlId: Long, correction: AuditControlCorrectionCreate): AuditControlCorrectionDetail {
        val correctionEntity = persistCorrection(auditControlId, correction)

        persistFinancialDescription(correctionEntity)
        persistProgrammeMeasure(correctionEntity)

        return correctionEntity.toModel()
    }

    private fun persistCorrection(auditControlId: Long, correction: AuditControlCorrectionCreate): AuditControlCorrectionEntity {
        val auditControlEntity = auditControlRepository.getReferenceById(auditControlId)
        return auditControlCorrectionRepository.save(correction.toEntity(auditControlEntity))
    }

    private fun persistFinancialDescription(correction: AuditControlCorrectionEntity) {
        auditControlCorrectionFinanceRepository.save(
            AuditControlCorrectionFinanceEntity(
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

    private fun persistProgrammeMeasure(correction: AuditControlCorrectionEntity) =
        auditControlCorrectionMeasureRepository.save(
            AuditControlCorrectionMeasureEntity(
                correction = correction,
                scenario = ProjectCorrectionProgrammeMeasureScenario.NA,
                comment = null,
            )
        )

}
