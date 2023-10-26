package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuditControlCorrectionRepository: JpaRepository<ProjectAuditControlCorrectionEntity, Long> {

    fun findAllByAuditControlEntityId(auditControlId: Long, pageable: Pageable): Page<ProjectAuditControlCorrectionEntity>

    fun findFirstByAuditControlEntityIdOrderByOrderNrDesc(auditControlId: Long): ProjectAuditControlCorrectionEntity?

    fun getAllByAuditControlEntityIdAndStatusAndOrderNrBefore(
        auditControlId: Long,
        status: CorrectionStatus,
        orderNr: Int
    ): List<ProjectAuditControlCorrectionEntity>

    fun getAllByAuditControlEntityIdAndStatus(auditControlId: Long, status: CorrectionStatus): List<ProjectAuditControlCorrectionEntity>

    fun getFirstByAuditControlEntityIdAndStatusOrderByOrderNrDesc(auditControlId: Long, status: CorrectionStatus): ProjectAuditControlCorrectionEntity?

}
