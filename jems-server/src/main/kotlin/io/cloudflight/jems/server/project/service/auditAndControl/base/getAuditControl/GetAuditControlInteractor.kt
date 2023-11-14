package io.cloudflight.jems.server.project.service.auditAndControl.base.getAuditControl

import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl

interface GetAuditControlInteractor {

    fun getDetails(auditControlId: Long): AuditControl
}
