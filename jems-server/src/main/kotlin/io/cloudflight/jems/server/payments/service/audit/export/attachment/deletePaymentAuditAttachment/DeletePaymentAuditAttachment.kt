package io.cloudflight.jems.server.payments.service.audit.export.attachment.deletePaymentAuditAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAudit
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaymentAuditAttachment(
    private val jemsSystemFileService: JemsSystemFileService,
    private val projectFileMetadataRepository: JemsFileMetadataRepository,

    ) : DeletePaymentAuditAttachmentInteractor {

    @CanUpdatePaymentsAudit
    @Transactional
    @ExceptionWrapper(DeletePaymentAuditAttachmentException::class)
    override fun delete(fileId: Long) =
        jemsSystemFileService.delete(
            projectFileMetadataRepository.findByTypeAndId(JemsFileType.PaymentAuditAttachment, fileId) ?: throw ResourceNotFoundException("file")
        )

}
