package io.cloudflight.jems.server.project.service.auditAndControl.base.getAuditControl

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAuditControl(
    private val auditControlPersistence: AuditControlPersistence,
): GetAuditControlInteractor {

    @CanViewAuditControl
    @Transactional
    @ExceptionWrapper(GetAuditControlException::class)
    override fun getDetails(auditControlId: Long): AuditControl =
        auditControlPersistence.getById(auditControlId = auditControlId)

}
