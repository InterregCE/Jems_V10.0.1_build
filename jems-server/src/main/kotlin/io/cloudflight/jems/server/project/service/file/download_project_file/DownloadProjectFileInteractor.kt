package io.cloudflight.jems.server.project.service.file.download_project_file

import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata

interface DownloadProjectFileInteractor {

    fun download(projectId: Long, fileId: Long): Pair<ProjectFileMetadata, ByteArray>

}
