package io.cloudflight.jems.server.project.service.overview.getProjectAdvancePayments

import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.payments.service.advance.calculateAmountSettled
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartnerReports
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectAdvancePayments(
    private val paymentPersistence: PaymentAdvancePersistence
): GetProjectAdvancePaymentsInteractor {

    @CanRetrieveProjectPartnerReports
    @Transactional(readOnly = true)
    override fun list(
        projectId: Long,
        pageable: Pageable
    ): Page<AdvancePayment> {
        return paymentPersistence.getConfirmedPaymentsForProject(projectId, pageable).calculateAmountSettled()
    }
}
