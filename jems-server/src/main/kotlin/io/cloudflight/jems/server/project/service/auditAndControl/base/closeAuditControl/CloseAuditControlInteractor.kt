package io.cloudflight.jems.server.project.service.auditAndControl.base.closeAuditControl

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus

interface CloseAuditControlInteractor {

    fun closeAuditControl(auditControlId: Long): AuditControlStatus
}
