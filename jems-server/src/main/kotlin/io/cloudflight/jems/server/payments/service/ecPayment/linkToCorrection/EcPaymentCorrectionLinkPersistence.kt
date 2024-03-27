package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection

import io.cloudflight.jems.server.payments.model.ec.CorrectionInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import java.math.BigDecimal

interface EcPaymentCorrectionLinkPersistence {

    fun getCorrectionExtension(correctionId: Long): PaymentToEcCorrectionExtension

    fun getCorrectionsLinkedToEcPayment(ecPaymentId: Long): Map<Long, CorrectionInEcPaymentMetadata>

    fun selectCorrectionToEcPayment(correctionIds: Set<Long>, ecPaymentId: Long)

    fun deselectCorrectionFromEcPaymentAndResetFields(correctionIds: Set<Long>)

    fun updateCorrectionLinkedToEcPaymentCorrectedAmounts(
        correctionId: Long,
        ecPaymentCorrectionLinkingUpdate: PaymentToEcCorrectionLinkingUpdate
    ): PaymentToEcCorrectionExtension

    fun updatePaymentToEcFinalScoBasis(toUpdate: Map<Long, PaymentSearchRequestScoBasis>)

    fun createCorrectionExtension(
        financialDescription: ProjectCorrectionFinancialDescription,
        totalEligibleWithoutArt94or95: BigDecimal,
        unionContribution: BigDecimal
    )

    fun getCorrectionIdsAvailableForEcPayments(fundId: Long): Set<Long>

}
