package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.payments.entity.PaymentPartnerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentPartnerRepository: JpaRepository<PaymentPartnerEntity, Long> {

    @Modifying
    fun deleteAllByPaymentId(paymentId: Long)

    fun findAllByPaymentId(paymentId: Long): List<PaymentPartnerEntity>

    @Query("" +
        "SELECT pp.id FROM #{#entityName} pp" +
        " WHERE pp.payment.id = :paymentId AND pp.partnerId = :partnerId")
    fun getIdByPaymentIdAndPartnerId(paymentId: Long, partnerId: Long): Long
}
