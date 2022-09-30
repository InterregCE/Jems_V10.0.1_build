package io.cloudflight.jems.server.payments.service.updatePaymentInstallments

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerInstallmentUpdate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UpdatePaymentInstallments(
    private val paymentPersistence: PaymentPersistence,
    private val securityService: SecurityService,
    private val validator: PaymentInstallmentsValidator
): UpdatePaymentInstallmentsInteractor {

    @CanUpdatePayments
    @Transactional
    @ExceptionWrapper(UpdatePaymentInstallmentsException::class)
    override fun updatePaymentPartnerInstallments(
        paymentId: Long,
        partnerId: Long,
        installments: List<PaymentPartnerInstallmentUpdate>
    ): List<PaymentPartnerInstallment> {
        val paymentPartnerId = paymentPersistence.getPaymentPartnerId(paymentId, partnerId)
        val savedInstallments = paymentPersistence.findPaymentPartnerInstallments(paymentPartnerId)
        // find Installments for deletion
        val deleteInstallments = savedInstallments.filter { saved -> saved.id !in installments.map { it.id } }

        // prevent deletion if isSavePaymentInfo, validate data
        validator.validateInstallmentDeletion(deleteInstallments)
        validator.validateInstallmentValues(installments)
        validator.validateMaxInstallments(installments)
        validator.validateCheckboxStates(installments)

        // calculate
        // - savePaymentInfoUserId, savePaymentDate by isSavePaymentInfo
        // - paymentConfirmedUserId, paymentConfirmedDate by isPaymentConfirmed
        val currentUserId = securityService.getUserIdOrThrow()
        val today = LocalDate.now()
        installments.forEach { toUpdate ->
            calculateCheckboxValues(
                currentUserId = currentUserId,
                currentDate = today,
                old = savedInstallments.find { toUpdate.id == it.id },
                update = toUpdate
            )
        }

        return paymentPersistence.updatePaymentPartnerInstallments(
            paymentPartnerId = paymentPartnerId,
            toDeleteInstallmentIds = deleteInstallments.mapNotNull { it.id }.toHashSet(),
            paymentPartnerInstallments = installments
        )
    }

    private fun calculateCheckboxValues(
        currentUserId: Long,
        currentDate: LocalDate,
        old: PaymentPartnerInstallment?,
        update: PaymentPartnerInstallmentUpdate
    ) {
        // savePaymentInfo checkbox was selected
        if (update.isSavePaymentInfo == true) {
            if (old == null || old.isSavePaymentInfo == false) {
                update.savePaymentInfoUserId = currentUserId
                update.savePaymentDate = currentDate
            } else {
                update.savePaymentInfoUserId = old.savePaymentInfoUser?.id
                update.savePaymentDate = old.savePaymentDate
            }
        } // else: unselected - leave null for update
        // paymentConfirmed checkbox was selected
        if (update.isPaymentConfirmed == true) {
            if (old == null || old.isPaymentConfirmed == false) {
                update.paymentConfirmedUserId = currentUserId
                update.paymentConfirmedDate = currentDate
            } else {
                update.paymentConfirmedUserId = old.paymentConfirmedUser?.id
                update.paymentConfirmedDate = old.paymentConfirmedDate
            }
        } // else: unselected - leave null for update
    }

}
