package io.cloudflight.jems.server.payments.controller.applicationToEc.export

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.payments.applicationToEc.PaymentAuditAttachmentApi
import io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.deletePaymentAuditAttachment.DeletePaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.downloadPaymentAuditAttachment.DownloadPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.getPaymentAuditAttchament.GetPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.setDescriptionToPaymentAuditAttachment.SetDescriptionToPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.uploadPaymentAuditAttachment.UploadPaymentAuditAttachmentInteractor
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
class PaymentAuditAttachmentController(
    private val downloadPaymentAuditAttachment: DownloadPaymentAuditAttachmentInteractor,
    private val deletePaymentAuditAttachment: DeletePaymentAuditAttachmentInteractor,
    private val setDescriptionToPaymentAuditAttachment: SetDescriptionToPaymentAuditAttachmentInteractor,
    private val uploadPaymentAuditAttachment: UploadPaymentAuditAttachmentInteractor,
    private val getPaymentAuditAttachment: GetPaymentAuditAttachmentInteractor,
) : PaymentAuditAttachmentApi {

    override fun listPaymentAuditAttachments(pageable: Pageable): Page<JemsFileDTO> =
        getPaymentAuditAttachment.list(pageable).map { it.toDto() }

    override fun downloadAttachment(fileId: Long): ResponseEntity<ByteArrayResource> =
        with(downloadPaymentAuditAttachment.download(fileId = fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }

    override fun deleteAttachment(fileId: Long) =
        deletePaymentAuditAttachment.delete(fileId)

    override fun updateAttachmentDescription(fileId: Long, description: String?) =
        setDescriptionToPaymentAuditAttachment.setDescription(fileId = fileId, description ?: "")

    override fun uploadAttachmentToPaymentAudit(file: MultipartFile): JemsFileMetadataDTO =
        uploadPaymentAuditAttachment.upload(file.toProjectFile()).toDto()

}
