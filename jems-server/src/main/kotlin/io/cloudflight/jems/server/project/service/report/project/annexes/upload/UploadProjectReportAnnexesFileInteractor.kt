package io.cloudflight.jems.server.project.service.report.project.annexes.upload

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface UploadProjectReportAnnexesFileInteractor {

    fun upload(projectId: Long, reportId: Long, file: ProjectFile): JemsFileMetadata
}
