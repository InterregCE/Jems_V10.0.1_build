package io.cloudflight.jems.server.project.service.auditAndControl.file.upload

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadAuditControlFileInteractor {

    fun upload(auditControlId: Long, file: ProjectFile): JemsFileMetadata
}
