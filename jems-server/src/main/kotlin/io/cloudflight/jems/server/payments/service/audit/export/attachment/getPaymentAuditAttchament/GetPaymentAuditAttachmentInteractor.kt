package io.cloudflight.jems.server.payments.service.audit.export.attachment.getPaymentAuditAttchament

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentAuditAttachmentInteractor {

    fun list(pageable: Pageable): Page<JemsFile>

}
