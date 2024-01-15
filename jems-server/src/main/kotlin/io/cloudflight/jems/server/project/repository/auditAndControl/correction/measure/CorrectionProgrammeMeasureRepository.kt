package io.cloudflight.jems.server.project.repository.auditAndControl.correction.measure

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionMeasureEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CorrectionProgrammeMeasureRepository: JpaRepository<AuditControlCorrectionMeasureEntity, Long> {

    fun getByCorrectionId(correctionId: Long): AuditControlCorrectionMeasureEntity
}
