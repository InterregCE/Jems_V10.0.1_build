package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.upload

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface UploadAttachmentToProjectReportResultPrincipleInteractor {

    fun upload(projectId: Long, reportId: Long, resultNumber: Int, file: ProjectFile): JemsFileMetadata
}
