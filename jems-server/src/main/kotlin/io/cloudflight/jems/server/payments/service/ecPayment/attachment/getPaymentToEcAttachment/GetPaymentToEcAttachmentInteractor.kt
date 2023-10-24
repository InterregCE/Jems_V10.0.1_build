package io.cloudflight.jems.server.payments.service.ecPayment.attachment.getPaymentToEcAttachment

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentToEcAttachmentInteractor {

    fun list(paymentToEcId: Long, pageable: Pageable): Page<JemsFile>

}
