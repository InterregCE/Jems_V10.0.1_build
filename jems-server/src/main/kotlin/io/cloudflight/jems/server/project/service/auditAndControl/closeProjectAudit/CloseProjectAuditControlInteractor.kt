package io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus

interface CloseProjectAuditControlInteractor {

    fun closeAuditControl(projectId: Long, auditControlId: Long): AuditStatus
}
