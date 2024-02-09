package io.cloudflight.jems.server.project.service.auditAndControl.correction.closeAuditControlCorrection

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.service.account.finance.correction.PaymentAccountCorrectionLinkingPersistence
import io.cloudflight.jems.server.payments.service.ecPayment.linkToCorrection.EcPaymentCorrectionLinkPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.base.updateAuditControl.UpdateAuditControlTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.AuditControlCorrectionFinancePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario.SCENARIO_4
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5
import io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.AuditControlCorrectionMeasurePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.CorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal

class CloseAuditControlCorrectionTest : UnitTest() {
    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 170L

        private fun programmeMeasureModel(scenario: ProjectCorrectionProgrammeMeasureScenario) = ProjectCorrectionProgrammeMeasure(
            correctionId = CORRECTION_ID,
            scenario = scenario,
            comment = "comment",
            includedInAccountingYear = null,
        )

        private val financialDescription = ProjectCorrectionFinancialDescription(
            correctionId =  CORRECTION_ID,
            deduction = true,
            fundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ONE,
            privateContribution = BigDecimal.ZERO,
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "sample comment",
            correctionType = CorrectionType.Ref1Dot15,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = null
        )

        private fun projectAuditControl(auditStatus: AuditControlStatus) = AuditControl(
            id = AUDIT_CONTROL_ID,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "01",
            projectAcronym = "01 acr",
            status = auditStatus,
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.Administrative,
            startDate = UpdateAuditControlTest.DATE.minusDays(1),
            endDate = UpdateAuditControlTest.DATE.plusDays(1),
            finalReportDate = UpdateAuditControlTest.DATE.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(10000),
            totalCorrectionsAmount = BigDecimal.ZERO,
            existsOngoing = true,
            existsClosed = true,
            comment = null
        )

