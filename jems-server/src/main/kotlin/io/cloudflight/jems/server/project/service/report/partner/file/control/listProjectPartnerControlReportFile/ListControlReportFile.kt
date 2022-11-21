package io.cloudflight.jems.server.project.service.report.partner.file.control.listProjectPartnerControlReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReportFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType.ControlDocument
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType.PartnerControlReport
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListControlReportFile(
    private val partnerPersistence: PartnerPersistence,
    private val filePersistence: JemsFilePersistence
) : ListControlReportFileInteractor {

    @CanViewPartnerControlReportFile
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListControlReportFileException::class)
    override fun list(partnerId: Long, reportId: Long, pageable: Pageable): Page<JemsFile> {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)

        return filePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = PartnerControlReport.generatePath(projectId, partnerId, reportId),
            filterSubtypes = setOf(ControlDocument),
            filterUserIds = emptySet(),
        )
    }

}
