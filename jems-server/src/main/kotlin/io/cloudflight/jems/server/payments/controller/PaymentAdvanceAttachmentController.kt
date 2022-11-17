package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.payments.PaymentAdvanceAttachmentApi
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.server.payments.service.advance.attachment.deletePaymentAdvanceAttachment.DeletePaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.downloadPaymentAdvanceAttachment.DownloadPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.getPaymentAdvanceAttachment.GetPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.setDescriptionToPaymentAdvAttachment.SetDescriptionToPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.uploadPaymentAdvanceAttachment.UploadPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.project.controller.report.toDto
import io.cloudflight.jems.server.project.controller.report.toProjectFile
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class PaymentAdvanceAttachmentController(
    private val getPaymentAdvAttachment: GetPaymentAdvAttachmentInteractor,
    private val deletePaymentAdvAttachment: DeletePaymentAdvAttachmentInteractor,
    private val uploadPaymentAdvAttachment: UploadPaymentAdvAttachmentInteractor,
    private val downloadPaymentAdvAttachment: DownloadPaymentAdvAttachmentInteractor,
    private val setDescriptionToPaymentAdvAttachment: SetDescriptionToPaymentAdvAttachmentInteractor
) : PaymentAdvanceAttachmentApi {

    override fun listPaymentAttachments(paymentId: Long, pageable: Pageable): Page<ProjectReportFileDTO> =
        getPaymentAdvAttachment.list(paymentId, pageable).map { it.toDto() }

    override fun downloadAttachment(fileId: Long): ResponseEntity<ByteArrayResource> =
        with(downloadPaymentAdvAttachment.download(fileId = fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }

    override fun deleteAttachment(fileId: Long) =
        deletePaymentAdvAttachment.delete(fileId)


    override fun updateAttachmentDescription(fileId: Long, description: String?) =
        setDescriptionToPaymentAdvAttachment.setDescription(fileId = fileId, description ?: "")


    override fun uploadAttachmentToPayment(paymentId: Long, file: MultipartFile): ProjectReportFileMetadataDTO =
        uploadPaymentAdvAttachment.upload(paymentId = paymentId, file.toProjectFile()).toDto()
}