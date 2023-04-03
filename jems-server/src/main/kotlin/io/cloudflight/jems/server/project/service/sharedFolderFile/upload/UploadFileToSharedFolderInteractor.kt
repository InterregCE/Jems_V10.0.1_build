package io.cloudflight.jems.server.project.service.sharedFolderFile.upload

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface UploadFileToSharedFolderInteractor {

    fun upload(projectId: Long, file: ProjectFile): JemsFileMetadata
}
