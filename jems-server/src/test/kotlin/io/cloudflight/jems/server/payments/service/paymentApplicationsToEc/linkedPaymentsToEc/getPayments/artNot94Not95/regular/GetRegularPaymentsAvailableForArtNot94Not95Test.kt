package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.linkedPaymentsToEc.getPayments.artNot94Not95.regular

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

internal class GetRegularPaymentsAvailableForArtNot94Not95Test: UnitTest() {

    @MockK
    private lateinit var paymentToEcPersistence: PaymentApplicationToEcPersistence
    @MockK
    private lateinit var paymentPersistence: PaymentPersistence

    @InjectMockKs
    private lateinit var interactor: GetRegularPaymentsAvailableForArtNot94Not95

    @BeforeEach
    fun reset() {
        clearMocks(paymentToEcPersistence, paymentPersistence)
    }

    @Test
    fun getPaymentList() {
        val paymentEc = mockk<PaymentApplicationToEcDetail>()
        every { paymentEc.id } returns 28L
        every { paymentEc.paymentApplicationToEcSummary.programmeFund.id } returns 77L
        every { paymentToEcPersistence.getPaymentApplicationToEcDetail(28L) } returns paymentEc
        every { paymentEc.status } returns PaymentEcStatus.Draft

        val result = mockk<Page<PaymentToEcPayment>>()
        val slotFilter = slot<PaymentSearchRequest>()
        every { paymentPersistence.getAllPaymentToEcPayment(Pageable.unpaged(), capture(slotFilter)) } returns result
        Assertions.assertThat(interactor.getPaymentList(Pageable.unpaged(), 28L)).isEqualTo(result)

        Assertions.assertThat(slotFilter.captured).isEqualTo(
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
                availableForEcId = 28L,
                scoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            )
        )
    }
}
