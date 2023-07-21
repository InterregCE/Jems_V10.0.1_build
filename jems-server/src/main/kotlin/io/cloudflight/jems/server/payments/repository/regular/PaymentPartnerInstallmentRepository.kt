package io.cloudflight.jems.server.payments.repository.regular

import io.cloudflight.jems.server.payments.entity.PaymentPartnerInstallmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface PaymentPartnerInstallmentRepository: JpaRepository<PaymentPartnerInstallmentEntity, Long> {

    fun findAllByPaymentPartnerId(paymentPartnerId: Long): List<PaymentPartnerInstallmentEntity>

    fun findAllByPaymentPartnerPartnerId(partnerId: Long): List<PaymentPartnerInstallmentEntity>

    @Query("""
        SELECT new kotlin.Pair(ppi.paymentPartner.payment.fund.id, COALESCE(SUM(ppi.amountPaid), 0))
        FROM #{#entityName} ppi
        WHERE ppi.paymentPartner.payment.project.id = :projectId
        GROUP BY ppi.paymentPartner.payment.fund.id
    """)
    fun getConfirmedCumulativeForProject(projectId: Long): List<Pair<Long, BigDecimal>>

}
