package io.cloudflight.jems.server.project.service.report.partner.control.file.listCertificates

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListReportControlCertificates(
    private val partnerPersistence: PartnerPersistence,
    private val filePersistence: JemsFilePersistence,
): ListReportControlCertificatesInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListReportControlCertificatesException::class)
    override fun list(
        partnerId: Long,
        reportId: Long,
        pageable: Pageable
    ): Page<JemsFile> {

        val filePathPrefix = generateSearchString(
            projectId = partnerPersistence.getProjectIdForPartnerId(partnerId),
            partnerId = partnerId,
            reportId = reportId
        )

        return filePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = filePathPrefix,
            filterSubtypes = setOf(JemsFileType.ControlCertificate),
            filterUserIds = emptySet()
        )
    }

    private fun generateSearchString(
        projectId: Long,
        partnerId: Long,
        reportId: Long,
    ): String {
        return JemsFileType.ControlCertificate.generatePath(projectId, partnerId, reportId)
    }
}
