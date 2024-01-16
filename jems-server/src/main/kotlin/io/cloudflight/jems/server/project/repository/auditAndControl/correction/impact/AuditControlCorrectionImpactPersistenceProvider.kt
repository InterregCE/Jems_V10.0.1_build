package io.cloudflight.jems.server.project.repository.auditAndControl.correction.impact

import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.service.auditAndControl.correction.impact.AuditControlCorrectionImpactPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class AuditControlCorrectionImpactPersistenceProvider(
    private val correctionRepository: AuditControlCorrectionRepository,
): AuditControlCorrectionImpactPersistence {

    @Transactional
    override fun updateCorrectionImpact(
        correctionId: Long,
        impact: AuditControlCorrectionImpact,
    ): AuditControlCorrectionImpact {
        val entity = correctionRepository.getReferenceById(correctionId)

        entity.impact = impact.action
        entity.impactComment = impact.comment

        return entity.toImpactModel()
    }

}
