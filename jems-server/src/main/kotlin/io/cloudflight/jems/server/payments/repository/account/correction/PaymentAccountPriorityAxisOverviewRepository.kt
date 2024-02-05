package io.cloudflight.jems.server.payments.repository.account.correction

import io.cloudflight.jems.server.payments.entity.account.PaymentAccountPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentAccountPriorityAxisOverviewRepository: JpaRepository<PaymentAccountPriorityAxisOverviewEntity, Long> {

    fun getAllByPaymentAccountIdAndType(paymentAccountId: Long, type: PaymentAccountOverviewType): List<PaymentAccountPriorityAxisOverviewEntity>

    fun deleteAllByPaymentAccountId(paymentAccountId: Long)

}
