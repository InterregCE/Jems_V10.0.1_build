package io.cloudflight.jems.server.payments.repository.applicationToEc

import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcEntity
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentApplicationsToEcRepository: JpaRepository<PaymentApplicationToEcEntity, Long> {

    fun existsByProgrammeFundIdAndAccountingYearIdAndStatus(programmeFundId: Long, accountingYearId: Long, status: PaymentEcStatus): Boolean


    @Query(
        """
       SELECT acc_year FROM accounting_years acc_year
        WHERE acc_year.id NOT IN 
            ( SELECT patec.accountingYear.id FROM payment_applications_to_ec patec 
                WHERE patec.programmeFund.id=:programmeFundId AND patec.status != 'Finished') 
        ORDER BY acc_year.year ASC 
    """
    )
    fun getAvailableAccountingYearForFund(programmeFundId: Long): Set<AccountingYearEntity>
}
