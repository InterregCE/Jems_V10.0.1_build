package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.payments.entity.PaymentToEcCumulativeAmountsEntity
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentToEcCumulativeAmountsRepository: JpaRepository<PaymentToEcCumulativeAmountsEntity, Long> {

    fun getAllByPaymentApplicationToEcIdAndType(id: Long, type: PaymentSearchRequestScoBasis): List<PaymentToEcCumulativeAmountsEntity>

    fun deleteAllByPaymentApplicationToEcId(ecPaymentId: Long)

}
