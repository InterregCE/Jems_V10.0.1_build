package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadPartnerFile

interface DownloadPartnerFileInteractor {
    fun downloadPartnerFile(partnerId: Long, fileId: Long): Pair<String, ByteArray>
}
