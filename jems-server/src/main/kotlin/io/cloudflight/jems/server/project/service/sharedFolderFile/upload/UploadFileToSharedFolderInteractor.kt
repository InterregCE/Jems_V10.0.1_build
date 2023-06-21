package io.cloudflight.jems.server.project.service.sharedFolderFile.upload

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadFileToSharedFolderInteractor {

    fun upload(projectId: Long, file: ProjectFile): JemsFileMetadata
}
