package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForPayment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePayments
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.AvailableCorrectionsForPayment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAvailableCorrectionsForPayment(
    private val paymentPersistence: PaymentPersistence,
    private val correctionsPersistence: AuditControlCorrectionPersistence,
) : GetAvailableCorrectionsForPaymentInteractor {

    @CanRetrievePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableCorrectionsForPaymentException::class)
    override fun getAvailableCorrections(paymentId: Long): List<AvailableCorrectionsForPayment> {
        val projectId = paymentPersistence.getProjectIdForPayment(paymentId)
        return correctionsPersistence.getAvailableCorrectionsForPayments(projectId = projectId)
    }

}
