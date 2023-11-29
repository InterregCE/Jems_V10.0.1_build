package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.payments.entity.PaymentToEcPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentToEcPriorityAxisOverviewRepository: JpaRepository<PaymentToEcPriorityAxisOverviewEntity, Long> {

    fun getAllByPaymentApplicationToEcIdAndType(id: Long, type: PaymentToEcOverviewType): List<PaymentToEcPriorityAxisOverviewEntity>

    fun deleteAllByPaymentApplicationToEcId(ecPaymentId: Long)

}
