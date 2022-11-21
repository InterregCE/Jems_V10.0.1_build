package io.cloudflight.jems.server.project.service.report.partner.file.control.deleteControlReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportFile
import io.cloudflight.jems.server.project.service.report.partner.file.control.ControlReportFileAuthorizationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteControlReportFile(
    private val filePersistence: JemsFilePersistence,
    private val authorization: ControlReportFileAuthorizationService,
) : DeleteControlReportFileInteractor {

    @CanEditPartnerControlReportFile
    @Transactional
    @ExceptionWrapper(DeleteControlReportFileException::class)
    override fun delete(partnerId: Long, reportId: Long, fileId: Long) {
        authorization.validateChangeToFileAllowed(partnerId = partnerId, reportId = reportId, fileId)
        filePersistence.deleteFile(partnerId = partnerId, fileId = fileId)
    }

}
