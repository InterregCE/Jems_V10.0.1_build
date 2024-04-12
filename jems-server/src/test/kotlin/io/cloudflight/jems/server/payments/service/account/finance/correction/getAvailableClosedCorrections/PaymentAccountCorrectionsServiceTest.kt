package io.cloudflight.jems.server.payments.service.account.finance.correction.getAvailableClosedCorrections

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinking
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionSearchRequest
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class PaymentAccountCorrectionsServiceTest: UnitTest() {

    companion object {
        private const val FUND_ID = 11L

        val paymentAccount = mockk<PaymentAccount>()
    }

    @MockK
    lateinit var paymentAccountPersistence: PaymentAccountPersistence

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    lateinit var service: PaymentAccountCorrectionsService

    @Test
    fun `getCorrectionList - payment draft`() {
        every { paymentAccount.status } returns PaymentAccountStatus.DRAFT
        every { paymentAccount.fund.id } returns FUND_ID
        every { paymentAccountPersistence.getByPaymentAccountId(20L) } returns paymentAccount

        val slotSearchRequest = slot<PaymentAccountCorrectionSearchRequest>()
        val result = mockk<Page<PaymentAccountCorrectionLinking>>()
        every {
            correctionPersistence.getCorrectionsLinkedToPaymentAccount(Pageable.unpaged(), capture(slotSearchRequest))
        } returns result

        Assertions.assertThat(service.getClosedCorrections(pageable = Pageable.unpaged(), paymentAccountId = 20L))
            .isEqualTo(result)

        Assertions.assertThat(slotSearchRequest.captured).isEqualTo(
            PaymentAccountCorrectionSearchRequest(
                correctionStatus = AuditControlStatus.Closed,
                paymentAccountIds = setOf(null, 20L),
                fundIds = setOf(FUND_ID),
                scenarios = setOf(
                    ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3,
                    ProjectCorrectionProgrammeMeasureScenario.SCENARIO_4,
                ),
            )
        )
    }

    @Test
    fun `getCorrectionList - payment finished`() {
        every { paymentAccount.status } returns PaymentAccountStatus.FINISHED
        every { paymentAccount.fund.id } returns FUND_ID
        every { paymentAccountPersistence.getByPaymentAccountId(15L) } returns paymentAccount

        val slotSearchRequest = slot<PaymentAccountCorrectionSearchRequest>()
        val result = mockk<Page<PaymentAccountCorrectionLinking>>()
        every {
            correctionPersistence.getCorrectionsLinkedToPaymentAccount(Pageable.unpaged(), capture(slotSearchRequest))
        } returns result

        Assertions.assertThat(service.getClosedCorrections(Pageable.unpaged(), paymentAccountId = 15L))
            .isEqualTo(result)

        Assertions.assertThat(slotSearchRequest.captured).isEqualTo(
            PaymentAccountCorrectionSearchRequest(
                correctionStatus = AuditControlStatus.Closed,
                paymentAccountIds = setOf(15L),
                fundIds = emptySet(),
                scenarios = setOf(
                    ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3,
                    ProjectCorrectionProgrammeMeasureScenario.SCENARIO_4,
                ),
            )
        )
    }

}
