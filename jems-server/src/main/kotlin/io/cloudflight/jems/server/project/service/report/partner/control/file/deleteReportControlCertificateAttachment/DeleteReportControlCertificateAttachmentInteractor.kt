package io.cloudflight.jems.server.project.service.report.partner.control.file.deleteReportControlCertificateAttachment

interface DeleteReportControlCertificateAttachmentInteractor {

    fun deleteReportControlCertificateAttachment(partnerId: Long, reportId: Long, fileId: Long, attachmentId: Long)
}
