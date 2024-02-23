package io.cloudflight.jems.server.payments.service.account.attachment.getPaymentAccountAttachment

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentAccountAttachmentInteractor {

    fun list(paymentAccountId: Long, pageable: Pageable): Page<JemsFile>

}
