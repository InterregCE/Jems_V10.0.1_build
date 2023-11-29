package io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.getCorrections

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEcDetail
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionSearchRequest
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.PaymentApplicationToEcPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.getAvailableClosedCorrections.GetAvailableClosedCorrections
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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class GetAvailableCorrections : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 541L
        private const val EC_PAYMENT_ID = 20L
        private const val PROJECT_ID = 1L
        private const val FUND_ID = 11L

        val paymentDraft = mockk<PaymentApplicationToEcDetail>()

        private val correction = AuditControlCorrection(
            id = CORRECTION_ID,
            orderNr = 12,
            status = AuditControlStatus.Ongoing,
            type = AuditControlCorrectionType.LinkedToInvoice,
            auditControlId = 5L,
            auditControlNr = 15,
        )

        private fun searchRequest(ecPaymentIds: Set<Long?>, fundIds: Set<Long>) = PaymentToEcCorrectionSearchRequest(
            correctionStatus = AuditControlStatus.Closed,
            ecPaymentIds = ecPaymentIds,
            scenarios = listOf(
                ProjectCorrectionProgrammeMeasureScenario.NA,
                ProjectCorrectionProgrammeMeasureScenario.SCENARIO_2,
                ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5
            ),
            fundIds = fundIds
        )

        val correctionLinked = PaymentToEcCorrectionLinking(
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
            comment = "Comment"
        )

        val correctionNotLinked = PaymentToEcCorrectionLinking(
            correction = correction,
            projectId = PROJECT_ID,
            projectAcronym = "Acronym",
            projectCustomIdentifier = "Custom Identifier",
            priorityAxis = "PO1",
            controllingBody = ControllingBody.Controller,
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_2,
            projectFlagged94Or95 = true,
            paymentToEcId = null,

            fundAmount = BigDecimal(100),
            partnerContribution = BigDecimal(101),
            publicContribution = BigDecimal(102),
            privateContribution = BigDecimal(103),
            autoPublicContribution = BigDecimal(104),
            correctedPrivateContribution = BigDecimal(105),
            correctedPublicContribution = BigDecimal(106),
            correctedAutoPublicContribution = BigDecimal(107),
            comment = "Comment"
        )
    }

    @MockK
    lateinit var ecPaymentPersistence: PaymentApplicationToEcPersistence

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    lateinit var service: GetAvailableClosedCorrections

    @Test
    fun `getCorrectionList - payment draft`() {
        every { paymentDraft.id } returns EC_PAYMENT_ID
        every { paymentDraft.status } returns PaymentEcStatus.Draft
        every { paymentDraft.paymentApplicationToEcSummary.programmeFund.id } returns FUND_ID
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(EC_PAYMENT_ID) } returns paymentDraft
        every {
            correctionPersistence.getCorrectionsLinkedToPaymentToEc(
                Pageable.unpaged(),
                searchRequest(setOf(null, EC_PAYMENT_ID), setOf(FUND_ID))
            )
        } returns PageImpl(listOf(correctionNotLinked, correctionLinked))

        assertThat(
            service.getClosedCorrectionList(
                pageable = Pageable.unpaged(),
                ecApplicationId = EC_PAYMENT_ID
            ).content
        ).isEqualTo(
            listOf(correctionNotLinked, correctionLinked)
        )

    }

    @Test
    fun `getCorrectionList - payment finished`() {
        every { paymentDraft.id } returns EC_PAYMENT_ID
        every { paymentDraft.status } returns PaymentEcStatus.Finished
        every { paymentDraft.paymentApplicationToEcSummary.programmeFund.id } returns FUND_ID
        every { ecPaymentPersistence.getPaymentApplicationToEcDetail(EC_PAYMENT_ID) } returns paymentDraft
        every {
            correctionPersistence.getCorrectionsLinkedToPaymentToEc(
                Pageable.unpaged(),
                searchRequest(setOf(EC_PAYMENT_ID), emptySet())
            )
        } returns PageImpl(listOf(correctionNotLinked))

        assertThat(
            service.getClosedCorrectionList(
                pageable = Pageable.unpaged(),
                ecApplicationId = EC_PAYMENT_ID
            ).content
        ).isEqualTo(
            listOf(correctionNotLinked)
        )

    }
}
