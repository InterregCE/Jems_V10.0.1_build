package io.cloudflight.jems.server.project.service.contracting.fileManagement

import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata

interface ProjectContractingFilePersistence {

    fun uploadFile(file: ProjectReportFileCreate): ProjectReportFileMetadata

    fun downloadFile(projectId: Long, fileId: Long): Pair<String, ByteArray>?

    fun existsFile(projectId: Long, fileId: Long): Boolean

    fun deleteFile(projectId: Long, fileId: Long)

}
