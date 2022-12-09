package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.payments.PaymentAttachmentApi
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.server.payments.service.regular.attachment.deletePaymentAttachment.DeletePaymentAttachmentInteractor
import io.cloudflight.jems.server.payments.service.regular.attachment.downloadPaymentAttachment.DownloadPaymentAttachmentInteractor
import io.cloudflight.jems.server.payments.service.regular.attachment.getPaymentAttchament.GetPaymentAttachmentInteractor
import io.cloudflight.jems.server.payments.service.regular.attachment.setDescriptionToPaymentAttachment.SetDescriptionToPaymentAttachmentInteractor
import io.cloudflight.jems.server.payments.service.regular.attachment.uploadPaymentAttachment.UploadPaymentAttachmentInteractor
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class PaymentAttachmentController(
    private val downloadPaymentAttachment: DownloadPaymentAttachmentInteractor,
    private val deletePaymentAttachment: DeletePaymentAttachmentInteractor,
    private val setDescriptionToPaymentAttachment: SetDescriptionToPaymentAttachmentInteractor,
    private val uploadPaymentAttachment: UploadPaymentAttachmentInteractor,
    private val getPaymentAttachment: GetPaymentAttachmentInteractor,
) : PaymentAttachmentApi {

    override fun listPaymentAttachments(paymentId: Long, pageable: Pageable): Page<ProjectReportFileDTO> =
        getPaymentAttachment.list(paymentId, pageable).map { it.toDto() }

    override fun downloadAttachment(fileId: Long): ResponseEntity<ByteArrayResource> =
        with(downloadPaymentAttachment.download(fileId = fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }

    override fun deleteAttachment(fileId: Long) =
        deletePaymentAttachment.delete(fileId)

    override fun updateAttachmentDescription(fileId: Long, description: String?) =
        setDescriptionToPaymentAttachment.setDescription(fileId = fileId, description ?: "")

    override fun uploadAttachmentToPayment(paymentId: Long, file: MultipartFile): ProjectReportFileMetadataDTO =
        uploadPaymentAttachment.upload(paymentId = paymentId, file.toProjectFile()).toDto()

}
