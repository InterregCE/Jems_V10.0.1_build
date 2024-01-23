package io.cloudflight.jems.server.plugin.services.payments

import io.cloudflight.jems.plugin.contract.models.common.paging.Page
import io.cloudflight.jems.plugin.contract.models.common.paging.Pageable
import io.cloudflight.jems.plugin.contract.models.payments.export.EcPaymentLinkedPaymentsFilterData
import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentApplicationToEcData
import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentEcStatusData
import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentToEcAmountSummaryData
import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentToEcCorrectionLinkingData
import io.cloudflight.jems.plugin.contract.models.payments.export.PaymentToEcPaymentData
import io.cloudflight.jems.plugin.contract.services.payments.EcPaymentDataProvider
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
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
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentApplicationToEcAuditDataProviderImpl(
    private val paymentApplicationToEcPersistence: PaymentApplicationToEcPersistence,
    private val ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence,
    private val correctionPersistence: AuditControlCorrectionPersistence,
    private val paymentPersistence: PaymentPersistence
) : EcPaymentDataProvider {

    @Transactional(readOnly = true)
    override fun getEcPaymentList(pageable: Pageable): Page<PaymentApplicationToEcData> =
        paymentApplicationToEcPersistence.findAllWithDetails(pageable.toJpaPage()).toPluginPage {
            PaymentApplicationToEcData(
                id = it.id,
                status = PaymentEcStatusData.valueOf(it.status.name),
                paymentApplicationToEcSummary = it.paymentApplicationToEcSummary.toDataModel(),
            )
        }

    @Transactional(readOnly = true)
    override fun getAmountsForEcPayment(ecPaymentId: Long): PaymentToEcAmountSummaryData {
        val status = paymentApplicationToEcPersistence.getPaymentApplicationToEcDetail(ecPaymentId).status

        val currentOverview = (if (status.isFinished())
            ecPaymentLinkPersistence.getTotalsForFinishedEcPayment(ecPaymentId)
        else
            ecPaymentLinkPersistence.calculateAndGetOverviewForDraftEcPayment(ecPaymentId).sumUpProperColumns()
                ).mergeBothScoBases()

        val cumulativeOverviewForThisEcPayment = ecPaymentLinkPersistence.getCumulativeTotalForEcPayment(ecPaymentId)
        val cumulativeOverviewLines = currentOverview.plus(cumulativeOverviewForThisEcPayment)
        return PaymentToEcAmountSummary(
            amountsGroupedByPriority = cumulativeOverviewLines.values.toList(),
            totals = cumulativeOverviewLines.values.sumUp(),
        ).toDataModel()
    }

    @Transactional(readOnly = true)
    override fun getCorrectionsForEcPayment(ecPaymentId: Long, pageable: Pageable): Page<PaymentToEcCorrectionLinkingData> {
        val ecPayment = paymentApplicationToEcPersistence.getPaymentApplicationToEcDetail(ecPaymentId)
        val fundId = ecPayment.paymentApplicationToEcSummary.programmeFund.id

        val filter = if (ecPayment.status.isFinished())
            constructCorrectionFilter(ecPaymentIds = setOf(ecPaymentId))
        else
            constructCorrectionFilter(ecPaymentIds = setOf(null, ecPaymentId), fundId = fundId)

        return correctionPersistence.getCorrectionsLinkedToPaymentToEc(pageable.toJpaPage(), filter)
            .toPluginPage { it.toDataModel() }
    }

    override fun getPaymentsForEcPaymentByFilter(
        paymentFiler: EcPaymentLinkedPaymentsFilterData,
        pageable: Pageable
    ): Page<PaymentToEcPaymentData> {
        val ecPayment = paymentApplicationToEcPersistence.getPaymentApplicationToEcDetail(paymentFiler.ecPaymentId)
        val fundId = ecPayment.paymentApplicationToEcSummary.programmeFund.id

        val scoBasis = PaymentSearchRequestScoBasis.valueOf(paymentFiler.scoBasis.name)
        val paymentType = if(paymentFiler.paymentType == null) null else PaymentType.valueOf(paymentFiler.paymentType!!.name)


        val filter = if (ecPayment.status.isFinished())
            filterEcPaymentLinkedPayments(setOf(ecPayment.id), paymentType = paymentType, finalScoBasis = scoBasis)
        else
            filterEcPaymentLinkedPayments(
                setOf(null, ecPayment.id), fundId = fundId, paymentType = paymentType, contractingScoBasis = scoBasis
            )

        return paymentPersistence.getAllPaymentToEcPayment(pageable.toJpaPage(), filter)
            .toPluginPage { it.toDataModel() }
    }

    private fun filterEcPaymentLinkedPayments(
        ecPaymentIds: Set<Long?>,
        fundId: Long? = null,
        finalScoBasis: PaymentSearchRequestScoBasis? = null,
        contractingScoBasis: PaymentSearchRequestScoBasis? = null,
        paymentType: PaymentType?,
    ) = constructFilter(
        ecPaymentIds = ecPaymentIds,
        fundId = fundId,
        finalScoBasis = finalScoBasis,
        contractingScoBasis = contractingScoBasis,
        paymentType = paymentType
    )

    @Transactional(readOnly = true)
    override fun getPaymentsForEcPayment(ecPaymentId: Long, pageable: Pageable): Page<PaymentToEcPaymentData> {
        val ecPayment = paymentApplicationToEcPersistence.getPaymentApplicationToEcDetail(ecPaymentId)
        val fundId = ecPayment.paymentApplicationToEcSummary.programmeFund.id

        val filter = if (ecPayment.status.isFinished())
            filterFtlsAndRegular(setOf(ecPaymentId), finalScoBasis = DoesNotFallUnderArticle94Nor95)
        else
            filterFtlsAndRegular(setOf(null, ecPaymentId), fundId = fundId, contractingScoBasis = DoesNotFallUnderArticle94Nor95)

        return paymentPersistence.getAllPaymentToEcPayment(pageable.toJpaPage(), filter)
            .toPluginPage { it.toDataModel() }
    }

    private fun filterFtlsAndRegular(
        ecPaymentIds: Set<Long?>,
        fundId: Long? = null,
        finalScoBasis: PaymentSearchRequestScoBasis? = null,
        contractingScoBasis: PaymentSearchRequestScoBasis? = null,
    ) = constructFilter(
        ecPaymentIds = ecPaymentIds,
        fundId = fundId,
        finalScoBasis = finalScoBasis,
        contractingScoBasis = contractingScoBasis,
    )
}
