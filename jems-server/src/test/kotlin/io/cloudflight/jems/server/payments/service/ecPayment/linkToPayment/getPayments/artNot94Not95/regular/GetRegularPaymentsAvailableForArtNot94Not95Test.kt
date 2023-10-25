package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95.regular

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

internal class GetRegularPaymentsAvailableForArtNot94Not95Test: UnitTest() {

    @MockK
    private lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence
    @MockK
    private lateinit var paymentPersistence: PaymentPersistence

    @InjectMockKs
    private lateinit var interactor: GetRegularPaymentsAvailableForArtNot94Not95

    @BeforeEach
    fun reset() {
        clearMocks(ecPaymentPersistence, paymentPersistence)
    }

    @Test
    fun `getPaymentList - Draft`() {
        val paymentEc = mockk<PaymentApplicationToEcDetail>()
        every { paymentEc.id } returns 28L
        every { paymentEc.paymentApplicationToEcSummary.programmeFund.id } returns 77L
        every { paymentEc.status } returns PaymentEcStatus.Draft
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(28L) } returns paymentEc

        val result = mockk<Page<PaymentToEcPayment>>()
        val slotFilter = slot<PaymentSearchRequest>()
        every { paymentPersistence.getAllPaymentToEcPayment(Pageable.unpaged(), capture(slotFilter)) } returns result
        assertThat(interactor.getPaymentList(Pageable.unpaged(), 28L)).isEqualTo(result)

        assertThat(slotFilter.captured).isEqualTo(
            PaymentSearchRequest(
                paymentId = null,
                paymentType = PaymentType.REGULAR,
                projectIdentifiers = emptySet(),
                projectAcronym = null,
                claimSubmissionDateFrom = null,
                claimSubmissionDateTo = null,
                approvalDateFrom = null,
                approvalDateTo = null,
                fundIds = setOf(77L),
                lastPaymentDateFrom = null,
                lastPaymentDateTo = null,
                ecPaymentIds = setOf(null, 28L),
                scoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            )
        )
    }

    @Test
    fun `getPaymentList - Finished`() {
        val paymentEc = mockk<PaymentApplicationToEcDetail>()
        every { paymentEc.id } returns 29L
        every { paymentEc.paymentApplicationToEcSummary.programmeFund.id } returns 78L
        every { paymentEc.status } returns PaymentEcStatus.Finished
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(29L) } returns paymentEc

        val result = mockk<Page<PaymentToEcPayment>>()
        val slotFilter = slot<PaymentSearchRequest>()
        every { paymentPersistence.getAllPaymentToEcPayment(Pageable.unpaged(), capture(slotFilter)) } returns result
        assertThat(interactor.getPaymentList(Pageable.unpaged(), 29L)).isEqualTo(result)

        assertThat(slotFilter.captured).isEqualTo(
            PaymentSearchRequest(
                paymentId = null,
                paymentType = PaymentType.REGULAR,
                projectIdentifiers = emptySet(),
                projectAcronym = null,
                claimSubmissionDateFrom = null,
                claimSubmissionDateTo = null,
                approvalDateFrom = null,
                approvalDateTo = null,
                fundIds = emptySet(),
                lastPaymentDateFrom = null,
                lastPaymentDateTo = null,
                ecPaymentIds = setOf(29L),
                scoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            )
        )
    }

}
