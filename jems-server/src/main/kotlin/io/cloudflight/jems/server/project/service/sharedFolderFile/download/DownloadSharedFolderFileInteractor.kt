package io.cloudflight.jems.server.project.service.sharedFolderFile.download

interface DownloadSharedFolderFileInteractor {

    fun download(projectId: Long, fileId: Long): Pair<String, ByteArray>
}
