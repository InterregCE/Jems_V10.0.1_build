package io.cloudflight.jems.server.project.service.report.partner.control.file.deleteFileAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportFile
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import io.cloudflight.jems.server.project.service.report.partner.control.file.validateCertificateFileAttachment
import io.cloudflight.jems.server.project.service.report.partner.file.deleteProjectPartnerReportFile.FileNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteReportControlFileAttachment(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence
) : DeleteReportControlFileAttachmentInteractor {

    @CanEditPartnerControlReportFile
    @Transactional()
    @ExceptionWrapper(DeleteReportControlFileAttachmentException::class)
    override fun deleteReportControlCertificateAttachment(partnerId: Long, reportId: Long, fileId: Long, attachmentId: Long) {

        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        val certificateFile = projectPartnerReportControlFilePersistence.getByReportIdAndId(reportId, fileId)

        if (certificateFile.signedFile == null)
            throw FileNotFound()

        validateCertificateFileAttachment(certificateFile, report.lastControlReopening) { DeletionNotAllowed() }


        if (report.status.controlNotFullyOpen())
            throw DeletionNotAllowed()

        projectPartnerReportControlFilePersistence.deleteCertificateAttachment(fileId)
    }
}
