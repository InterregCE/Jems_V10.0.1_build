package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.upload

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadAttachmentToProjectReportResultPrinciple(
    private val filePersistence: ProjectReportFilePersistence,
    private val securityService: SecurityService,
) : UploadAttachmentToProjectReportResultPrincipleInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UploadAttachmentToProjectReportResultPrincipleException::class)
    override fun upload(projectId: Long, reportId: Long, resultNumber: Int, file: ProjectFile): JemsFileMetadata {
        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.ProjectResult) {
            val location = generatePath(projectId, reportId, resultNumber.toLong())

            return filePersistence.updateProjectResultAttachment(
                reportId = reportId,
                resultNumber = resultNumber,
                file = file.getFileMetadata(
                    projectId = projectId,
                    partnerId = null,
                    location = location,
                    type = this,
                    userId = securityService.getUserIdOrThrow()
                )
            )
        }
    }
}
