package io.cloudflight.jems.server.project.service.auditAndControl.correction.deleteProjectAuditCorrection

interface DeleteProjectAuditControlCorrectionInteractor {

    fun deleteProjectAuditCorrection(projectId: Long, auditControlId: Long, correctionToBeDeletedId: Long)

}
