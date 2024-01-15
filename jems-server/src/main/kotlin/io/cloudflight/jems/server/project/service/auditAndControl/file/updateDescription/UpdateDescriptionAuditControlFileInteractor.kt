package io.cloudflight.jems.server.project.service.auditAndControl.file.updateDescription

interface UpdateDescriptionAuditControlFileInteractor {

    fun updateDescription(auditControlId: Long, fileId: Long, description: String)
}

