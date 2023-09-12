package io.cloudflight.jems.server.payments.repository.regular

import io.cloudflight.jems.server.payments.entity.PaymentPartnerEntity
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface PaymentPartnerRepository: JpaRepository<PaymentPartnerEntity, Long> {

    fun findAllByPaymentId(paymentId: Long): List<PaymentPartnerEntity>

    fun findAllByProjectPartnerId(partnerId: Long): List<PaymentPartnerEntity>

    @Query("" +
        "SELECT pp.id FROM #{#entityName} pp" +
        " WHERE pp.payment.id = :paymentId AND pp.projectPartner.id = :partnerId")
    fun getIdByPaymentIdAndPartnerId(paymentId: Long, partnerId: Long): Long

    @Query("""
        SELECT new kotlin.Pair(pp.payment.fund.id, COALESCE(SUM(pp.amountApprovedPerPartner), 0))
        FROM #{#entityName} pp
        WHERE pp.projectPartner.id = :partnerId and pp.payment.type = :paymentType
        GROUP BY pp.payment.fund.id
    """)
    fun getPaymentOfTypeCumulativeForPartner(paymentType: PaymentType, partnerId: Long): List<Pair<Long, BigDecimal>>

    @Query("""
        SELECT new kotlin.Pair(pp.payment.fund.id, COALESCE(SUM(pp.amountApprovedPerPartner), 0))
        FROM #{#entityName} pp
        WHERE pp.payment.project.id = :projectId and pp.payment.type = :paymentType
        GROUP BY pp.payment.fund.id
    """)
    fun getPaymentOfTypeCumulativeForProject(paymentType: PaymentType, projectId: Long): List<Pair<Long, BigDecimal>>

}
