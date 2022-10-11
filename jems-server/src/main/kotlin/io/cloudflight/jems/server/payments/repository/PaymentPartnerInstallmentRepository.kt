package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.payments.entity.PaymentPartnerInstallmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentPartnerInstallmentRepository: JpaRepository<PaymentPartnerInstallmentEntity, Long> {

    fun findAllByPaymentPartnerId(paymentPartnerId: Long): List<PaymentPartnerInstallmentEntity>

    fun findAllByPaymentPartnerPartnerId(partnerId: Long): List<PaymentPartnerInstallmentEntity>

}
