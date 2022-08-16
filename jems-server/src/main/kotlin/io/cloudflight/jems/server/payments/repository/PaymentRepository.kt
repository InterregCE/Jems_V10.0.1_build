package io.cloudflight.jems.server.payments.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignmentWithUsers
import io.cloudflight.jems.server.payments.entity.PaymentToProjectEntity
import io.cloudflight.jems.server.payments.service.model.ComputedPaymentToProject
import io.cloudflight.jems.server.payments.service.model.PaymentRow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository: JpaRepository<PaymentToProjectEntity, Long> {

    @Query("""
            SELECT table1.project_id, table1.order_nr, table1.programme_lump_sum_id, table1.programme_fund_id, SUM(table1.amount* (table1.percentage/100)) as amount_approved_per_fund FROM
            ( SELECT p.order_nr, ppls.project_id, ppcf.partner_id, ppcf.percentage, ppcf.programme_fund_id, p.programme_lump_sum_id, p.payment_enabled_date, amount FROM project_partner_lump_sum ppls
                JOIN project_lump_sum p on ppls.project_id = p.project_id AND ppls.order_nr = p.order_nr
                JOIN project_partner_co_financing ppcf on ppls.project_partner_id = ppcf.partner_id
            WHERE amount != 0 AND ppcf.programme_fund_id IS NOT NULL AND ppls.project_id = :projectId AND p.order_nr IN (SELECT pls.order_nr FROM project_lump_sum pls WHERE pls.is_ready_for_payment = 1)
            ORDER BY programme_fund_id) as table1
            GROUP BY table1.order_nr, table1.programme_fund_id""", nativeQuery = true
    )
    fun getApprovedAmountsPerFundByProjectId(projectId: Long): List<PaymentRow>

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

    fun deleteAllByIdIn(paymentIds: List<Long>): List<PaymentToProjectEntity>

    @Modifying
    @Query("DELETE FROM #{#entityName} WHERE project_id = :projectId AND order_nr IN :orderNr", nativeQuery = true)
    fun deleteAllByProjectIdAndOrderNr(projectId: Long, orderNr: Set<Int>): List<PaymentToProjectEntity>
}
