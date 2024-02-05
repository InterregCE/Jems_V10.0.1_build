package io.cloudflight.jems.server.payments.service.account.corrections.getAvailableClosedCorrections

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionLinking
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionSearchRequest
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class GetAvailableClosedCorrectionsForPaymentAccountTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 541L
        private const val PAYMENT_ACCOUNT_ID = 20L
        private const val PROJECT_ID = 1L
        private const val FUND_ID = 11L

        val paymentAccount = mockk<PaymentAccount>()

        private val correction = AuditControlCorrection(
            id = CORRECTION_ID,
            orderNr = 12,
            status = AuditControlStatus.Ongoing,
            type = AuditControlCorrectionType.LinkedToInvoice,
            auditControlId = 5L,
            auditControlNr = 15,
        )

        private fun searchRequest(paymentAccountIds: Set<Long?>, fundIds: Set<Long>) = PaymentAccountCorrectionSearchRequest(
            correctionStatus = AuditControlStatus.Closed,
            paymentAccountIds = paymentAccountIds,
            scenarios = listOf(
                ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3,
                ProjectCorrectionProgrammeMeasureScenario.SCENARIO_4
            ),
            fundIds = fundIds
        )

        val correctionLinked = PaymentAccountCorrectionLinking(
            correction = correction,
            projectId = PROJECT_ID,
            projectAcronym = "Acronym",
            projectCustomIdentifier = "Custom Identifier",
            priorityAxis = "PO1",
            controllingBody = ControllingBody.Controller,
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_4,
            projectFlagged94Or95 = true,
            paymentAccountId = PAYMENT_ACCOUNT_ID,

            fundAmount = BigDecimal(100),
            partnerContribution = BigDecimal(101),
            publicContribution = BigDecimal(102),
            privateContribution = BigDecimal(103),
            autoPublicContribution = BigDecimal(104),
            correctedPrivateContribution = BigDecimal(105),
            correctedPublicContribution = BigDecimal(106),
            correctedAutoPublicContribution = BigDecimal(107),
            comment = "Comment",
            correctedFundAmount = BigDecimal(107),
        )

        val correctionNotLinked = PaymentAccountCorrectionLinking(
            correction = correction,
            projectId = PROJECT_ID,
            projectAcronym = "Acronym",
            projectCustomIdentifier = "Custom Identifier",
            priorityAxis = "PO1",
            controllingBody = ControllingBody.Controller,
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_4,
            projectFlagged94Or95 = true,
            paymentAccountId = null,

            fundAmount = BigDecimal(100),
            partnerContribution = BigDecimal(101),
            publicContribution = BigDecimal(102),
            privateContribution = BigDecimal(103),
            autoPublicContribution = BigDecimal(104),
            correctedPrivateContribution = BigDecimal(105),
            correctedPublicContribution = BigDecimal(106),
            correctedAutoPublicContribution = BigDecimal(107),
            comment = "Comment",
            correctedFundAmount = BigDecimal(107),
        )
    }

    @MockK
    lateinit var paymentAccountPersistence: PaymentAccountPersistence

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    lateinit var service: GetAvailableClosedCorrectionsForPaymentAccount

    @Test
    fun `getCorrectionList - payment draft`() {
        every { paymentAccount.id } returns PAYMENT_ACCOUNT_ID
        every { paymentAccount.status } returns PaymentAccountStatus.DRAFT
        every { paymentAccount.fund.id } returns FUND_ID
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccount
        every {
            correctionPersistence.getCorrectionsLinkedToPaymentAccount(
                Pageable.unpaged(),
                searchRequest(setOf(null, PAYMENT_ACCOUNT_ID), setOf(FUND_ID))
            )
        } returns PageImpl(listOf(correctionNotLinked, correctionLinked))

        Assertions.assertThat(
            service.getClosedCorrections(pageable = Pageable.unpaged(), paymentAccountId = PAYMENT_ACCOUNT_ID).content
        ).isEqualTo(
            listOf(correctionNotLinked, correctionLinked)
        )

    }

    @Test
    fun `getCorrectionList - payment finished`() {
        every { paymentAccount.id } returns PAYMENT_ACCOUNT_ID
        every { paymentAccount.status } returns PaymentAccountStatus.FINISHED
        every { paymentAccount.fund.id } returns FUND_ID
        every { paymentAccountPersistence.getByPaymentAccountId(PAYMENT_ACCOUNT_ID) } returns paymentAccount
        every {
            correctionPersistence.getCorrectionsLinkedToPaymentAccount(
                Pageable.unpaged(),
                searchRequest(setOf(PAYMENT_ACCOUNT_ID), emptySet())
            )
        } returns PageImpl(listOf(correctionNotLinked))

        Assertions.assertThat(
            service.getClosedCorrections(pageable = Pageable.unpaged(), paymentAccountId = PAYMENT_ACCOUNT_ID).content
        ).isEqualTo(
            listOf(correctionNotLinked)
        )

    }
}
