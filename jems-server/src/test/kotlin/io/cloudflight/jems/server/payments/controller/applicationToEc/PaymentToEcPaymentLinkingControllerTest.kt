package io.cloudflight.jems.server.payments.controller.applicationToEc

import io.cloudflight.jems.api.payments.dto.PaymentToEcAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcAmountSummaryLineDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcOverviewTypeDTO
import io.cloudflight.jems.api.payments.dto.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.dto.PaymentTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.controller.PaymentsControllerTest
import io.cloudflight.jems.server.payments.controller.PaymentsControllerTest.Companion.ftlsPaymentToProject
import io.cloudflight.jems.server.payments.controller.PaymentsControllerTest.Companion.regularPaymentToProject
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcOverviewType
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.deselectPayment.DeselectPaymentFromEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeAmountsForArtNot94Not95.GetOverviewByTypeInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getCumulativeOverview.GetCumulativeOverviewInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.art94Art95.GetPaymentsAvailableForArt94Art95Interactor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95.GetPaymentsAvailableArtNot94Not95Interactor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.selectPayment.SelectPaymentToEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.updatePayment.UpdateLinkedPaymentInteractor
import io.cloudflight.jems.server.payments.service.toModel
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class PaymentToEcPaymentLinkingControllerTest : UnitTest() {

    companion object {
        private const val PAYMENT_TO_EC_ID = 45L
        private fun payment(type: PaymentType) = PaymentToEcPayment(
            payment = paymentByType(type),
            paymentToEcId = PAYMENT_TO_EC_ID,
            partnerContribution = BigDecimal.valueOf(4),
            publicContribution = BigDecimal.valueOf(5),
            correctedPublicContribution = BigDecimal.valueOf(6),
            autoPublicContribution = BigDecimal.valueOf(7),
            correctedAutoPublicContribution = BigDecimal.valueOf(8),
            privateContribution = BigDecimal.valueOf(9),
            correctedPrivateContribution = BigDecimal.valueOf(10),
            priorityAxis = "code",
            correctedTotalEligibleWithoutSco = BigDecimal.ZERO,
            correctedFundAmountUnionContribution = BigDecimal.ZERO,
            correctedFundAmountPublicContribution = BigDecimal.ZERO,
            comment = "comment",
        )

        private fun paymentByType(type: PaymentType) =
            if (type == PaymentType.FTLS) ftlsPaymentToProject.copy(paymentToEcId = PAYMENT_TO_EC_ID)
            else regularPaymentToProject.copy(paymentToEcId = PAYMENT_TO_EC_ID)

        private fun expectedPayment(type: PaymentTypeDTO) = PaymentToEcLinkingDTO(
            payment = PaymentToProjectDTO(
                id = if (type == PaymentTypeDTO.FTLS) 1L else 11L,
                paymentType = type,
                projectId = 2L,
                projectCustomIdentifier = "T1000",
                projectAcronym = "project",
                paymentClaimId = if (type == PaymentTypeDTO.FTLS) null else 5L,
                paymentClaimNo = if (type == PaymentTypeDTO.FTLS) 0 else 5,
                paymentToEcId = PAYMENT_TO_EC_ID,
                fundName = "OTHER",
                fundAmount = BigDecimal.TEN,
                amountPaidPerFund = BigDecimal.ZERO,
                amountAuthorizedPerFund = BigDecimal.ZERO,
                paymentApprovalDate = PaymentsControllerTest.currentTime,
                paymentClaimSubmissionDate = null,
                totalEligibleAmount = BigDecimal.TEN,
                lastApprovedVersionBeforeReadyForPayment = "v1.0",
                remainingToBePaid = if (type == PaymentTypeDTO.FTLS) BigDecimal.valueOf(514L) else BigDecimal.valueOf(515L),
            ),
            paymentToEcId = PAYMENT_TO_EC_ID,
            partnerContribution = BigDecimal.valueOf(4),
            publicContribution = BigDecimal.valueOf(5),
            correctedPublicContribution = BigDecimal.valueOf(6),
            autoPublicContribution = BigDecimal.valueOf(7),
            correctedAutoPublicContribution = BigDecimal.valueOf(8),
            privateContribution = BigDecimal.valueOf(9),
            correctedPrivateContribution = BigDecimal.valueOf(10),
            priorityAxis = "code",
            correctedTotalEligibleWithoutSco = BigDecimal.ZERO,
            correctedFundAmountUnionContribution = BigDecimal.ZERO,
            correctedFundAmountPublicContribution = BigDecimal.ZERO,
            comment = "comment",
        )

        private val paymentsIncludedInPaymentsToEc = listOf(
            PaymentToEcAmountSummaryLine(
                priorityAxis = "PO1",
                totalEligibleExpenditure = BigDecimal(100),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(200)
            ),
            PaymentToEcAmountSummaryLine(
                priorityAxis = "P02",
                totalEligibleExpenditure = BigDecimal(100),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(200)
            ),
        )

        private val expectedPaymentsIncludedInPaymentsToEc = listOf(
            PaymentToEcAmountSummaryLineDTO(
                priorityAxis = "PO1",
                totalEligibleExpenditure = BigDecimal(100),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(200)
            ),
            PaymentToEcAmountSummaryLineDTO(
                priorityAxis = "P02",
                totalEligibleExpenditure = BigDecimal(100),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(200)
            ),
        )

        private val totals = PaymentToEcAmountSummaryLine(
            priorityAxis = null,
            totalEligibleExpenditure = BigDecimal(300),
            totalUnionContribution = BigDecimal.ZERO,
            totalPublicContribution = BigDecimal(400)
        )

        private val expectedTotals = PaymentToEcAmountSummaryLineDTO(
            priorityAxis = null,
            totalEligibleExpenditure = BigDecimal(300),
            totalUnionContribution = BigDecimal.ZERO,
            totalPublicContribution = BigDecimal(400)
        )

        private fun expectedCumulativeAmountsSummary() = PaymentToEcAmountSummaryDTO(
            amountsGroupedByPriority = expectedPaymentsIncludedInPaymentsToEc,
            totals = expectedTotals
        )

        private fun cumulativeAmountsSummary() = PaymentToEcAmountSummary(
            amountsGroupedByPriority = paymentsIncludedInPaymentsToEc,
            totals = totals
        )
    }

    @MockK
    private lateinit var deselectPaymentFromEc: DeselectPaymentFromEcInteractor

    @MockK
    private lateinit var selectPaymentToEc: SelectPaymentToEcInteractor

    @MockK
    private lateinit var updateLinkedPayment: UpdateLinkedPaymentInteractor

    @MockK
    private lateinit var getOverviewByTypeInteractor: GetOverviewByTypeInteractor

    @MockK
    private lateinit var getCumulativeOverviewInteractor: GetCumulativeOverviewInteractor

    @MockK
    private lateinit var  getPaymentsAvailableForArt94Art95Interactor: GetPaymentsAvailableForArt94Art95Interactor

    @MockK
    private lateinit var  getPaymentsAvailableArtNot94Not95Interactor: GetPaymentsAvailableArtNot94Not95Interactor

    @InjectMockKs
    private lateinit var controller: PaymentToEcPaymentLinkingController


    @ParameterizedTest(name = "can fetch available payment for art 94 95 by paymentType {0}")
    @EnumSource(value = PaymentTypeDTO::class)
    fun getPaymentAvailableForArt94Art95(paymentType: PaymentTypeDTO) {
        every { getPaymentsAvailableForArt94Art95Interactor.getPaymentList(Pageable.unpaged(), PAYMENT_TO_EC_ID, paymentType.toModel()) } returns
                PageImpl(listOf(payment(paymentType.toModel())))

        assertThat(controller.getPaymentsLinkedWithEcForArt94OrArt95(Pageable.unpaged(), PAYMENT_TO_EC_ID,  paymentType))
            .containsExactly(expectedPayment(paymentType))
    }

    @ParameterizedTest(name = "can fetch available payment NOT art 94 95 by paymentType {0}")
    @EnumSource(value = PaymentTypeDTO::class)
    fun getPaymentAvailableNotArt94NotArt95(paymentType: PaymentTypeDTO) {
        every { getPaymentsAvailableArtNot94Not95Interactor.getPaymentList(Pageable.unpaged(), PAYMENT_TO_EC_ID, paymentType.toModel()) } returns
                PageImpl(listOf(payment(paymentType.toModel())))

        assertThat(controller.getPaymentsLinkedWithEcNotArt94NotArt95(Pageable.unpaged(), PAYMENT_TO_EC_ID,  paymentType))
            .containsExactly(expectedPayment(paymentType))
    }

    @Test
    fun selectPaymentToEc() {
        every { selectPaymentToEc.selectPaymentToEcPayment(85L, ecPaymentId = 22L) } answers { }
        controller.selectPaymentToEcPayment(paymentId = 85L, ecPaymentId = 22L)
        verify(exactly = 1) { selectPaymentToEc.selectPaymentToEcPayment(85L, ecPaymentId = 22L) }
    }

    @Test
    fun deselectPaymentFromEc() {
        every { deselectPaymentFromEc.deselectPaymentFromEcPayment(69L) } answers { }
        controller.deselectPaymentFromEcPayment(ecPaymentId = 22L, paymentId = 69L)
        verify(exactly = 1) { deselectPaymentFromEc.deselectPaymentFromEcPayment(69L) }
    }

    @Test
    fun updateLinkedPayment() {
        val toUpdate = PaymentToEcLinkingUpdateDTO(
            correctedPublicContribution = BigDecimal.valueOf(60),
            correctedAutoPublicContribution = BigDecimal.valueOf(65),
            correctedPrivateContribution = BigDecimal.valueOf(70),
            correctedTotalEligibleWithoutSco = BigDecimal.ZERO,
            correctedFundAmountUnionContribution = BigDecimal.ZERO,
            correctedFundAmountPublicContribution = BigDecimal.ZERO,
            comment = "newComment",
        )
        val slotUpdate = slot<PaymentToEcLinkingUpdate>()
        every { updateLinkedPayment.updateLinkedPayment(paymentId = 75L, capture(slotUpdate)) } answers { }

        controller.updateLinkedPayment(ecPaymentId = PAYMENT_TO_EC_ID, paymentId = 75L, toUpdate)
        verify(exactly = 1) { updateLinkedPayment.updateLinkedPayment(75L, any()) }

        assertThat(slotUpdate.captured).isEqualTo(
            PaymentToEcLinkingUpdate(
                correctedPublicContribution = BigDecimal.valueOf(60),
                correctedAutoPublicContribution = BigDecimal.valueOf(65),
                correctedPrivateContribution = BigDecimal.valueOf(70),
                correctedTotalEligibleWithoutSco = BigDecimal.ZERO,
                correctedFundAmountUnionContribution = BigDecimal.ZERO,
                correctedFundAmountPublicContribution = BigDecimal.ZERO,
                comment = "newComment"
            )
        )
    }

    @Test
    fun `getPaymentApplicationToEcCumulativeAmountsByType - type ArtNot94Not95`() {
        val expectedSummary = expectedCumulativeAmountsSummary()
        val summary = cumulativeAmountsSummary()

        every {
            getOverviewByTypeInteractor.getOverviewAmountsByType(
                PAYMENT_TO_EC_ID,
                PaymentToEcOverviewType.DoesNotFallUnderArticle94Nor95
            )
        } returns summary

        assertThat(
            controller.getPaymentApplicationToEcOverviewAmountsByType(
                PAYMENT_TO_EC_ID,
                PaymentToEcOverviewTypeDTO.DoesNotFallUnderArticle94Nor95
            )
        ).isEqualTo(expectedSummary)
    }

    @Test
    fun `getPaymentApplicationToEcCumulativeAmountsByType - type null`() {
        val expectedSummary = expectedCumulativeAmountsSummary()
        val summary = cumulativeAmountsSummary()

        every {
            getOverviewByTypeInteractor.getOverviewAmountsByType(
                PAYMENT_TO_EC_ID,
                null
            )
        } returns summary

        assertThat(
            controller.getPaymentApplicationToEcOverviewAmountsByType(
                PAYMENT_TO_EC_ID,
                null
            )
        ).isEqualTo(expectedSummary)


    }

    @Test
    fun `get EC payment cumulative overview per priority axis`() {
        val expectedSummary = expectedCumulativeAmountsSummary()
        val cumulativeSummary = cumulativeAmountsSummary()

        every {
            getCumulativeOverviewInteractor.getCumulativeOverview(PAYMENT_TO_EC_ID)
        } returns cumulativeSummary

        assertThat(
            controller.getPaymentApplicationToEcCumulativeOverview(PAYMENT_TO_EC_ID)
        ).isEqualTo(expectedSummary)
    }

}
