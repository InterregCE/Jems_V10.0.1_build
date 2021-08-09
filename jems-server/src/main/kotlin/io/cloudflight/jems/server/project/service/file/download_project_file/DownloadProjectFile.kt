package io.cloudflight.jems.server.project.service.file.download_project_file

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanDownloadFileFromCategory
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import io.cloudflight.jems.server.project.service.projectFileDownloadFailed
import io.cloudflight.jems.server.project.service.projectFileDownloadSucceed
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadProjectFile(
    private val filePersistence: ProjectFilePersistence,
    private val projectPersistence: ProjectPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val securityService: SecurityService
) : DownloadProjectFileInteractor {

    @CanDownloadFileFromCategory
    @Transactional
    @ExceptionWrapper(DownloadProjectFileExceptions::class)
    override fun download(projectId: Long, fileId: Long): Pair<ProjectFileMetadata, ByteArray> {
        projectPersistence.throwIfNotExists(projectId)
        return runCatching {
            filePersistence.getFileMetadata(fileId).let { fileMetadata ->
                Pair(fileMetadata, filePersistence.getFile(projectId, fileId, fileMetadata.name)).also {
                    auditPublisher.publishEvent(projectFileDownloadSucceed(this, fileMetadata))
                }
            }
        }.onFailure {
            auditPublisher.publishEvent(
                projectFileDownloadFailed(
                    this, projectId, fileId, securityService.currentUser?.user?.id!!
                )
            )
        }.getOrThrow()
    }
}
