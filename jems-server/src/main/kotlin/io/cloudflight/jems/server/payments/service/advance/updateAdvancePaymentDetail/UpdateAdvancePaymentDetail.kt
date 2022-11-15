package io.cloudflight.jems.server.payments.service.advance.updateAdvancePaymentDetail

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.payments.authorization.CanUpdateAdvancePayments
import io.cloudflight.jems.server.payments.service.advance.AdvancePaymentValidator
import io.cloudflight.jems.server.payments.service.advancePaymentAuthorized
import io.cloudflight.jems.server.payments.service.advancePaymentConfirmed
import io.cloudflight.jems.server.payments.service.advancePaymentCreated
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UpdateAdvancePaymentDetail(
    private val advancePaymentPersistence: PaymentAdvancePersistence,
    private val securityService: SecurityService,
    private val validator: AdvancePaymentValidator,
    private val auditPublisher: ApplicationEventPublisher
): UpdateAdvancePaymentDetailInteractor {

    @CanUpdateAdvancePayments
    @Transactional
    @ExceptionWrapper(UpdateAdvancePaymentDetailException::class)
    override fun updateDetail(paymentDetail: AdvancePaymentUpdate): AdvancePaymentDetail {
        val existing = if (paymentDetail.id != null && paymentDetail.id > 0) {
            advancePaymentPersistence.getPaymentDetail(paymentDetail.id)
        } else {
            null
        }
        // prevent deletion if authorized, validate data
        validator.validateDetail(paymentDetail, existing)

        // calculate authorized and confirmed user/date info
        val currentUser = securityService.getUserIdOrThrow()
        paymentDetail.fillInCurrentAuthorizedUser(currentUser, existing)
        paymentDetail.fillInCurrentConfirmedUser(currentUser, existing)

        val newlyAuthorized = (paymentDetail.paymentAuthorizedDate != null) && (paymentDetail.paymentAuthorizedDate != existing?.paymentAuthorizedDate)
        val newlyConfirmed = (paymentDetail.paymentConfirmedDate != null) && paymentDetail.paymentConfirmedDate != existing?.paymentConfirmedDate

        return advancePaymentPersistence.updatePaymentDetail(paymentDetail).also {
            if (existing == null) {
                auditPublisher.publishEvent(advancePaymentCreated(context = this, paymentDetail = it))
            }
            if (newlyAuthorized) {
                auditPublisher.publishEvent(advancePaymentAuthorized(context = this, paymentDetail = it))
            }
            if (newlyConfirmed) {
                auditPublisher.publishEvent(advancePaymentConfirmed(this, paymentDetail = it))
            }
        }
    }

    private fun AdvancePaymentUpdate.fillInCurrentAuthorizedUser(
        currentUserId: Long,
        old: AdvancePaymentDetail?
    ) {
        // paymentAuthorized checkbox was selected
        if (paymentAuthorized == true) {
            if (old == null || old.paymentAuthorized == false) {
                paymentAuthorizedUserId = currentUserId
                paymentAuthorizedDate = LocalDate.now()
            } else {
                paymentAuthorizedUserId = old.paymentAuthorizedUser?.id
                paymentAuthorizedDate = old.paymentAuthorizedDate
            }
        } // else: unselected - leave null for update
    }

    private fun AdvancePaymentUpdate.fillInCurrentConfirmedUser(
        currentUserId: Long,
        old: AdvancePaymentDetail?
    ) {
        // paymentConfirmed checkbox was selected
        if (paymentConfirmed == true) {
            if (old == null || old.paymentConfirmed == false) {
                paymentConfirmedUserId = currentUserId
                paymentConfirmedDate = LocalDate.now()
            } else {
                paymentConfirmedUserId = old.paymentConfirmedUser?.id
                paymentConfirmedDate = old.paymentConfirmedDate
            }
        } // else: unselected - leave null for update
    }

}
