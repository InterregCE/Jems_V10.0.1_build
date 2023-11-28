package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.CorrectionImpactAction
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AuditControlCorrectionRepository : JpaRepository<AuditControlCorrectionEntity, Long> {

    fun findAllByAuditControlId(auditControlId: Long, pageable: Pageable): Page<AuditControlCorrectionEntity>

    fun findFirstByAuditControlIdOrderByOrderNrDesc(auditControlId: Long): AuditControlCorrectionEntity?

    fun getAllByAuditControlAndStatusAndOrderNrBefore(
        auditControl: AuditControlEntity,
        status: AuditControlStatus,
        orderNr: Int
    ): List<AuditControlCorrectionEntity>

    fun getAllByAuditControlIdAndStatus(auditControlId: Long, status: AuditControlStatus): List<AuditControlCorrectionEntity>

    fun findAllByAuditControlProjectIdAndStatusAndImpactIn(
        projectId: Long,
        status: AuditControlStatus,
        impact: Set<CorrectionImpactAction>
    ): List<AuditControlCorrectionEntity>

    fun findAllByAuditControlProjectId(projectId: Long): Set<Long>

}
