package io.cloudflight.jems.server.project.service.report.partner.control.file.deleteFileAttachment

interface DeleteReportControlFileAttachmentInteractor {

    fun deleteReportControlCertificateAttachment(partnerId: Long, reportId: Long, fileId: Long, attachmentId: Long)
}
