package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.getAvailableClosedCorrections

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionSearchRequest
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetAvailableClosedCorrectionsForEcPayment(
    private val ecPaymentPersistence: PaymentApplicationToEcPersistence,
    private val correctionPersistence: AuditControlCorrectionPersistence
) : GetAvailableClosedCorrectionsForEcPaymentInteractor {

    companion object {
        fun filterForEcPaymentAvailableCorrections(
            ecPaymentIds: Set<Long?>,
            fundId: Long? = null,
        ) = PaymentToEcCorrectionSearchRequest(
            correctionStatus = AuditControlStatus.Closed,
            ecPaymentIds = ecPaymentIds,
            fundIds = if (fundId != null) setOf(fundId) else emptySet(),
            scenarios = ProjectCorrectionProgrammeMeasureScenario.linkableToEcPayment,
        )
    }

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableClosedCorrectionsForEcPaymentException::class)
    override fun getClosedCorrectionList(pageable: Pageable, ecPaymentId: Long): Page<PaymentToEcCorrectionLinking> {
        val ecPayment = ecPaymentPersistence.getPaymentApplicationToEcDetail(ecPaymentId)
        val fundId = ecPayment.paymentApplicationToEcSummary.programmeFund.id

        val filter = if (ecPayment.status.isFinished())
            filterForEcPaymentAvailableCorrections(ecPaymentIds = ecPaymentId.asSet())
        else
            filterForEcPaymentAvailableCorrections(ecPaymentIds = ecPaymentId.orNull(), fundId = fundId)

        val corrections = correctionPersistence.getCorrectionsLinkedToEcPayment(pageable, filter)

        if (ecPayment.isOpen())
            corrections.clearInputsFromCorrectionsNot94Nor95Flagged()

        return corrections
    }

    private fun Page<PaymentToEcCorrectionLinking>.clearInputsFromCorrectionsNot94Nor95Flagged() =
        filterNot { it.projectFlagged94Or95 }.onEach {
            it.correctedFundAmount = it.fundAmount
            it.correctedTotalEligibleWithoutArt94or95 = it.totalEligibleWithoutArt94or95
            it.correctedUnionContribution = BigDecimal.ZERO
        }

    private fun PaymentApplicationToEcDetail.isOpen() = !status.isFinished()

    private fun Long.orNull() = setOf(this, null)
    private fun Long.asSet() = setOf(this)

}
