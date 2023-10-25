package io.cloudflight.jems.server.payments.service.advance.updateAdvancePaymentDetail

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdateAdvancePayments
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentDetail
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSettlement
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentUpdate
import io.cloudflight.jems.server.payments.service.advance.AdvancePaymentValidator
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.payments.service.advancePaymentCreated
import io.cloudflight.jems.server.payments.service.advancePaymentSettlementCreated
import io.cloudflight.jems.server.payments.service.advancePaymentSettlementDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAdvancePaymentDetail(
    private val advancePaymentPersistence: PaymentAdvancePersistence,
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

        paymentDetail.reNumberSettlements()

        return advancePaymentPersistence.updatePaymentDetail(paymentDetail).also {
            if (existing == null) {
                auditPublisher.publishEvent(advancePaymentCreated(context = this, paymentDetail = it))
            }

            auditSettlements(
                paymentDetails = it,
                newSettlements = paymentDetail.getNewSettlements(),
                deletedSettlements = paymentDetail.getDeletedSettlements(existing?.paymentSettlements)
            )
        }
    }

    private fun AdvancePaymentUpdate.reNumberSettlements(): AdvancePaymentUpdate {
        this.paymentSettlements.forEachIndexed { index, settlement ->
            settlement.number = index.plus(1)
        }
        return this
    }

    private fun auditSettlements(
        paymentDetails: AdvancePaymentDetail,
        newSettlements: List<AdvancePaymentSettlement>,
        deletedSettlements: List<AdvancePaymentSettlement>
    ) {

        newSettlements.forEach { settlement ->
            auditPublisher.publishEvent(
                advancePaymentSettlementCreated(
                    this, settlement, paymentDetails
                )
            )
        }

        deletedSettlements.forEach { settlement ->
            auditPublisher.publishEvent(
                advancePaymentSettlementDeleted(
                    this, settlement, paymentDetails
                )
            )
        }
    }

    private fun AdvancePaymentUpdate.getDeletedSettlements(existing: List<AdvancePaymentSettlement>?): List<AdvancePaymentSettlement>  {
        val updatedSettlementsIds = this.paymentSettlements.map { it.id }
        return existing?.filter { it.id !in updatedSettlementsIds } ?: emptyList()
    }

    private fun AdvancePaymentUpdate.getNewSettlements() = this.paymentSettlements.filter { it.id == 0L }

}
