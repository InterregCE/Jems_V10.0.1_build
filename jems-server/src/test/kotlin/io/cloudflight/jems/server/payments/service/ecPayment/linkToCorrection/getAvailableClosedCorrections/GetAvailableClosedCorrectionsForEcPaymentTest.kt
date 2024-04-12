package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.getAvailableClosedCorrections

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class GetAvailableClosedCorrectionsForEcPaymentTest : UnitTest() {

    companion object {
        private const val EC_PAYMENT_ID = 20L
        private const val PROJECT_ID = 1L
        private const val FUND_ID = 11L

        val paymentDraft = mockk<PaymentApplicationToEcDetail>()

        private val correction = AuditControlCorrection(
            id = 541L,
            orderNr = 12,
            status = AuditControlStatus.Ongoing,
            type = AuditControlCorrectionType.LinkedToInvoice,
            auditControlId = 5L,
            auditControlNr = 15,
        )

        val correctionFlagged = PaymentToEcCorrectionLinking(
            correction = correction,
            projectId = PROJECT_ID,
            projectAcronym = "Acronym",
            projectCustomIdentifier = "Custom Identifier",
            priorityAxis = "PO1",
            controllingBody = ControllingBody.Controller,
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_2,
            projectFlagged94Or95 = true,
            paymentToEcId = EC_PAYMENT_ID,

            fundAmount = BigDecimal(100),
            partnerContribution = BigDecimal(101),
            publicContribution = BigDecimal(102),
            privateContribution = BigDecimal(103),
            autoPublicContribution = BigDecimal(104),
            correctedPrivateContribution = BigDecimal(105),
            correctedPublicContribution = BigDecimal(106),
            correctedAutoPublicContribution = BigDecimal(107),
            comment = "Comment",
            correctedFundAmount = BigDecimal(108),
            unionContribution = BigDecimal(109),
            correctedTotalEligibleWithoutArt94or95 = BigDecimal(110),
            correctedUnionContribution = BigDecimal(111),
            totalEligibleWithoutArt94or95 = BigDecimal(112),
        )

        val correctionNotFlagged = PaymentToEcCorrectionLinking(
            correction = correction,
            projectId = PROJECT_ID,
            projectAcronym = "Acronym",
            projectCustomIdentifier = "Custom Identifier",
            priorityAxis = "PO1",
            controllingBody = ControllingBody.Controller,
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_2,
            projectFlagged94Or95 = false,
            paymentToEcId = null,

            fundAmount = BigDecimal(200),
            partnerContribution = BigDecimal(201),
            publicContribution = BigDecimal(202),
            privateContribution = BigDecimal(203),
            autoPublicContribution = BigDecimal(204),
            correctedPrivateContribution = BigDecimal(205),
            correctedPublicContribution = BigDecimal(206),
            correctedAutoPublicContribution = BigDecimal(207),
            comment = "Comment",
            correctedFundAmount = BigDecimal(208),
            unionContribution = BigDecimal(209),
            correctedTotalEligibleWithoutArt94or95 = BigDecimal(210),
            correctedUnionContribution = BigDecimal(211),
            totalEligibleWithoutArt94or95 = BigDecimal(212),
        )
    }

    @MockK
    private lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence

    @MockK
    private lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    private lateinit var service: GetAvailableClosedCorrectionsForEcPayment

    @Test
    fun `getCorrectionList - payment draft`() {
        every { paymentDraft.id } returns EC_PAYMENT_ID
        every { paymentDraft.status } returns PaymentEcStatus.Draft
        every { paymentDraft.paymentApplicationToEcSummary.programmeFund.id } returns FUND_ID
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(EC_PAYMENT_ID) } returns paymentDraft

        val page = mockk<Pageable>()
        val slotSearchRequest = slot<PaymentToEcCorrectionSearchRequest>()
        every {
            correctionPersistence.getCorrectionsLinkedToEcPayment(page, capture(slotSearchRequest))
        } returns PageImpl(listOf(correctionNotFlagged, correctionFlagged))

        assertThat(service.getClosedCorrectionList(page, ecPaymentId = EC_PAYMENT_ID))
            .containsExactly(correctionNotFlagged, correctionFlagged)

        assertThat(slotSearchRequest.captured).isEqualTo(
            PaymentToEcCorrectionSearchRequest(
                correctionStatus = AuditControlStatus.Closed,
                ecPaymentIds = setOf(null, EC_PAYMENT_ID),
                fundIds = setOf(FUND_ID),
                scenarios = setOf(
                    ProjectCorrectionProgrammeMeasureScenario.NA,
                    ProjectCorrectionProgrammeMeasureScenario.SCENARIO_2,
                    ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5,
                ),
            )
        )
    }

    @Test
    fun `getCorrectionList - payment finished`() {
        every { paymentDraft.id } returns EC_PAYMENT_ID
        every { paymentDraft.status } returns PaymentEcStatus.Finished
        every { paymentDraft.paymentApplicationToEcSummary.programmeFund.id } returns FUND_ID
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(EC_PAYMENT_ID) } returns paymentDraft

        val page = mockk<Pageable>()
        val slotSearchRequest = slot<PaymentToEcCorrectionSearchRequest>()
        every {
            correctionPersistence.getCorrectionsLinkedToEcPayment(page, capture(slotSearchRequest))
        } returns PageImpl(listOf(correctionNotFlagged, correctionFlagged))

        assertThat(service.getClosedCorrectionList(page, ecPaymentId = EC_PAYMENT_ID))
            .containsExactly(correctionNotFlagged, correctionFlagged)

        assertThat(slotSearchRequest.captured).isEqualTo(
            PaymentToEcCorrectionSearchRequest(
                correctionStatus = AuditControlStatus.Closed,
                ecPaymentIds = setOf(EC_PAYMENT_ID),
                fundIds = emptySet(),
                scenarios = setOf(
                    ProjectCorrectionProgrammeMeasureScenario.NA,
                    ProjectCorrectionProgrammeMeasureScenario.SCENARIO_2,
                    ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5,
                ),
            )
        )
    }

}
