package io.cloudflight.jems.server.project.service.contracting.fileManagement


interface ProjectContractingFilePersistence {

    fun downloadFile(projectId: Long, fileId: Long): Pair<String, ByteArray>?

    fun downloadFileByPartnerId(partnerId: Long, fileId: Long): Pair<String, ByteArray>?

    fun existsFile(projectId: Long, fileId: Long): Boolean

    fun deleteFile(projectId: Long, fileId: Long)

    fun deleteFileByPartnerId(partnerId: Long, fileId: Long)

}
