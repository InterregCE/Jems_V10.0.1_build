package io.cloudflight.jems.server.project.service.report.project.annexes.upload

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.repository.file.ProjectFileTypeNotSupported
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.project.annexes.ProjectReportAnnexesFilePersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadProjectReportAnnexesFile(
    private val projectPersistence: ProjectPersistence,
    private val userPersistence: UserPersistence,
    private val filePersistence: JemsFilePersistence,
    private val securityService: SecurityService,
    private val projectReportFilePersistence: ProjectReportAnnexesFilePersistence
) : UploadProjectReportAnnexesFileInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UploadProjectReportAnnexesFileException::class)
    override fun upload(projectId: Long, reportId: Long, file: ProjectFile): JemsFileMetadata {

        if (isFileTypeInvalid(file))
            throw ProjectFileTypeNotSupported()

        projectPersistence.throwIfNotExists(projectId)
        val currentUser = securityService.currentUser?.user?.id!!
        userPersistence.throwIfNotExists(currentUser)

        with(JemsFileType.ProjectReport) {
            val location = generatePath(projectId, reportId)

            if (filePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists()

            return projectReportFilePersistence.saveFile(
                file.getFileMetadata(
                    projectId,
                    null,
                    location,
                    this,
                    securityService.getUserIdOrThrow()
                )
            )
        }
    }
}
