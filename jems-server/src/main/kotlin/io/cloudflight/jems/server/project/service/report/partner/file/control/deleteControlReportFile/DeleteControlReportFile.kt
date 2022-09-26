package io.cloudflight.jems.server.project.service.report.partner.file.control.deleteControlReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportFile
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.partner.file.control.ControlReportFileAuthorizationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteControlReportFile(
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val authorization: ControlReportFileAuthorizationService,
) : DeleteControlReportFileInteractor {

    @CanEditPartnerControlReportFile
    @Transactional
    @ExceptionWrapper(DeleteControlReportFileException::class)
    override fun delete(partnerId: Long, reportId: Long, fileId: Long) {
        authorization.validateChangeToFileAllowed(partnerId = partnerId, reportId = reportId, fileId)
        reportFilePersistence.deleteFile(partnerId = partnerId, fileId = fileId)
    }

}
