package io.cloudflight.jems.server.project.service.report.project.verification.certificate.list

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationCertificate
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationPrivileged
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListProjectReportVerificationCertificate(
    private val filePersistence: JemsFilePersistence,
    private val projectReportPersistence: ProjectReportPersistence,
) : ListProjectReportVerificationCertificateInteractor {

    @CanViewReportVerificationPrivileged
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListProjectReportVerificationCertificateException::class)
    override fun list(projectId: Long, reportId: Long, pageable: Pageable): Page<JemsFile> {
        if (!projectReportPersistence.exists(projectId = projectId, reportId = reportId))
            throw FileNotFound()

        val filePathPrefix = VerificationCertificate.generatePath(projectId, reportId)
        return filePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = filePathPrefix,
            filterSubtypes = setOf(VerificationCertificate),
            filterUserIds = emptySet(),
        )
    }
}
