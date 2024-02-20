package io.cloudflight.jems.server.payments.controller.account

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.payments.account.PaymentAccountAttachmentApi
import io.cloudflight.jems.server.common.toResponseFile
import io.cloudflight.jems.server.payments.service.account.attachment.deletePaymentAccountAttachment.DeletePaymentAccountAttachmentInteractor
import io.cloudflight.jems.server.payments.service.account.attachment.downloadPaymentAccountAttachment.DownloadPaymentAccountAttachmentInteractor
import io.cloudflight.jems.server.payments.service.account.attachment.getPaymentAccountAttachment.GetPaymentAccountAttachmentInteractor
import io.cloudflight.jems.server.payments.service.account.attachment.setDescriptionToPaymentAccountAttachment.SetDescriptionToPaymentAccountAttachmentInteractor
import io.cloudflight.jems.server.payments.service.account.attachment.uploadPaymentAccountAttachment.UploadPaymentAccountAttachmentInteractor
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class PaymentAccountAttachmentController(
    private val downloadPaymentAttachment: DownloadPaymentAccountAttachmentInteractor,
    private val deletePaymentAttachment: DeletePaymentAccountAttachmentInteractor,
    private val setDescriptionToPaymentAttachment: SetDescriptionToPaymentAccountAttachmentInteractor,
    private val uploadPaymentAttachment: UploadPaymentAccountAttachmentInteractor,
    private val getPaymentAttachment: GetPaymentAccountAttachmentInteractor,
) : PaymentAccountAttachmentApi {

    override fun listPaymentAccountAttachments(paymentAccountId: Long, pageable: Pageable): Page<JemsFileDTO> =
        getPaymentAttachment.list(paymentAccountId, pageable).map { it.toDto() }

    override fun downloadAttachment(fileId: Long): ResponseEntity<ByteArrayResource> =
        downloadPaymentAttachment.download(fileId = fileId).toResponseFile()

    override fun deleteAttachment(fileId: Long) =
        deletePaymentAttachment.delete(fileId)

    override fun updateAttachmentDescription(fileId: Long, description: String?) =
        setDescriptionToPaymentAttachment.setDescription(fileId = fileId, description ?: "")

    override fun uploadAttachmentToPaymentAccount(paymentAccountId: Long, file: MultipartFile): JemsFileMetadataDTO =
        uploadPaymentAttachment.upload(paymentAccountId = paymentAccountId, file.toProjectFile()).toDto()

}
