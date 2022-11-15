package io.cloudflight.jems.server.project.service.contracting.fileManagement

import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface ProjectContractingFilePersistence {

    fun uploadFile(file: JemsFileCreate): JemsFileMetadata

    fun downloadFile(projectId: Long, fileId: Long): Pair<String, ByteArray>?

    fun downloadFileByPartnerId(partnerId: Long, fileId: Long): Pair<String, ByteArray>?

    fun existsFile(projectId: Long, fileId: Long): Boolean

    fun deleteFile(projectId: Long, fileId: Long)

    fun deleteFileByPartnerId(partnerId: Long, fileId: Long)

}
