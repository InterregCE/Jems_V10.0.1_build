package io.cloudflight.jems.server.payments.controller.applicationToEc

import io.cloudflight.jems.api.payments.dto.PaymentSearchRequestScoBasisDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcAmountSummaryLineDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcLinkingUpdateDTO
import io.cloudflight.jems.api.payments.dto.PaymentToProjectDTO
import io.cloudflight.jems.api.payments.dto.PaymentTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.controller.PaymentsControllerTest
import io.cloudflight.jems.server.payments.controller.PaymentsControllerTest.Companion.ftlsPaymentToProject
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcLinkingUpdate
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.deselectPayment.DeselectPaymentFromEcInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getCumulativeAmountsForArtNot94Not95.GetCumulativeAmountsByTypeInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getPayments.artNot94Not95.GetFtlsPaymentsAvailableForArtNot94Not95Interactor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.selectPayment.SelectPaymentToEcInteractor
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.updatePayment.UpdateLinkedPaymentInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class PaymentToEcPaymentLinkingControllerTest : UnitTest() {

    companion object {
        private const val paymentApplicationsToEcId = 45L
        private val payment = PaymentToEcPayment(
            payment = ftlsPaymentToProject,
            paymentToEcId = 45L,
            partnerContribution = BigDecimal.valueOf(4),
            publicContribution = BigDecimal.valueOf(5),
            correctedPublicContribution = BigDecimal.valueOf(6),
            autoPublicContribution = BigDecimal.valueOf(7),
            correctedAutoPublicContribution = BigDecimal.valueOf(8),
            privateContribution = BigDecimal.valueOf(9),
            correctedPrivateContribution = BigDecimal.valueOf(10),
            priorityAxis = "code",
        )

        private val expectedPayment = PaymentToEcLinkingDTO(
            payment = PaymentToProjectDTO(
                id = 1L,
                paymentType = PaymentTypeDTO.FTLS,
                projectId = 2L,
                projectCustomIdentifier = "T1000",
                projectAcronym = "project",
                paymentClaimId = null,
                paymentClaimNo = 0,
                fundName = "OTHER",
                amountApprovedPerFund = BigDecimal.TEN,
                amountPaidPerFund = BigDecimal.ZERO,
                amountAuthorizedPerFund = BigDecimal.ZERO,
                paymentApprovalDate = PaymentsControllerTest.currentTime,
                paymentClaimSubmissionDate = null,
                totalEligibleAmount = BigDecimal.TEN,
                lastApprovedVersionBeforeReadyForPayment = "v1.0",
            ),
            paymentToEcId = paymentApplicationsToEcId,
            partnerContribution = BigDecimal.valueOf(4),
            publicContribution = BigDecimal.valueOf(5),
            correctedPublicContribution = BigDecimal.valueOf(6),
            autoPublicContribution = BigDecimal.valueOf(7),
            correctedAutoPublicContribution = BigDecimal.valueOf(8),
            privateContribution = BigDecimal.valueOf(9),
            correctedPrivateContribution = BigDecimal.valueOf(10),
            priorityAxis = "code",
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

        private val expectedTotals =   PaymentToEcAmountSummaryLineDTO(
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
    private lateinit var getPaymentsAvailableForArtNot94Not95: GetFtlsPaymentsAvailableForArtNot94Not95Interactor
    @MockK
    private lateinit var deselectPaymentFromEc: DeselectPaymentFromEcInteractor
    @MockK
    private lateinit var selectPaymentToEc: SelectPaymentToEcInteractor
    @MockK
    private lateinit var updateLinkedPayment: UpdateLinkedPaymentInteractor
    @MockK
    lateinit var getCumulativeAmountsSummaryInteractor: GetCumulativeAmountsByTypeInteractor

    @InjectMockKs
    private lateinit var controller: PaymentToEcPaymentLinkingController

    @Test
    fun getFTLSPaymentsLinkedWithEcForArtNot94Not95() {
        every { getPaymentsAvailableForArtNot94Not95.getPaymentList(Pageable.unpaged(), 45L) } returns
                PageImpl(listOf(payment))

        assertThat(controller.getFTLSPaymentsLinkedWithEcForArtNot94Not95(Pageable.unpaged(), 45L))
            .containsExactly(expectedPayment)
    }

    @Test
    fun selectPaymentToEc() {
        every { selectPaymentToEc.selectPaymentToEcPayment(85L, ecPaymentId = 22L) } answers { }
        controller.selectPaymentToEcPayment(paymentId = 85L, ecApplicationId = 22L)
        verify(exactly = 1) { selectPaymentToEc.selectPaymentToEcPayment(85L, ecPaymentId = 22L) }
    }

    @Test
    fun deselectPaymentFromEc() {
        every { deselectPaymentFromEc.deselectPaymentFromEcPayment(69L) } answers { }
        controller.deselectPaymentFromEcPayment(paymentId = 69L)
        verify(exactly = 1) { deselectPaymentFromEc.deselectPaymentFromEcPayment(69L) }
    }

    @Test
    fun updateLinkedPayment() {
        val toUpdate = PaymentToEcLinkingUpdateDTO(
            correctedPublicContribution = BigDecimal.valueOf(60),
            correctedAutoPublicContribution = BigDecimal.valueOf(65),
            correctedPrivateContribution = BigDecimal.valueOf(70),
        )
        val slotUpdate = slot<PaymentToEcLinkingUpdate>()
        every { updateLinkedPayment.updateLinkedPayment(paymentId = 75L, capture(slotUpdate)) } answers { }

        controller.updateLinkedPayment(paymentId = 75L, toUpdate)
        verify(exactly = 1) { updateLinkedPayment.updateLinkedPayment(75L, any()) }

        assertThat(slotUpdate.captured).isEqualTo(
            PaymentToEcLinkingUpdate(
                correctedPublicContribution = BigDecimal.valueOf(60),
                correctedAutoPublicContribution = BigDecimal.valueOf(65),
                correctedPrivateContribution = BigDecimal.valueOf(70),
            )
        )
    }

    @Test
    fun `getPaymentApplicationToEcCumulativeAmountsByType - type ArtNot94Not95`() {
        val expectedSummary = expectedCumulativeAmountsSummary()
        val summary = cumulativeAmountsSummary()

        every {
            getCumulativeAmountsSummaryInteractor.getCumulativeAmountsByType(
                paymentApplicationsToEcId,
                PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
            )
        } returns summary

        assertThat(
            controller.getPaymentApplicationToEcCumulativeAmountsByType(
                paymentApplicationsToEcId,
                PaymentSearchRequestScoBasisDTO.DoesNotFallUnderArticle94Nor95
            )
        ).isEqualTo(expectedSummary)
    }

    @Test
    fun `getPaymentApplicationToEcCumulativeAmountsByType - type null`() {
        val expectedSummary = expectedCumulativeAmountsSummary()
        val summary = cumulativeAmountsSummary()

        every {
            getCumulativeAmountsSummaryInteractor.getCumulativeAmountsByType(
                paymentApplicationsToEcId,
                null
            )
        } returns summary

        assertThat(
            controller.getPaymentApplicationToEcCumulativeAmountsByType(
                paymentApplicationsToEcId,
                null
            )
        ).isEqualTo(expectedSummary)


    }

}
