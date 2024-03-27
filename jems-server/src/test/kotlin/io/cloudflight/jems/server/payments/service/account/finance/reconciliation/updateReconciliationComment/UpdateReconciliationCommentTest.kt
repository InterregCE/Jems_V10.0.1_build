package io.cloudflight.jems.server.payments.service.account.finance.reconciliation.updateReconciliationComment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.PaymentAccountReconciliation
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.PaymentAccountReconciliationType
import io.cloudflight.jems.server.payments.model.account.finance.reconciliation.ReconciledAmountUpdate
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.payments.service.account.finance.reconciliation.updateReconciliation.PaymentAccountNotInDraftException
import io.cloudflight.jems.server.payments.service.account.finance.reconciliation.updateReconciliation.UpdatePaymentReconciliation
import io.cloudflight.jems.server.payments.service.account.paymentAccount
import io.cloudflight.jems.server.payments.service.account.reconciliation.PaymentAccountReconciliationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate


class UpdateReconciliationCommentTest : UnitTest() {

    companion object {
        private const val PAYMENT_ACCOUNT_ID = 1L
        private const val PRIORITY_AXIS_ID = 2L

        private val reconciliationUpdate = ReconciledAmountUpdate(
            priorityAxisId = PRIORITY_AXIS_ID,
            type = PaymentAccountReconciliationType.OfAa,
            comment = "Updated comment"
        )

        private val reconciliation = PaymentAccountReconciliation(
            id = 1L,
            priorityAxisId = PRIORITY_AXIS_ID,
            paymentAccount = paymentAccount,
            totalComment = "Total Comment",
            aaComment = "",
            ecComment = ""
        )
    }

    @MockK
    lateinit var reconciliationPersistence: PaymentAccountReconciliationPersistence

    @MockK
    lateinit var paymentAccountPersistence: PaymentAccountPersistence

    @InjectMockKs
    lateinit var generalValidator: GeneralValidatorDefaultImpl

    @InjectMockKs
    lateinit var service: UpdatePaymentReconciliation

    @Test
    fun updatePaymentReconciliation() {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccount.copy(status = PaymentAccountStatus.DRAFT)
        every {
            reconciliationPersistence.updateReconciliation(
                PAYMENT_ACCOUNT_ID,
                reconciliationUpdate
            )
        } returns reconciliation

        service.updatePaymentReconciliation(PAYMENT_ACCOUNT_ID, reconciliationUpdate)
        verify(exactly = 1) { reconciliationPersistence.updateReconciliation(PAYMENT_ACCOUNT_ID, reconciliationUpdate) }

    }

    @Test
    fun `updatePaymentReconciliation - audit not in draft exception`() {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccount.copy(status = PaymentAccountStatus.FINISHED)


        assertThrows<PaymentAccountNotInDraftException> {
            service.updatePaymentReconciliation(
                PAYMENT_ACCOUNT_ID,
                reconciliationUpdate
            )
        }
    }

    @Test
    fun `updatePaymentReconciliation - comment too long exception`() {
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccount.copy(status = PaymentAccountStatus.DRAFT)

        val updateReconciliationLongComment = reconciliationUpdate.copy(comment = "test".repeat(400))

        assertThrows<AppInputValidationException> {
            service.updatePaymentReconciliation(
                PAYMENT_ACCOUNT_ID,
                updateReconciliationLongComment
            )
        }
    }

}
