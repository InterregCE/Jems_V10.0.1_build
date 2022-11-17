package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadContractFile

interface DownloadContractFileInteractor {

    fun download(projectId: Long, fileId: Long): Pair<String, ByteArray>

}
