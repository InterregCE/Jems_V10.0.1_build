package io.cloudflight.jems.server.payments.service.regular.attachment.getPaymentAttchament

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentAttachmentInteractor {

    fun list(paymentId: Long, pageable: Pageable): Page<JemsFile>

}
