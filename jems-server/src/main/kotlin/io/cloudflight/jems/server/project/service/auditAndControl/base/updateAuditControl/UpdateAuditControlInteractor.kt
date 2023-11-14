package io.cloudflight.jems.server.project.service.auditAndControl.base.updateAuditControl

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate

interface UpdateAuditControlInteractor {

    fun updateAudit(auditControlId: Long, auditControlData: AuditControlUpdate): AuditControl

}
