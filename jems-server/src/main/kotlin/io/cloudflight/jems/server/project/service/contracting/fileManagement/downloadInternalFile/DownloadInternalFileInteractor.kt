package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadInternalFile

interface DownloadInternalFileInteractor {

    fun download(projectId: Long, fileId: Long): Pair<String, ByteArray>

}
