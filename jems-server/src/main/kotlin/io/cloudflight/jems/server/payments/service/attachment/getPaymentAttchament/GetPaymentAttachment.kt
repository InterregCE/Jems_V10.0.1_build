package io.cloudflight.jems.server.payments.service.attachment.getPaymentAttchament

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePayments
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.PaymentAttachment
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentAttachment(
    private val reportFilePersistence: ProjectReportFilePersistence,
) : GetPaymentAttachmentInteractor {

    @CanRetrievePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentAttachmentException::class)
    override fun list(paymentId: Long, pageable: Pageable): Page<ProjectReportFile> =
        reportFilePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = PaymentAttachment.generatePath(paymentId),
            filterSubtypes = emptySet(),
            filterUserIds = emptySet(),
        )

}
