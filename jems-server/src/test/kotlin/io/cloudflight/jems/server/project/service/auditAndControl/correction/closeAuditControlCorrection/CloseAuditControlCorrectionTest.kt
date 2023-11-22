package io.cloudflight.jems.server.project.service.auditAndControl.correction.closeAuditControlCorrection

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.base.updateAuditControl.UpdateAuditControlTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
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
        private const val CORRECTION_ID = 3L

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
            comment = null
        )

        private fun correctionIdentification(
            status: AuditControlStatus,
            reportId: Long?,
            programmeFundId: Long?,
            id: Long = AUDIT_CONTROL_ID,
        ): AuditControlCorrectionDetail {
            val correction = mockk<AuditControlCorrectionDetail>()
            every { correction.status } returns status
            every { correction.auditControlId } returns id
            every { correction.partnerReportId } returns reportId
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

    @InjectMockKs
    lateinit var closeProjectAuditControlCorrection: CloseAuditControlCorrection

    @Test
    fun closeCorrection() {
        val auditControlId = 17L
        val correctionId = 170L
        every { auditControlCorrectionPersistence.getByCorrectionId(correctionId) } returns
                correctionIdentification(AuditControlStatus.Ongoing, reportId = 50L, programmeFundId = 60L, id = auditControlId)
        every { auditControlPersistence.getById(auditControlId) } returns
                projectAuditControl(AuditControlStatus.Ongoing).copy(id = auditControlId)

        every { auditControlCorrectionPersistence.closeCorrection(correctionId) } returns mockk {
            every { orderNr } returns 4
            every { status } returns AuditControlStatus.Closed
        }

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        assertThat(closeProjectAuditControlCorrection.closeCorrection(correctionId))
            .isEqualTo(AuditControlStatus.Closed)

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CORRECTION_IS_CLOSED,
                project = AuditProject(id = "2", customIdentifier = "01", name = "01 acr"),
                entityRelatedId = auditControlId,
                description = "Correction AC1.4 for Audit/Control number 01_AC_1 is closed."
            )
        )
    }

    @Test
    fun `closeCorrection - correction is already closed exception`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(CORRECTION_ID) } returns
                correctionIdentification(AuditControlStatus.Closed, null, null)
        every { auditControlPersistence.getById(AUDIT_CONTROL_ID) } returns
                projectAuditControl(AuditControlStatus.Ongoing)

        assertThrows<AuditControlCorrectionClosedException> {
            closeProjectAuditControlCorrection.closeCorrection(CORRECTION_ID)
        }
    }

    @Test
    fun `closeCorrection - audit control is closed exception`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(CORRECTION_ID) } returns
                correctionIdentification(AuditControlStatus.Ongoing, null, null)
        every { auditControlPersistence.getById(AUDIT_CONTROL_ID) } returns
                projectAuditControl(AuditControlStatus.Closed)

        assertThrows<AuditControlClosedException> {
            closeProjectAuditControlCorrection.closeCorrection(CORRECTION_ID)
        }
    }

    @ParameterizedTest
    @CsvSource(value = [
        "true,false",
        "false,true",
        "false,false",
    ])
    fun `closeCorrection - report and_or fund not selected yet`(reportSelected: Boolean, fundSelected: Boolean) {
        val reportId = if (reportSelected) 70L else null
        val fundId = if (fundSelected) 80L else null
        every { auditControlCorrectionPersistence.getByCorrectionId(CORRECTION_ID) } returns
                correctionIdentification(AuditControlStatus.Ongoing, reportId, fundId)
        every { auditControlPersistence.getById(AUDIT_CONTROL_ID) } returns
                projectAuditControl(AuditControlStatus.Ongoing)

        assertThrows<PartnerOrReportOrFundNotSelectedException> {
            closeProjectAuditControlCorrection.closeCorrection(CORRECTION_ID)
        }
    }

}
