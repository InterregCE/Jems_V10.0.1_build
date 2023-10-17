package io.cloudflight.jems.server.project.service.auditAndControl.correction.createProjectAuditCorrection

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
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

class CreateProjectAuditCorrectionTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L

        private fun projectAuditControl(auditStatus: AuditStatus) =  ProjectAuditControl(
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

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "test",
            callId = 1L,
            callName = "call",
            acronym = "test",
            status = ApplicationStatus.CONTRACTED,
        )

        private val correction = ProjectAuditControlCorrection(
            id = 0,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 2,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
        )
    }

    @MockK
    lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    lateinit var applicationEventPublisher: ApplicationEventPublisher

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var createProjectAuditControlCorrection: CreateProjectAuditControlCorrection

    @Test
    fun createProjectAuditCorrection() {
        val correctionToBeSaved = correction

        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl(AuditStatus.Ongoing)
        every { auditControlCorrectionPersistence.getLastUsedOrderNr(AUDIT_CONTROL_ID) } returns 1
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { auditControlCorrectionPersistence.saveCorrection(correctionToBeSaved) } returns correctionToBeSaved

        val auditSlot = slot<AuditCandidateEvent>()
        every { applicationEventPublisher.publishEvent(capture(auditSlot)) } returns Unit

        assertThat(createProjectAuditControlCorrection.createProjectAuditCorrection(PROJECT_ID, AUDIT_CONTROL_ID, true)).isEqualTo(correctionToBeSaved)
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CORRECTION_IS_CREATED,
                project = AuditProject(
                    id = PROJECT_ID.toString(),
                    customIdentifier = "test",
                    name = "test",
                ),
                description = "Correction AC1.2 for Audit/control number 01_AC_1 is created."
            )
        )
    }

    @Test
    fun `createProjectAuditCorrection - maximum number of corrections exception`() {
        val correctionToBeSaved = correction

        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl(AuditStatus.Ongoing)
        every { auditControlCorrectionPersistence.getLastUsedOrderNr(AUDIT_CONTROL_ID) } returns 100
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { auditControlCorrectionPersistence.saveCorrection(correctionToBeSaved) } returns correctionToBeSaved

        assertThrows<MaximumNumberOfCorrectionsException> {
            createProjectAuditControlCorrection.createProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                true
            )
        }
    }

    @Test
    fun `createProjectAuditCorrection - audit control not ongoing exception`() {
        val correctionToBeSaved = correction

        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl(
            AuditStatus.Closed
        )
        every { auditControlCorrectionPersistence.getLastUsedOrderNr(AUDIT_CONTROL_ID) } returns 1
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { auditControlCorrectionPersistence.saveCorrection(correctionToBeSaved) } returns correctionToBeSaved

        assertThrows<AuditControlIsInStatusClosedException> {
            createProjectAuditControlCorrection.createProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                true
            )
        }
    }
}
