package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.payments.entity.PaymentToEcExtensionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentToEcExtensionRepository: JpaRepository<PaymentToEcExtensionEntity, Long> {

    fun findAllByPaymentApplicationToEcId(ecPaymentId: Long): List<PaymentToEcExtensionEntity>

    fun findAllByPaymentApplicationToEcNotNullAndPaymentProjectReportId(projectReportId: Long): List<PaymentToEcExtensionEntity>

}
