package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.payments.entity.PaymentEntity
import io.cloudflight.jems.server.payments.service.model.PaymentRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository: JpaRepository<PaymentEntity, Long> {

    @Query(""" SELECT ppls.project_id AS projectId,
                      ppcf.partner_id AS partnerId,
                      p.order_nr AS orderNr,
                      ppcf.programme_fund_id AS programmeFundId,
                      p.programme_lump_sum_id AS programmeLumpSumId,
                      TRUNCATE(amount*ppcf.percentage/100, 2) as amountApprovedPerPartner
                      FROM project_partner_lump_sum ppls
                      JOIN project_lump_sum p on ppls.project_id = p.project_id AND ppls.order_nr = p.order_nr
                      JOIN project_partner_co_financing ppcf on ppls.project_partner_id = ppcf.partner_id
                        WHERE amount != 0
                        AND ppcf.programme_fund_id IS NOT NULL
                        AND ppls.project_id = :projectId AND
                        p.order_nr IN (SELECT pls.order_nr FROM project_lump_sum pls WHERE pls.is_ready_for_payment = 1)
                        AND p.order_nr IN :orderNr
                ORDER BY programme_fund_id """, nativeQuery = true)
    fun getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId: Long, orderNr: Set<Int>): List<PaymentRow>

    @Modifying
    @Query("DELETE FROM #{#entityName} WHERE project_id = :projectId AND order_nr IN :orderNr", nativeQuery = true)
    fun deleteAllByProjectIdAndOrderNr(projectId: Long, orderNr: Set<Int>): List<PaymentEntity>

}
