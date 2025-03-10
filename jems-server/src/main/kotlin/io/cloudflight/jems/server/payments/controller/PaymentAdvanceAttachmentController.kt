package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.payments.PaymentAdvanceAttachmentApi
import io.cloudflight.jems.server.common.toResponseFile
import io.cloudflight.jems.server.payments.service.advance.attachment.deletePaymentAdvanceAttachment.DeletePaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.downloadPaymentAdvanceAttachment.DownloadPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.getPaymentAdvanceAttachment.GetPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.setDescriptionToPaymentAdvAttachment.SetDescriptionToPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.uploadPaymentAdvanceAttachment.UploadPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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

    override fun listPaymentAttachments(paymentId: Long, pageable: Pageable): Page<JemsFileDTO> =
        getPaymentAdvAttachment.list(paymentId, pageable).map { it.toDto() }

    override fun downloadAttachment(fileId: Long): ResponseEntity<ByteArrayResource> =
        downloadPaymentAdvAttachment.download(fileId = fileId).toResponseFile()

    override fun deleteAttachment(fileId: Long) =
        deletePaymentAdvAttachment.delete(fileId)


    override fun updateAttachmentDescription(fileId: Long, description: String?) =
        setDescriptionToPaymentAdvAttachment.setDescription(fileId = fileId, description ?: "")


    override fun uploadAttachmentToPayment(paymentId: Long, file: MultipartFile): JemsFileMetadataDTO =
        uploadPaymentAdvAttachment.upload(paymentId = paymentId, file.toProjectFile()).toDto()
}
