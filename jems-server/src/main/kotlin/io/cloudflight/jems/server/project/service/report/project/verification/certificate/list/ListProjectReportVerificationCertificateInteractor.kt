package io.cloudflight.jems.server.project.service.report.project.verification.certificate.list

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListProjectReportVerificationCertificateInteractor {

    fun list(projectId: Long, reportId: Long, pageable: Pageable): Page<JemsFile>
}
