package io.cloudflight.jems.server.payments.controller.applicationToEc

import io.cloudflight.jems.api.payments.dto.PaymentToEcCorrectionLinkingDTO
import io.cloudflight.jems.api.payments.dto.PaymentToEcCorrectionLinkingUpdateDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ControllingBodyDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureScenarioDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.EcPaymentCorrectionExtension
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.deselectCorrection.DeselectCorrectionFromEcInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.getAvailableClosedCorrections.GetAvailableClosedCorrectionsInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.selectCorrection.SelectCorrectionToEcPaymentInteractor
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.updateCorrection.UpdateLinkedCorrectionInteractor
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

class PaymentToEcPaymentCorrectionLinkingControllerTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val EC_PAYMENT_ID = 10L
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
            PaymentToEcCorrectionLinking(
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
        )


        private val expectedCorrectionList = listOf(
            PaymentToEcCorrectionLinkingDTO(
                correction = expectedCorrection,
                projectId = PROJECT_ID,
                projectAcronym = "Acronym",
                projectCustomIdentifier = "Custom Identifier",
                priorityAxis = "PO1",
                controllingBody = ControllingBodyDTO.Controller,
                scenario = ProjectCorrectionProgrammeMeasureScenarioDTO.SCENARIO_2,
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
        )

        private val correctionUpdate = PaymentToEcCorrectionLinkingUpdateDTO(
            correctedPrivateContribution = BigDecimal(205),
            correctedPublicContribution = BigDecimal(206),
            correctedAutoPublicContribution = BigDecimal(207),
            comment = "Updated comment"
        )


        private val paymentToEcExtensionModel = EcPaymentCorrectionExtension(
            correctionId = CORRECTION_ID,
            ecPaymentId = EC_PAYMENT_ID,
            ecPaymentStatus = PaymentEcStatus.Draft,
            auditControlStatus = AuditControlStatus.Ongoing,
            comment = "Comment",
            fundAmount = BigDecimal.valueOf(25.80),
            publicContribution = BigDecimal.valueOf(35.00),
            correctedPublicContribution = BigDecimal.valueOf(36.20),
            autoPublicContribution = BigDecimal.valueOf(15.00),
            correctedAutoPublicContribution = BigDecimal.valueOf(16.00),
            privateContribution = BigDecimal.valueOf(45.00),
            correctedPrivateContribution = BigDecimal.valueOf(46.20),
        )
    }

    @MockK
    lateinit var getAvailableCorrections: GetAvailableClosedCorrectionsInteractor

    @MockK
    lateinit var selectCorrectionToEcPayment: SelectCorrectionToEcPaymentInteractor

    @MockK
    lateinit var deselectCorrectionToEcPayment: DeselectCorrectionFromEcInteractor

    @MockK
    lateinit var updateLinkedCorrection: UpdateLinkedCorrectionInteractor

    @InjectMockKs
    lateinit var controller: CorrectionToEcPaymentLinkingController

    @Test
    fun getAvailableCorrections() {
        every { getAvailableCorrections.getClosedCorrectionList(Pageable.unpaged(), EC_PAYMENT_ID) } returns PageImpl(
            correctionList
        )

        assertThat(controller.getAvailableCorrections(Pageable.unpaged(), EC_PAYMENT_ID).content).isEqualTo(
            expectedCorrectionList)
    }

    @Test
    fun selectCorrectionToEcPayment() {
        every { selectCorrectionToEcPayment.selectCorrectionToEcPayment(85L, ecPaymentId = 22L) } returns Unit
        controller.selectCorrectionToEcPayment(correctionId = 85L, ecApplicationId = 22L)
        verify(exactly = 1) { selectCorrectionToEcPayment.selectCorrectionToEcPayment(85L, ecPaymentId = 22L) }
    }

    @Test
    fun deselectCorrectionFromEcPayment() {
        every { deselectCorrectionToEcPayment.deselectCorrectionFromEcPayment(69L) } returns Unit
        controller.deselectCorrectionFromEcPayment(correctionId = 69L)
        verify(exactly = 1) { deselectCorrectionToEcPayment.deselectCorrectionFromEcPayment(69L) }
    }

    @Test
    fun updateLinkedCorrection() {
        every { updateLinkedCorrection.updateLinkedCorrection(CORRECTION_ID, correctionUpdate.toModel()) } returns paymentToEcExtensionModel
        controller.updateLinkedCorrection(CORRECTION_ID, correctionUpdate)
        verify(exactly = 1) { updateLinkedCorrection.updateLinkedCorrection(CORRECTION_ID, correctionUpdate.toModel()) }
    }
}
