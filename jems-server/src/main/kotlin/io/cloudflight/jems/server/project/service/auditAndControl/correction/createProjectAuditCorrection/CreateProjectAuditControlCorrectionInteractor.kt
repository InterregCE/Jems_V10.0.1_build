package io.cloudflight.jems.server.project.service.auditAndControl.correction.createProjectAuditCorrection

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection

interface CreateProjectAuditControlCorrectionInteractor {

    fun createProjectAuditCorrection(projectId: Long, auditControlId: Long, linkedToInvoice: Boolean): ProjectAuditControlCorrection

}
