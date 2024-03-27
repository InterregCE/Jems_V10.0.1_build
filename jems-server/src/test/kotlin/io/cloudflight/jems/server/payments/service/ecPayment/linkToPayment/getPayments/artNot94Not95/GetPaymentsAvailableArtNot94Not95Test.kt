package io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.getPayments.artNot94Not95

import io.cloudflight.jems.api.payments.dto.PaymentTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcPayment
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.payments.service.toModel
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

internal class GetPaymentsAvailableArtNot94Not95Test : UnitTest() {

    @MockK
    private lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence
    @MockK
    private lateinit var paymentPersistence: PaymentPersistence

    @InjectMockKs
    private lateinit var interactor: GetPaymentsAvailableArtNot94Not95

    @BeforeEach
    fun reset() {
        clearMocks(ecPaymentPersistence, paymentPersistence)
    }

    @ParameterizedTest(name = "can fetch available payment NOT art 94 95 by paymentType {0}")
    @EnumSource(value = PaymentTypeDTO::class)
    fun `getPaymentList - Draft`(paymentType: PaymentTypeDTO) {
        val payment = mockk<PaymentApplicationToEcDetail>()
        every { payment.id } returns 28L
        every { payment.paymentApplicationToEcSummary.programmeFund.id } returns 77L
        every { payment.status } returns PaymentEcStatus.Draft
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(28L) } returns payment

        val result = mockk<Page<PaymentToEcPayment>>()
        val slotFilter = slot<PaymentSearchRequest>()
        every { paymentPersistence.getAllPaymentToEcPayment(Pageable.unpaged(), capture(slotFilter)) } returns result
        assertThat(interactor.getPaymentList(Pageable.unpaged(), 28L, paymentType.toModel())).isEqualTo(result)

        assertThat(slotFilter.captured).isEqualTo(
            PaymentSearchRequest(
                paymentId = null,
                paymentType = paymentType.toModel(),
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
                contractingScoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
                finalScoBasis = null,
            )
        )
    }

    @ParameterizedTest(name = "can fetch available payment NOT art 94 95 by paymentType {0}")
    @EnumSource(value = PaymentTypeDTO::class)
    fun `getPaymentList - Finished`(paymentType: PaymentTypeDTO) {
        val payment = mockk<PaymentApplicationToEcDetail>()
        every { payment.id } returns 29L
        every { payment.paymentApplicationToEcSummary.programmeFund.id } returns 78L
        every { payment.status } returns PaymentEcStatus.Finished
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(29L) } returns payment

        val result = mockk<Page<PaymentToEcPayment>>()
        val slotFilter = slot<PaymentSearchRequest>()
        every { paymentPersistence.getAllPaymentToEcPayment(Pageable.unpaged(), capture(slotFilter)) } returns result
        assertThat(interactor.getPaymentList(Pageable.unpaged(), 29L, paymentType.toModel())).isEqualTo(result)

        assertThat(slotFilter.captured).isEqualTo(
            PaymentSearchRequest(
                paymentId = null,
                paymentType = paymentType.toModel(),
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
                contractingScoBasis = null,
                finalScoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
            )
        )
    }

}
