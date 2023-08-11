package io.cloudflight.jems.server.project.service.report.project.verification.file.upload

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationCommunication
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadProjectReportVerificationFile(
    private val filePersistence: JemsFilePersistence,
    private val projectReportFilePersistence: ProjectReportFilePersistence,
    private val securityService: SecurityService,
) : UploadProjectReportVerificationFileInteractor {

    @CanEditReportVerificationCommunication
    @Transactional
    @ExceptionWrapper(UploadProjectReportVerificationFileException::class)
    override fun upload(projectId: Long, reportId: Long, file: ProjectFile): JemsFileMetadata {
        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.VerificationDocument) {
            val location = generatePath(projectId, reportId)

            if (filePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists(file.name)

            return projectReportFilePersistence.addAttachmentToProjectReport(
                file = file.getFileMetadata(projectId, null, location, type = this, securityService.getUserIdOrThrow())
            )
        }
    }
}
