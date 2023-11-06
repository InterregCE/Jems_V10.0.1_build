package io.cloudflight.jems.server.project.service.auditAndControl.correction.deleteProjectAuditCorrection

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
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit.UpdateProjectAuditTest
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal

class DeleteProjectAuditCorrectionTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 1L

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
            id = CORRECTION_ID,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 2,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
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

    @InjectMockKs
    lateinit var deleteProjectAuditControlCorrection: DeleteProjectAuditControlCorrection

    @BeforeEach
    fun setup() {
        clearMocks(correctionPersistence, auditControlPersistence, projectPersistence)
    }
    @Test
    fun `deleteProjectAuditCorrectionTest - correction should be deleted`() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl(AuditStatus.Ongoing)
        every { correctionPersistence.getLastCorrectionOngoingId(AUDIT_CONTROL_ID) } returns 1L
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { correctionPersistence.getByCorrectionId(CORRECTION_ID) } returns correction
        every { correctionPersistence.deleteCorrectionById(CORRECTION_ID) } returns Unit


        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit


        deleteProjectAuditControlCorrection.deleteProjectAuditCorrection(
            PROJECT_ID, AUDIT_CONTROL_ID, CORRECTION_ID
        )

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CORRECTION_IS_DELETED,
                project = AuditProject(
                    id = PROJECT_ID.toString(),
                    customIdentifier = "test",
                    name = "test",
                ),
                description = "Correction AC1.2 for Audit/control number 01_AC_1 is deleted."
            )
        )

    }

    @Test
    fun `deleteProjectAuditCorrectionTest - audit control is closed exception`() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl(AuditStatus.Closed)
        every { correctionPersistence.getLastCorrectionOngoingId(AUDIT_CONTROL_ID) } returns CORRECTION_ID
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { correctionPersistence.getByCorrectionId(1) } returns correction
        every { correctionPersistence.deleteCorrectionById(1) } returns Unit


        assertThrows<AuditControlNotOngoingException> { deleteProjectAuditControlCorrection.deleteProjectAuditCorrection(
            PROJECT_ID, AUDIT_CONTROL_ID, 1) }
    }

    @Test
    fun `deleteProjectAuditCorrectionTest - no saved corrections exception`() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl(AuditStatus.Ongoing)
        every { correctionPersistence.getLastCorrectionOngoingId(AUDIT_CONTROL_ID) } returns null
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { correctionPersistence.getByCorrectionId(1) } returns correction
        every { correctionPersistence.deleteCorrectionById(1) } returns Unit


        assertThrows<AuditControlNoCorrectionSavedException> { deleteProjectAuditControlCorrection.deleteProjectAuditCorrection(
            PROJECT_ID, AUDIT_CONTROL_ID, 1) }
    }

    @Test
    fun `deleteProjectAuditCorrectionTest - correction is not last`() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl(AuditStatus.Ongoing)
        every { correctionPersistence.getLastCorrectionOngoingId(AUDIT_CONTROL_ID) } returns 2
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { correctionPersistence.getByCorrectionId(1) } returns correction
        every { correctionPersistence.deleteCorrectionById(1) } returns Unit


        assertThrows<AuditControlIsNotLastCorrectionException> { deleteProjectAuditControlCorrection.deleteProjectAuditCorrection(
            PROJECT_ID, AUDIT_CONTROL_ID, 1) }
    }
}
