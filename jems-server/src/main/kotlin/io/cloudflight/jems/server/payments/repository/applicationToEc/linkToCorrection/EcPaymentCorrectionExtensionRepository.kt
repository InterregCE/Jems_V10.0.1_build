package io.cloudflight.jems.server.payments.repository.applicationToEc.linkToCorrection

import io.cloudflight.jems.server.payments.entity.AccountingYearEntity
import io.cloudflight.jems.server.payments.entity.PaymentToEcCorrectionExtensionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EcPaymentCorrectionExtensionRepository: JpaRepository<PaymentToEcCorrectionExtensionEntity, Long> {

    fun getAllByPaymentApplicationToEcIdNull(): List<PaymentToEcCorrectionExtensionEntity>

    fun getByCorrectionId(correctionId: Long): PaymentToEcCorrectionExtensionEntity?

    @Query("SELECT e.paymentApplicationToEc.accountingYear FROM #{#entityName} e WHERE e.correctionId = :correctionId")
    fun getAccountingYearByCorrectionId(correctionId: Long): AccountingYearEntity?
}