        private fun correctionIdentification(
            status: AuditControlStatus,
            reportId: Long?,
            lumpSumOrderNr: Int?,
            programmeFundId: Long?,
            id: Long = AUDIT_CONTROL_ID,
        ): AuditControlCorrectionDetail {
            val correction = mockk<AuditControlCorrectionDetail>()
            every { correction.status } returns status
            every { correction.auditControlId } returns id
            every { correction.partnerReportId } returns reportId
            every { correction.lumpSumOrderNr } returns lumpSumOrderNr
            every { correction.programmeFundId } returns programmeFundId
            return correction
        }

    }

    @MockK
    private lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    private lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var ecPaymentCorrectionExtensionLinkingPersistence: EcPaymentCorrectionLinkPersistence

    @MockK
   private lateinit var paymentAccountCorrectionExtensionLinkingPersistence: PaymentAccountCorrectionLinkingPersistence

    @MockK
    private lateinit var auditControlCorrectionFinancePersistence: AuditControlCorrectionFinancePersistence

    @MockK
    private lateinit var auditControlCorrectionMeasurePersistence: AuditControlCorrectionMeasurePersistence

    @InjectMockKs
    lateinit var closeProjectAuditControlCorrection: CloseAuditControlCorrection

    @Test
    fun `closeCorrection - linked to EcPayment`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(CORRECTION_ID) } returns
            correctionIdentification(AuditControlStatus.Ongoing, reportId = 50L, lumpSumOrderNr = null, programmeFundId = 60L, id = AUDIT_CONTROL_ID)
        every { auditControlPersistence.getById(AUDIT_CONTROL_ID) } returns
                projectAuditControl(AuditControlStatus.Ongoing).copy(id = AUDIT_CONTROL_ID)
        every { auditControlCorrectionMeasurePersistence.getProgrammeMeasure(CORRECTION_ID) } returns programmeMeasureModel(SCENARIO_5)
        every { auditControlCorrectionFinancePersistence.getCorrectionFinancialDescription(CORRECTION_ID) } returns financialDescription
        every { ecPaymentCorrectionExtensionLinkingPersistence.createCorrectionExtension(
            financialDescription, BigDecimal.valueOf(11), BigDecimal.ZERO
        ) } returns Unit

        every { auditControlCorrectionPersistence.closeCorrection(CORRECTION_ID) } returns mockk {
            every { orderNr } returns 4
            every { status } returns AuditControlStatus.Closed
        }

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        assertThat(closeProjectAuditControlCorrection.closeCorrection(CORRECTION_ID))
            .isEqualTo(AuditControlStatus.Closed)

        verify(exactly = 1) { ecPaymentCorrectionExtensionLinkingPersistence.createCorrectionExtension(
            financialDescription, BigDecimal.valueOf(11), BigDecimal.ZERO
        ) }

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CORRECTION_IS_CLOSED,
                project = AuditProject(id = "2", customIdentifier = "01", name = "01 acr"),
                entityRelatedId = AUDIT_CONTROL_ID,
                description = "Correction AC1.4 for Audit/Control number 01_AC_1 is closed."
            )
        )
    }

    @Test
    fun `closeCorrection - linked to PaymentAccount`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(CORRECTION_ID) } returns
                correctionIdentification(AuditControlStatus.Ongoing, reportId = 50L, lumpSumOrderNr = null, programmeFundId = 60L, id = AUDIT_CONTROL_ID)
        every { auditControlPersistence.getById(AUDIT_CONTROL_ID) } returns
                projectAuditControl(AuditControlStatus.Ongoing).copy(id = AUDIT_CONTROL_ID)
        every { auditControlCorrectionMeasurePersistence.getProgrammeMeasure(CORRECTION_ID) } returns programmeMeasureModel(SCENARIO_4)
        every { auditControlCorrectionFinancePersistence.getCorrectionFinancialDescription(CORRECTION_ID) } returns financialDescription
        every { paymentAccountCorrectionExtensionLinkingPersistence.createCorrectionExtension(financialDescription) } returns Unit

        every { auditControlCorrectionPersistence.closeCorrection(CORRECTION_ID) } returns mockk {
            every { orderNr } returns 4
            every { status } returns AuditControlStatus.Closed
        }

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        assertThat(closeProjectAuditControlCorrection.closeCorrection(CORRECTION_ID))
            .isEqualTo(AuditControlStatus.Closed)

        verify(exactly = 1) { paymentAccountCorrectionExtensionLinkingPersistence.createCorrectionExtension(financialDescription) }

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CORRECTION_IS_CLOSED,
                project = AuditProject(id = "2", customIdentifier = "01", name = "01 acr"),
                entityRelatedId = AUDIT_CONTROL_ID,
                description = "Correction AC1.4 for Audit/Control number 01_AC_1 is closed."
            )
        )
    }

    @Test
    fun `closeCorrection - correction is already closed exception`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(CORRECTION_ID) } returns
                correctionIdentification(AuditControlStatus.Closed, null, null, null)
        every { auditControlPersistence.getById(AUDIT_CONTROL_ID) } returns
                projectAuditControl(AuditControlStatus.Ongoing)

        assertThrows<AuditControlCorrectionClosedException> {
            closeProjectAuditControlCorrection.closeCorrection(CORRECTION_ID)
        }
    }

    @Test
    fun `closeCorrection - audit control is closed exception`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(CORRECTION_ID) } returns
                correctionIdentification(AuditControlStatus.Ongoing, null, null, null)
        every { auditControlPersistence.getById(AUDIT_CONTROL_ID) } returns
                projectAuditControl(AuditControlStatus.Closed)

        assertThrows<AuditControlClosedException> {
            closeProjectAuditControlCorrection.closeCorrection(CORRECTION_ID)
        }
    }

    @ParameterizedTest
    @CsvSource(value = [
        "true,true,false",
        "true,false,false",
        "false,true,false",
        "false,false,true",
        "false,false,false",
    ])
    fun `closeCorrection - (report or lumpsum) and_or fund not selected yet`(reportSelected: Boolean, lumpSumSelected: Boolean, fundSelected: Boolean) {
        val reportId = if (reportSelected) 70L else null
        val lumpSumOrderNr = if(lumpSumSelected) 75 else null
        val fundId = if (fundSelected) 80L else null
        every { auditControlCorrectionPersistence.getByCorrectionId(CORRECTION_ID) } returns
                correctionIdentification(AuditControlStatus.Ongoing, reportId, lumpSumOrderNr, fundId)
        every { auditControlPersistence.getById(AUDIT_CONTROL_ID) } returns
                projectAuditControl(AuditControlStatus.Ongoing)

        assertThrows<PartnerOrReportOrFundNotSelectedException> {
            closeProjectAuditControlCorrection.closeCorrection(CORRECTION_ID)
        }
    }

}
