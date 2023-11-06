package io.cloudflight.jems.server.project.service.auditAndControl.correction.closeProjectAuditCorrection

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus

interface CloseProjectAuditControlCorrectionInteractor {

    fun closeProjectAuditCorrection(projectId: Long, auditControlId: Long, correctionId: Long): CorrectionStatus

}
