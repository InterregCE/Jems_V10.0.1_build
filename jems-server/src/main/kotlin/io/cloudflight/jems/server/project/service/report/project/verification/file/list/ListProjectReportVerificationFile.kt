package io.cloudflight.jems.server.project.service.report.project.verification.file.list

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationCommunication
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListProjectReportVerificationFile(
    private val filePersistence: JemsFilePersistence,
    private val projectReportPersistence: ProjectReportPersistence,
) : ListProjectReportVerificationFileInteractor {

    @CanViewReportVerificationCommunication
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListProjectReportVerificationFileException::class)
    override fun list(projectId: Long, reportId: Long, pageable: Pageable): Page<JemsFile> {
        if (!projectReportPersistence.exists(projectId = projectId, reportId = reportId))
            throw FileNotFound()

        val filePathPrefix = VerificationDocument.generatePath(projectId, reportId)
        return filePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = filePathPrefix,
            filterSubtypes = setOf(VerificationDocument),
            filterUserIds = emptySet(),
        )
    }
}
