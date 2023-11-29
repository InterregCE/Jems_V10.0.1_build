package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection

import io.cloudflight.jems.server.payments.model.ec.CorrectionInEcPaymentMetadata
import io.cloudflight.jems.server.payments.model.ec.EcPaymentCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription

interface EcPaymentCorrectionLinkPersistence {

    fun getCorrectionExtension(correctionId: Long): EcPaymentCorrectionExtension

    fun getCorrectionsLinkedToEcPayment(ecPaymentId: Long): Map<Long, CorrectionInEcPaymentMetadata>

    fun selectCorrectionToEcPayment(correctionIds: Set<Long>, ecPaymentId: Long)

    fun deselectCorrectionFromEcPaymentAndResetFields(correctionIds: Set<Long>)

    fun updateCorrectionLinkedToEcPaymentCorrectedAmounts(
        correctionId: Long,
        ecPaymentCorrectionLinkingUpdate: PaymentToEcCorrectionLinkingUpdate
    ): EcPaymentCorrectionExtension

    fun updatePaymentToEcFinalScoBasis(toUpdate: Map<Long, PaymentSearchRequestScoBasis>)

    fun createCorrectionExtension(financialDescription: ProjectCorrectionFinancialDescription)

    fun getCorrectionIdsAvailableForEcPayments(fundId: Long): Set<Long>

}
