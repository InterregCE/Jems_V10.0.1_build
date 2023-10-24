package io.cloudflight.jems.server.payments.service.ecPayment.deletePaymentApplicationToEc

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate

class DeletePaymentApplicationToEcTest : UnitTest() {

    companion object {
        private const val paymentApplicationsToEcId = 1L

        private val accountingYear = AccountingYear(2L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val fund = ProgrammeFund(id = 3L, selected = true)
        private val submissionDate = LocalDate.now()

        private val paymentApplicationsToEcSummary = PaymentApplicationToEcSummary(
            programmeFund = fund,
            accountingYear = accountingYear,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private fun paymentApplicationsToEcDetail(status: PaymentEcStatus) = PaymentApplicationToEcDetail(
            id = paymentApplicationsToEcId,
            status = status,
            paymentApplicationToEcSummary = paymentApplicationsToEcSummary
        )

        private val expectedFilter = PaymentSearchRequest(
            paymentId = null,
            paymentType = null,
            projectIdentifiers = emptySet(),
            projectAcronym = null,
            claimSubmissionDateFrom = null,
            claimSubmissionDateTo = null,
            approvalDateFrom = null,
            approvalDateTo = null,
            fundIds = emptySet(),
            lastPaymentDateFrom = null,
            lastPaymentDateTo = null,
            ecPaymentIds = setOf(paymentApplicationsToEcId),
            scoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95,
        )

    }

    @MockK
    private lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence

    @MockK
    private lateinit var ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence

    @MockK
    private lateinit var paymentPersistence: PaymentPersistence

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var service: DeletePaymentApplicationToEc

    @BeforeEach
    fun resetMocks() {
        clearMocks(ecPaymentPersistence, ecPaymentLinkPersistence, paymentPersistence, auditPublisher)
    }

    @Test
    fun deleteById() {
        every {
            ecPaymentPersistence.getPaymentApplicationToEcDetail(paymentApplicationsToEcId)
        } returns paymentApplicationsToEcDetail(PaymentEcStatus.Draft)

        val paymentLinked = mockk<PaymentToProject>()
        every { paymentLinked.id } returns 74L

        val slotFilter = slot<PaymentSearchRequest>()
        every { paymentPersistence.getAllPaymentToProject(Pageable.unpaged(), capture(slotFilter)) } returns
                PageImpl(listOf(paymentLinked))

        val slotUnLinkedPaymentIds = slot<Set<Long>>()
        every { ecPaymentLinkPersistence.deselectPaymentFromEcPaymentAndResetFields(capture(slotUnLinkedPaymentIds)) } answers { }
        every { ecPaymentPersistence.deleteById(paymentApplicationsToEcId) } answers { }

        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        service.deleteById(paymentApplicationsToEcId)

        verify(exactly = 1) { ecPaymentPersistence.deleteById(paymentApplicationsToEcId) }
        verify(exactly = 1) { ecPaymentLinkPersistence.deselectPaymentFromEcPaymentAndResetFields(any()) }
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }

        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PAYMENT_APPLICATION_TO_EC_IS_DELETED,
                description = "Payment application to EC number 1 created for Fund (3, OTHER) for accounting Year 1: 2021-01-01 - 2022-06-30 was deleted"
            )
        )
        assertThat(slotFilter.captured).isEqualTo(expectedFilter)
        assertThat(slotUnLinkedPaymentIds.captured).containsExactly(74L)
    }

    @Test
    fun `delete finished payment should throw exception`() {
        every {
            ecPaymentPersistence.getPaymentApplicationToEcDetail(paymentApplicationsToEcId)
        } returns paymentApplicationsToEcDetail(PaymentEcStatus.Finished)
        assertThrows<PaymentFinishedException> { service.deleteById(paymentApplicationsToEcId) }
    }
}
