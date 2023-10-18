package io.cloudflight.jems.server.project.service.auditAndControl.file.download

interface DownloadAuditControlFileInteractor {

    fun download(projectId: Long, auditControlId: Long, fileId: Long): Pair<String, ByteArray>
}
