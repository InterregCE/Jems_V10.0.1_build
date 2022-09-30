package io.cloudflight.jems.server.payments.service.attachment.getPaymentAttchament

import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPaymentAttachmentInteractor {

    fun list(paymentId: Long, pageable: Pageable): Page<ProjectReportFile>

}
