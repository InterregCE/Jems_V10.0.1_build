package io.cloudflight.jems.server.payments.controller.applicationToEc

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.payments.applicationToEc.PaymentApplicationToEcAttachmentApi
import io.cloudflight.jems.server.common.toResponseFile
import io.cloudflight.jems.server.payments.service.ecPayment.attachment.deletePaymentToEcAttachment.DeletePaymentToEcAttachmentInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.attachment.downloadPaymentToEcAttachment.DownloadPaymentToEcAttachmentInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.attachment.getPaymentToEcAttachment.GetPaymentToEcAttachmentInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.attachment.setDescriptionToPaymentToEcAttachment.SetDescriptionToPaymentToEcAttachmentInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.attachment.uploadPaymentToEcAttachment.UploadPaymentToEcAttachmentInteractor
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class PaymentApplicationToEcAttachmentController(
    private val downloadPaymentToEcAttachment: DownloadPaymentToEcAttachmentInteractor,
    private val deletePaymentToEcAttachment: DeletePaymentToEcAttachmentInteractor,
    private val setDescriptionToPaymentToEcAttachment: SetDescriptionToPaymentToEcAttachmentInteractor,
    private val uploadPaymentToEcAttachment: UploadPaymentToEcAttachmentInteractor,
    private val getPaymentToEcAttachment: GetPaymentToEcAttachmentInteractor,
) : PaymentApplicationToEcAttachmentApi {

    override fun listPaymentAttachments(paymentToEcId: Long, pageable: Pageable): Page<JemsFileDTO> =
        getPaymentToEcAttachment.list(paymentToEcId, pageable).map { it.toDto() }

    override fun downloadAttachment(fileId: Long): ResponseEntity<ByteArrayResource> =
        downloadPaymentToEcAttachment.download(fileId = fileId).toResponseFile()

    override fun deleteAttachment(fileId: Long) =
        deletePaymentToEcAttachment.delete(fileId)

    override fun updateAttachmentDescription(fileId: Long, description: String?) =
        setDescriptionToPaymentToEcAttachment.setDescription(fileId = fileId, description ?: "")

    override fun uploadAttachmentToPayment(paymentToEcId: Long, file: MultipartFile): JemsFileMetadataDTO =
        uploadPaymentToEcAttachment.upload(paymentToEcId = paymentToEcId, file.toProjectFile()).toDto()
}
