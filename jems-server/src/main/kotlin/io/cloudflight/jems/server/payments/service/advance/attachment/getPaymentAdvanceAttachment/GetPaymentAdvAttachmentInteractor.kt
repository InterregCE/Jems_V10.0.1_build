package io.cloudflight.jems.server.payments.service.advance.attachment.getPaymentAdvanceAttachment

import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentAdvAttachmentInteractor {

    fun list(paymentId: Long, pageable: Pageable): Page<JemsFile>
}