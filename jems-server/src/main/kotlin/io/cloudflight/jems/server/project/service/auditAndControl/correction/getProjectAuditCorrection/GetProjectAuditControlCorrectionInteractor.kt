package io.cloudflight.jems.server.project.service.auditAndControl.correction.getProjectAuditCorrection

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionExtended

interface GetProjectAuditControlCorrectionInteractor {

    fun getProjectAuditCorrection(projectId: Long, auditControlId: Long, correctionId: Long): ProjectAuditControlCorrectionExtended

}
