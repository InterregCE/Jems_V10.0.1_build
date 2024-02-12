package io.cloudflight.jems.server.payments.controller.account.finance

import io.cloudflight.jems.api.payments.dto.account.finance.PaymentAccountAmountSummaryDTO
import io.cloudflight.jems.api.payments.dto.account.finance.PaymentAccountAmountSummaryLineDTO
import io.cloudflight.jems.api.payments.dto.account.finance.correction.PaymentAccountCorrectionLinkingDTO
import io.cloudflight.jems.api.payments.dto.account.finance.correction.PaymentAccountCorrectionLinkingUpdateDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ControllingBodyDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureScenarioDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummary
import io.cloudflight.jems.server.payments.model.account.finance.PaymentAccountAmountSummaryLine
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinking
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.service.account.finance.correction.deselectCorrection.DeselectCorrectionFromPaymentAccount
import io.cloudflight.jems.server.payments.service.account.finance.correction.getAvailableClosedCorrections.GetAvailableClosedCorrectionsForPaymentAccount
import io.cloudflight.jems.server.payments.service.account.finance.correction.getOverview.GetPaymentAccountCurrentOverview
import io.cloudflight.jems.server.payments.service.account.finance.correction.selectCorrection.SelectCorrectionToPaymentAccount
import io.cloudflight.jems.server.payments.service.account.finance.correction.updateCorrection.UpdateLinkedCorrectionToPaymentAccount
import io.cloudflight.jems.server.payments.service.toModel
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class PaymentAccountCorrectionLinkingControllerTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val PAYMENT_ACCOUNT_ID = 10L
        private const val CORRECTION_ID = 541L

        private val correction = AuditControlCorrection(
            id = CORRECTION_ID,
            orderNr = 12,
            status = AuditControlStatus.Ongoing,
            type = AuditControlCorrectionType.LinkedToInvoice,
            auditControlId = 5L,
            auditControlNr = 15,
        )

        private val expectedCorrection = AuditControlCorrectionDTO(
            id = CORRECTION_ID,
            orderNr = 12,
            status = AuditStatusDTO.Ongoing,
            type = AuditControlCorrectionTypeDTO.LinkedToInvoice,
            auditControlId = 5L,
            auditControlNumber = 15,
        )

        private val correctionList = listOf(
            PaymentAccountCorrectionLinking(
                correction = correction,
                projectId = PROJECT_ID,
                projectAcronym = "Acronym",
                projectCustomIdentifier = "Custom Identifier",
                priorityAxis = "PO1",
                controllingBody = ControllingBody.Controller,
                scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_2,
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
                correctedFundAmount = BigDecimal(100),
            )
        )


        private val expectedAccountCorrection = PaymentAccountCorrectionLinkingDTO(
                correction = expectedCorrection,
                projectId = PROJECT_ID,
                projectAcronym = "Acronym",
                projectCustomIdentifier = "Custom Identifier",
                priorityAxis = "PO1",
                controllingBody = ControllingBodyDTO.Controller,
                scenario = ProjectCorrectionProgrammeMeasureScenarioDTO.SCENARIO_2,
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
                correctedFundAmount = BigDecimal(100),
        )

        private val correctionUpdate = PaymentAccountCorrectionLinkingUpdateDTO(
            correctedPrivateContribution = BigDecimal(205),
            correctedPublicContribution = BigDecimal(206),
            correctedAutoPublicContribution = BigDecimal(207),
            comment = "Updated comment",
            correctedFundAmount = BigDecimal(55),
        )


        private val paymentToEcExtensionModel = PaymentAccountCorrectionExtension(
            correctionId = CORRECTION_ID,
            paymentAccountId = PAYMENT_ACCOUNT_ID,
            paymentAccountStatus = PaymentAccountStatus.DRAFT,
            auditControlStatus = AuditControlStatus.Ongoing,
            comment = "Comment",
            fundAmount = BigDecimal.valueOf(25.80),
            publicContribution = BigDecimal.valueOf(35.00),
            correctedPublicContribution = BigDecimal.valueOf(36.20),
            autoPublicContribution = BigDecimal.valueOf(15.00),
            correctedAutoPublicContribution = BigDecimal.valueOf(16.00),
            privateContribution = BigDecimal.valueOf(45.00),
            correctedPrivateContribution = BigDecimal.valueOf(46.20),
            correctedFundAmount = BigDecimal.valueOf(75.00),
        )

        private fun summaryLine() = PaymentAccountAmountSummaryLine(
            priorityAxis = "P01",
            totalEligibleExpenditure = BigDecimal.valueOf(20),
            totalPublicContribution = BigDecimal.valueOf(10)
        )

        private val paymentAccountCurrentOverviewSummary = PaymentAccountAmountSummary(
            amountsGroupedByPriority = listOf(summaryLine()),
            totals = summaryLine()
        )

        private fun summaryLineDTO() = PaymentAccountAmountSummaryLineDTO(
            priorityAxis = "P01",
            totalEligibleExpenditure = BigDecimal.valueOf(20),
            totalPublicContribution = BigDecimal.valueOf(10)
        )

        private val paymentAccountCurrentOverviewSummaryDTO = PaymentAccountAmountSummaryDTO(
            amountsGroupedByPriority = listOf(summaryLineDTO()),
            totals = summaryLineDTO()
        )

    }

    @MockK
    lateinit var getAvailableCorrections: GetAvailableClosedCorrectionsForPaymentAccount

    @MockK
    lateinit var selectCorrection: SelectCorrectionToPaymentAccount

    @MockK
    lateinit var deselectCorrection: DeselectCorrectionFromPaymentAccount

    @MockK
    lateinit var updateLinkedCorrection: UpdateLinkedCorrectionToPaymentAccount

    @MockK
    lateinit var getCurrentOverview: GetPaymentAccountCurrentOverview

    @InjectMockKs
    lateinit var controller: PaymentAccountCorrectionLinkingController

    @Test
    fun getAvailableCorrections() {
        every { getAvailableCorrections.getClosedCorrections(Pageable.unpaged(), PAYMENT_ACCOUNT_ID) } returns
                PageImpl(correctionList)

        assertThat(controller.getAvailableCorrections(Pageable.unpaged(), PAYMENT_ACCOUNT_ID))
            .containsExactly(expectedAccountCorrection)
    }

    @Test
    fun selectCorrection() {
        every { selectCorrection.selectCorrection(85L, paymentAccountId = 22L) } returns Unit
        controller.selectCorrectionToPaymentAccount(correctionId = 85L, paymentAccountId = 22L)
        verify(exactly = 1) { selectCorrection.selectCorrection(85L, paymentAccountId = 22L) }
    }

    @Test
    fun deselectCorrection() {
        every { deselectCorrection.deselectCorrection(69L) } returns Unit
        controller.deselectCorrectionFromPaymentAccount(correctionId = 69L)
        verify(exactly = 1) { deselectCorrection.deselectCorrection(69L) }
    }

    @Test
    fun updateLinkedCorrection() {
        every { updateLinkedCorrection.updateCorrection(CORRECTION_ID, correctionUpdate.toModel()) } returns paymentToEcExtensionModel
        controller.updateLinkedCorrection(CORRECTION_ID, correctionUpdate)
        verify(exactly = 1) { updateLinkedCorrection.updateCorrection(CORRECTION_ID, correctionUpdate.toModel()) }
    }

    @Test
    fun getCurrentOverview() {
        every { getCurrentOverview.getCurrentOverview(PAYMENT_ACCOUNT_ID) } returns paymentAccountCurrentOverviewSummary
        val summaryLineDTO = controller.getCurrentOverview(PAYMENT_ACCOUNT_ID)
        verify(exactly = 1) { getCurrentOverview.getCurrentOverview(PAYMENT_ACCOUNT_ID)}
        assertThat(summaryLineDTO).isEqualTo(paymentAccountCurrentOverviewSummaryDTO)
    }
}
