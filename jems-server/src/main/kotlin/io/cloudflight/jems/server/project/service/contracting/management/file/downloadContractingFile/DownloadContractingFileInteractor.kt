package io.cloudflight.jems.server.project.service.contracting.management.file.downloadContractingFile

interface DownloadContractingFileInteractor {

    fun download(projectId: Long, fileId: Long): Pair<String, ByteArray>

}
