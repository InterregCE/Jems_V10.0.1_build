package io.cloudflight.jems.server.payments.service.ecPayment.createPaymentApplicationToEc

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcCreate
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcSummary
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcAmountSummaryLine
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToPayment.PaymentApplicationToEcLinkPersistence
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate

class CreatePaymentApplicationToEcTest : UnitTest() {

    companion object {
        private const val paymentApplicationsToEcId = 108L

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

        private val paymentApplicationsToEcToCreate = PaymentApplicationToEcCreate(
            id = null,
            programmeFundId = fund.id,
            accountingYearId = accountingYear.id,
            nationalReference = "National Reference",
            technicalAssistanceEur = BigDecimal.valueOf(105.32),
            submissionToSfcDate = submissionDate,
            sfcNumber = "SFC number",
            comment = "Comment"
        )

        private val paymentApplicationsToEcDetail = PaymentApplicationToEcDetail(
            id = paymentApplicationsToEcId,
            status = PaymentEcStatus.Draft,
            paymentApplicationToEcSummary = paymentApplicationsToEcSummary
        )

        private val cumulativeAmountsForFunAndYear = listOf(
            PaymentToEcAmountSummaryLine(
                priorityAxis = "PO1",
                totalEligibleExpenditure = BigDecimal(101),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(102)
            ),
            PaymentToEcAmountSummaryLine(
                priorityAxis = "PO2",
                totalEligibleExpenditure = BigDecimal(201),
                totalUnionContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal(202)
            ),
        )

    }

    @MockK
    lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence

    @MockK
    lateinit var ecPaymentLinkPersistence: PaymentApplicationToEcLinkPersistence

    @MockK
    lateinit var paymentPersistence: PaymentPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var service: CreatePaymentApplicationToEc

    @Test
    fun createPaymentApplicationToEc() {
        every {
            ecPaymentPersistence.createPaymentApplicationToEc(paymentApplicationsToEcToCreate)
        } returns paymentApplicationsToEcDetail
        every { paymentPersistence.getPaymentIdsAvailableForEcPayments(3L, PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95) } returns
                setOf(19L, 20L, 21L)
        every { ecPaymentLinkPersistence.selectPaymentToEcPayment(setOf(19L, 20L, 21L), 108L) } answers { }

        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit
        every { ecPaymentPersistence.existsDraftByFundAndAccountingYear(fund.id, accountingYear.id,) } returns false
        every {
            ecPaymentLinkPersistence.getCumulativeAmountsOfFinishedEcPaymentsByFundAndAccountingYear(
                fund.id,
                accountingYear.id
            )
        } returns cumulativeAmountsForFunAndYear
        every { ecPaymentLinkPersistence.saveCumulativeAmounts( any(), any()) } returns Unit

        assertThat(service.createPaymentApplicationToEc(paymentApplicationsToEcToCreate))
            .isEqualTo(paymentApplicationsToEcDetail)

        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PAYMENT_APPLICATION_TO_EC_IS_CREATED,
                description = "Payment application to EC number 108 was created for Fund (3, OTHER) for accounting Year 1: 2021-01-01 - 2022-06-30"
            )
        )
        verify(exactly = 1) { ecPaymentLinkPersistence.selectPaymentToEcPayment(setOf(19L, 20L, 21L), 108L) }
        verify(exactly = 1) { ecPaymentLinkPersistence.getCumulativeAmountsOfFinishedEcPaymentsByFundAndAccountingYear(fund.id, accountingYear.id) }
        verify(exactly = 1) { ecPaymentLinkPersistence.saveCumulativeAmounts(paymentApplicationsToEcId, cumulativeAmountsForFunAndYear) }
    }
}
