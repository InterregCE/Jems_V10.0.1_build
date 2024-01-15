package io.cloudflight.jems.server.project.service.auditAndControl.base.reopenAuditControl

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus

interface ReopenAuditControlInteractor {

    fun reopenAuditControl(auditControlId: Long): AuditControlStatus
}
