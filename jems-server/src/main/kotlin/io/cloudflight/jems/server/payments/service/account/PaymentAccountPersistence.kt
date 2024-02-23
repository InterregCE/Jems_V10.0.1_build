package io.cloudflight.jems.server.payments.service.account

import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.PaymentAccountUpdate

interface PaymentAccountPersistence {

    fun getAllAccounts(): List<PaymentAccount>

    fun findByFundAndYear(fundId: Long, accountingYearId: Long): PaymentAccount

    fun getByPaymentAccountId(paymentAccountId: Long): PaymentAccount

    fun updatePaymentAccount(paymentAccountId: Long, paymentAccount: PaymentAccountUpdate): PaymentAccount

    fun persistPaymentAccountsByFunds(programmeFundIds: Set<Long>)

    fun deletePaymentAccountsByFunds(idsToDelete: Set<Long>)

    fun finalizePaymentAccount(paymentAccountId: Long): PaymentAccountStatus

    fun reOpenPaymentAccount(paymentAccountId: Long): PaymentAccountStatus

    fun deletePaymentAccountAttachment(fileId: Long)


}
