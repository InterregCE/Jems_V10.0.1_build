package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EcPaymentRepository : JpaRepository<PaymentApplicationToEcEntity, Long> {

    fun existsByProgrammeFundIdAndAccountingYearIdAndStatus(
        programmeFundId: Long,
        accountingYearId: Long,
        status: PaymentEcStatus
    ): Boolean

    fun getByProgrammeFundIdAndAccountingYearIdAndStatus(
        programmeFundId: Long,
        accountingYearId: Long,
        status: PaymentEcStatus
    ): List<PaymentApplicationToEcEntity>

    @Query(
        """
        SELECT new kotlin.Pair(
            accYear,
            COUNT(ecPayment)
        ) FROM accounting_years accYear
            LEFT JOIN payment_applications_to_ec ecPayment
                ON accYear.id = ecPayment.accountingYear.id AND ecPayment.programmeFund.id = :programmeFundId
                    AND ecPayment.status != 'Finished'
            LEFT JOIN payment_account pa ON pa.accountingYear.id = accYear.id AND pa.programmeFund.id = :programmeFundId
        WHERE pa.status = 'DRAFT'
        GROUP BY accYear.id
        ORDER BY accYear.id
    """
    )
    fun getAvailableAccountingYearForFund(programmeFundId: Long): List<Pair<AccountingYearEntity, Int>>

}
