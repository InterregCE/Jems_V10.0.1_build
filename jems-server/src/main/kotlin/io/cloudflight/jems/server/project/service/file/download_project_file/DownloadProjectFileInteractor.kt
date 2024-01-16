package io.cloudflight.jems.server.project.service.file.download_project_file

interface DownloadProjectFileInteractor {

    fun download(projectId: Long, fileId: Long): Pair<String, ByteArray>

}
