package io.cloudflight.jems.server.project.service.auditAndControl.base.listAuditControl

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewAuditControlForProject
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListAuditControl(
    private val auditControlPersistence: AuditControlPersistenceProvider
): ListAuditControlIntetractor {

    @CanViewAuditControlForProject
    @Transactional
    @ExceptionWrapper(ListAuditControlException::class)
    override fun listForProject(projectId: Long, pageable: Pageable): Page<AuditControl> =
        auditControlPersistence.findAllProjectAudits(projectId, pageable)

}
