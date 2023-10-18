package io.cloudflight.jems.server.project.service.auditAndControl.file.updateDescription

interface UpdateDescriptionAuditControlFileInteractor {

    fun updateDescription(projectId: Long, auditControlId: Long, fileId: Long, description: String)
}

