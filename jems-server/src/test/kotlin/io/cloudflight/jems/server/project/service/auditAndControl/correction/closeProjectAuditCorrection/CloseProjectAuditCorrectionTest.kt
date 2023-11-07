package io.cloudflight.jems.server.project.service.auditAndControl.correction.closeProjectAuditCorrection

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.AuditControlNotOngoingException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.ProjectCorrectionIdentificationPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit.UpdateProjectAuditTest
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal

class CloseProjectAuditCorrectionTest : UnitTest() {
    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 3L

        private fun projectAuditControl(auditStatus: AuditStatus) = ProjectAuditControl(
            id = AUDIT_CONTROL_ID,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "01",
            status = auditStatus,
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.Administrative,
            startDate = UpdateProjectAuditTest.DATE.minusDays(1),
            endDate = UpdateProjectAuditTest.DATE.plusDays(1),
            finalReportDate = UpdateProjectAuditTest.DATE.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(10000),
            totalCorrectionsAmount = BigDecimal.ZERO,
            comment = null
        )

        private fun correction(status: CorrectionStatus) = ProjectAuditControlCorrection(
            id = CORRECTION_ID,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 2,
            status = status,
            linkedToInvoice = true,
        )

        private fun correctionIdentification(status: CorrectionStatus, partnerId: Long?, reportId: Long?, programmeFundId: Long?) =
            ProjectCorrectionIdentification(
                correction = correction(status),
                followUpOfCorrectionId = null,
                correctionFollowUpType = CorrectionFollowUpType.No,
                repaymentFrom = null,
                lateRepaymentTo = null,
                partnerId = partnerId,
                partnerReportId = reportId,
                programmeFundId = programmeFundId,
            )

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 1L,
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED,
        )

    }

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var correctionIdentificationPersistence: ProjectCorrectionIdentificationPersistence

    @InjectMockKs
    lateinit var closeProjectAuditControlCorrection: CloseProjectAuditControlCorrection

    @Test
    fun closeProjectAuditCorrection() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl(
            AuditStatus.Ongoing
        )
        every {
            correctionIdentificationPersistence.getCorrectionIdentification(
                correctionId = CORRECTION_ID,
            )
        } returns correctionIdentification(
            status = CorrectionStatus.Ongoing,
            partnerId = 1L,
            reportId = 2L,
            programmeFundId = 3L,
        )
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { correctionPersistence.closeCorrection(CORRECTION_ID) } returns correction(CorrectionStatus.Closed)

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        assertThat(
            closeProjectAuditControlCorrection.closeProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID
            )
        ).isEqualTo(CorrectionStatus.Closed)

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CORRECTION_IS_CLOSED,
                project = AuditProject(
                    id = PROJECT_ID.toString(),
                    customIdentifier = "01",
                    name = "project acronym",
                ),
                description = "Correction AC1.2 for Audit/control number 01_AC_1 is closed."
            )
        )


    }

    @Test
    fun `closeProjectAuditCorrection - correction is already closed exception`() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl(
            AuditStatus.Ongoing
        )
        every {
            correctionIdentificationPersistence.getCorrectionIdentification(
                correctionId = CORRECTION_ID,
            )
        } returns correctionIdentification(
            status = CorrectionStatus.Closed,
            partnerId = 1L,
            reportId = 2L,
            programmeFundId = 3L,
        )
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary

        assertThrows<ProjectCorrectionIsInStatusClosedException> {
            closeProjectAuditControlCorrection.closeProjectAuditCorrection(
                PROJECT_ID, AUDIT_CONTROL_ID, CORRECTION_ID
            )
        }

    }

    @Test
    fun `closeProjectAuditCorrection - audit control is closed exception`() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl(
            AuditStatus.Closed
        )
        every {
            correctionIdentificationPersistence.getCorrectionIdentification(
                correctionId = CORRECTION_ID,
            )
        } returns correctionIdentification(
            status = CorrectionStatus.Ongoing,
            partnerId = 1L,
            reportId = 2L,
            programmeFundId = 3L,
        )
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary

        assertThrows<AuditControlNotOngoingException> {
            closeProjectAuditControlCorrection.closeProjectAuditCorrection(
                PROJECT_ID, AUDIT_CONTROL_ID, CORRECTION_ID
            )
        }
    }

    @Test
    fun `closeProjectAuditCorrection - partner and partner report not selected exception`() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl(
            AuditStatus.Ongoing
        )
        every {
            correctionIdentificationPersistence.getCorrectionIdentification(
                correctionId = CORRECTION_ID,
            )
        } returns correctionIdentification(
            status = CorrectionStatus.Ongoing,
            partnerId = null,
            reportId = null,
            programmeFundId = null,
        )
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary

        assertThrows<PartnerOrReportOrFundNotSelectedException> {
            closeProjectAuditControlCorrection.closeProjectAuditCorrection(
                PROJECT_ID, AUDIT_CONTROL_ID, CORRECTION_ID
            )
        }
    }

}
