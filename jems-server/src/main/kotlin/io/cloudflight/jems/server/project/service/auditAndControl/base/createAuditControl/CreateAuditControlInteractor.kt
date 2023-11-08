package io.cloudflight.jems.server.project.service.auditAndControl.base.createAuditControl

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate

interface CreateAuditControlInteractor {

    fun createAudit(projectId: Long, auditControl: AuditControlUpdate): AuditControl

}
