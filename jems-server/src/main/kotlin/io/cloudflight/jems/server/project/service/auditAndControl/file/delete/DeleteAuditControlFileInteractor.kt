package io.cloudflight.jems.server.project.service.auditAndControl.file.delete

interface DeleteAuditControlFileInteractor {

    fun delete(projectId: Long, auditControlId: Long, fileId: Long)
}
