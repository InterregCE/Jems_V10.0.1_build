package io.cloudflight.jems.server.payments.service.advance.attachment.getPaymentAdvanceAttachment

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentAdvAttachmentInteractor {

    fun list(paymentId: Long, pageable: Pageable): Page<JemsFile>
}
