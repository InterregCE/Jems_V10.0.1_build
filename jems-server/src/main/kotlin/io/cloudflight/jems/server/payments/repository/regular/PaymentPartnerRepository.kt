package io.cloudflight.jems.server.payments.repository.regular

import io.cloudflight.jems.server.payments.entity.PaymentPartnerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface PaymentPartnerRepository: JpaRepository<PaymentPartnerEntity, Long> {

    fun findAllByPaymentId(paymentId: Long): List<PaymentPartnerEntity>

    fun findAllByPartnerId(partnerId: Long): List<PaymentPartnerEntity>

    @Query("" +
        "SELECT pp.id FROM #{#entityName} pp" +
        " WHERE pp.payment.id = :paymentId AND pp.partnerId = :partnerId")
    fun getIdByPaymentIdAndPartnerId(paymentId: Long, partnerId: Long): Long

    @Query("""
        SELECT new kotlin.Pair(pp.payment.fund.id, COALESCE(SUM(pp.amountApprovedPerPartner), 0))
        FROM #{#entityName} pp
        WHERE pp.partnerId = :partnerId
        GROUP BY pp.payment.fund.id
    """)
    fun getPaymentCumulative(partnerId: Long): List<Pair<Long, BigDecimal>>

    @Query("""
        SELECT new kotlin.Pair(pp.payment.fund.id, COALESCE(SUM(pp.amountApprovedPerPartner), 0))
        FROM #{#entityName} pp
        WHERE pp.payment.project.id = :projectId
        GROUP BY pp.payment.fund.id
    """)
    fun getPaymentCumulativeForProject(projectId: Long): List<Pair<Long, BigDecimal>>

}
