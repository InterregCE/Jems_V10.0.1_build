package io.cloudflight.jems.server.project.repository.auditAndControl.correction.identification

import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionIdentificationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CorrectionIdentificationRepository: JpaRepository<ProjectCorrectionIdentificationEntity, Long> {

    fun getByCorrectionId(correctionId: Long): ProjectCorrectionIdentificationEntity

}
