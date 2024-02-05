package io.cloudflight.jems.server.payments.repository.account.correction

import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountCorrectionExtensionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PaymentAccountCorrectionExtensionRepository: JpaRepository<PaymentAccountCorrectionExtensionEntity, Long> {

    @Query("SELECT e.paymentAccount.accountingYear FROM #{#entityName} e WHERE e.correctionId = :correctionId")
    fun getAccountingYearByCorrectionId(correctionId: Long): AccountingYearEntity?

}
