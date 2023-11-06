package io.cloudflight.jems.server.project.repository.auditAndControl.correction.programmeMeasure

import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionProgrammeMeasureEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CorrectionProgrammeMeasureRepository: JpaRepository<ProjectCorrectionProgrammeMeasureEntity, Long> {

    fun getByCorrectionId(correctionId: Long): ProjectCorrectionProgrammeMeasureEntity
}
