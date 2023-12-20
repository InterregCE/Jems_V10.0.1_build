package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentApplicationToEcData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundTypeData
import io.cloudflight.jems.plugin.contract.services.payments.PaymentApplicationToEcAuditDataProvider
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.ec.export.PaymentApplicationToEcFull
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.constructCorrectionFilter
import io.cloudflight.jems.server.payments.service.ecPayment.constructFilter
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.mergeBothScoBases
import io.cloudflight.jems.server.payments.service.ecPayment.plus
import io.cloudflight.jems.server.payments.service.ecPayment.sumUp
import io.cloudflight.jems.server.payments.service.ecPayment.sumUpProperColumns
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.plugin.services.toModel
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentApplicationToEcAuditDataProviderImpl(
    private val paymentApplicationToEcPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
    private val correctionPersistence: AuditControlCorrectionPersistence,
    private val paymentPersistence: PaymentPersistence
) : PaymentApplicationToEcAuditDataProvider {

    @Transactional(readOnly = true)
    override fun getPaymentApplicationToEcAuditData(
        accountingYear: Short?,
        programmeFundType: ProgrammeFundTypeData?
    ): List<PaymentApplicationToEcData> {
        val payments = paymentApplicationToEcPersistence.findAllWithDetails()
            .filter {
                programmeFundType?.let {
                        fundType -> fundType.toModel() == it.paymentApplicationToEcSummary.programmeFund.type
                } ?: true
            }
            .filter {
                accountingYear?.let {
                        accountingYear -> accountingYear == it.paymentApplicationToEcSummary.accountingYear.year
                } ?: true
            }

        return payments.map {
            PaymentApplicationToEcFull(
                id = it.id,
                paymentApplicationToEcSummary = it.paymentApplicationToEcSummary,
                paymentToEcAmountSummary = calculateCumulativeValues(it),
                corrections = getCorrectionsForEcPayment(it),
                regularProjectPayments = getPaymentsRegular(it)
            ).toDataModel()
        }
    }

    private fun calculateCumulativeValues(payment: PaymentApplicationToEcDetail): PaymentToEcAmountSummary {
        val cumulativeOverviewForThisEcPayment = ecPaymentLinkPersistence.getCumulativeTotalForEcPayment(payment.id)

        val currentOverview = (if (payment.status.isFinished())
            ecPaymentLinkPersistence.getTotalsForFinishedEcPayment(payment.id)
        else
            ecPaymentLinkPersistence.calculateAndGetOverviewForDraftEcPayment(payment.id).sumUpProperColumns()
            ).mergeBothScoBases()

        val cumulativeOverviewLines = currentOverview.plus(cumulativeOverviewForThisEcPayment)
        return PaymentToEcAmountSummary(
            amountsGroupedByPriority = cumulativeOverviewLines.values.toList(),
            totals = cumulativeOverviewLines.values.sumUp(),
        )
    }

    private fun getCorrectionsForEcPayment(payment: PaymentApplicationToEcDetail): List<PaymentToEcCorrectionLinking> {
        val fundId = payment.paymentApplicationToEcSummary.programmeFund.id

        val filter = if (payment.status.isFinished())
            constructCorrectionFilter(ecPaymentIds = setOf(payment.id))
        else
            constructCorrectionFilter(ecPaymentIds = setOf(null, payment.id), fundId = fundId)

        return correctionPersistence.getCorrectionsLinkedToPaymentToEc(Pageable.unpaged(), filter).content
    }

    private fun getPaymentsRegular(payment: PaymentApplicationToEcDetail): List<PaymentToEcPayment> {
        val fundId = payment.paymentApplicationToEcSummary.programmeFund.id

        val ftlsFilter = if (payment.status.isFinished())
            filterFtls(ecPaymentIds = setOf(payment.id))
        else
            filterFtls(ecPaymentIds = setOf(null, payment.id), fundId = fundId)

        val ftlsPayments = paymentPersistence.getAllPaymentToEcPayment(Pageable.unpaged(), ftlsFilter)

        val regularFilter = if (payment.status.isFinished())
            filterRegular(ecPaymentIds = setOf(payment.id))
        else
            filterRegular(ecPaymentIds = setOf(null, payment.id), fundId = fundId)

        val regularPayments = paymentPersistence.getAllPaymentToEcPayment(Pageable.unpaged(), regularFilter)

        return ftlsPayments.content.plus(regularPayments.content)
    }

    private fun filterFtls(ecPaymentIds: Set<Long?>, fundId: Long? = null) = constructFilter(
        ecPaymentIds = ecPaymentIds,
        fundId = fundId,
        scoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
        paymentType = PaymentType.FTLS,
    )

    private fun filterRegular(ecPaymentIds: Set<Long?>, fundId: Long? = null) = constructFilter(
        ecPaymentIds = ecPaymentIds,
        fundId = fundId,
        scoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
        paymentType = PaymentType.REGULAR,
    )
}
