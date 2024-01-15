package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.payments.entity.PaymentToEcPriorityAxisCumulativeOverviewEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentToEcPriorityAxisCumulativeOverviewRepository: JpaRepository<PaymentToEcPriorityAxisCumulativeOverviewEntity, Long> {

    fun getAllByPaymentApplicationToEcId(ecPaymentId: Long): List<PaymentToEcPriorityAxisCumulativeOverviewEntity>

}
