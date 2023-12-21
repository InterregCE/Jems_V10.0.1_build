package io.cloudflight.jems.server.payments.controller.applicationToEc.export

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.payments.applicationToEc.PaymentAuditAttachmentApi
import io.cloudflight.jems.server.common.toResponseFile
import io.cloudflight.jems.server.payments.service.audit.export.attachment.deletePaymentAuditAttachment.DeletePaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.downloadPaymentAuditAttachment.DownloadPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.getPaymentAuditAttchament.GetPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.setDescriptionToPaymentAuditAttachment.SetDescriptionToPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.uploadPaymentAuditAttachment.UploadPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
        downloadPaymentAuditAttachment.download(fileId = fileId).toResponseFile()

    override fun deleteAttachment(fileId: Long) =
        deletePaymentAuditAttachment.delete(fileId)

    override fun updateAttachmentDescription(fileId: Long, description: String?) =
        setDescriptionToPaymentAuditAttachment.setDescription(fileId = fileId, description ?: "")

    override fun uploadAttachmentToPaymentAudit(file: MultipartFile): JemsFileMetadataDTO =
        uploadPaymentAuditAttachment.upload(file.toProjectFile()).toDto()

}
