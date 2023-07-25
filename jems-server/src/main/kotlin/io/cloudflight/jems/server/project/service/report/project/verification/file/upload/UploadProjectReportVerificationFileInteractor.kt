package io.cloudflight.jems.server.project.service.report.project.verification.file.upload

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadProjectReportVerificationFileInteractor {

    fun upload(projectId: Long, reportId: Long, file: ProjectFile): JemsFileMetadata
}
