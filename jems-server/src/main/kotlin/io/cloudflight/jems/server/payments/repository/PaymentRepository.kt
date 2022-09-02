package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.payments.entity.PaymentToProjectEntity
import io.cloudflight.jems.server.payments.service.model.PaymentRow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository: JpaRepository<PaymentToProjectEntity, Long> {

    @Query(""" SELECT ppls.project_id AS projectId,
                      ppcf.partner_id AS partnerId,
                      p.order_nr AS orderNr,
                      ppcf.programme_fund_id AS programmeFundId,
                      p.programme_lump_sum_id AS programmeLumpSumId,
                      amount*ppcf.percentage/100 as amountApprovedPerFund
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

    @Query("""SELECT project.acronym as acronym,
                   pls.payment_enabled_date as payment_approval_date,
                   payment.id,
                   sum(amount_approved_per_fund) as amount_approved_per_fund,
                   payment.project_id, partner_id, payment.order_nr, payment.programme_lump_sum_id, payment.programme_fund_id,
                   table1.submission_date as payment_claim_submission_date
            FROM payment payment
                 JOIN project project on project.id = payment.project_id
                 JOIN project_lump_sum pls on pls.project_id = payment.project_id and pls.order_nr = payment.order_nr
                 JOIN (SELECT max(updated) as submission_date from project_status ps WHERE ps.project_id = project_id and ps.status = 'CONTRACTED') as table1
            GROUP BY payment.programme_fund_id, payment.order_nr""",
        countQuery = """SELECT count(*) from (select * from payment GROUP BY payment.programme_fund_id, payment.order_nr) as groupedPayment""",
        nativeQuery = true)
    fun getAllByGrouping(pageable: Pageable): Page<PaymentToProjectEntity>

    fun deleteAllByIdIn(paymentIds: List<Long>): List<PaymentToProjectEntity>

    @Modifying
    @Query("DELETE FROM #{#entityName} WHERE project_id = :projectId AND order_nr IN :orderNr", nativeQuery = true)
    fun deleteAllByProjectIdAndOrderNr(projectId: Long, orderNr: Set<Int>): List<PaymentToProjectEntity>
}
