package io.cloudflight.jems.server.payments.repository.applicationToEc.linkToCorrection

import io.cloudflight.jems.server.payments.entity.PaymentToEcCorrectionExtensionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EcPaymentCorrectionExtensionRepository: JpaRepository<PaymentToEcCorrectionExtensionEntity, Long> {

    fun getAllByPaymentApplicationToEcIdNull(): List<PaymentToEcCorrectionExtensionEntity>

    fun getByCorrectionId(correctionId: Long): PaymentToEcCorrectionExtensionEntity?

    fun getAllByPaymentApplicationToEcId(paymentApplicationToEcId: Long): List<PaymentToEcCorrectionExtensionEntity>
}
