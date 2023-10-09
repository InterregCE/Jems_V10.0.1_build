package io.cloudflight.jems.server.project.service.auditAndControl.createProjectAudit

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControlUpdate
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime

class CreateProjectAuditControlTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 49L
        val DATE = ZonedDateTime.now()


        val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 1L,
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED
        )

        val auditControlData =  ProjectAuditControlUpdate(
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.Administrative,
            startDate = DATE.minusDays(1),
            endDate = DATE.plusDays(1),
            finalReportDate = DATE.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(10000),
            comment = null
        )

        val auditControl = ProjectAuditControl(
            id = 1,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "01",
            status = AuditStatus.Ongoing,
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.Administrative,
            startDate = DATE.minusDays(1),
            endDate = DATE.plusDays(1),
            finalReportDate = DATE.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(10000),
            totalCorrectionsAmount = BigDecimal.ZERO,
            comment = null
        )
    }


    @MockK
    lateinit var auditControlPersistence: AuditControlPersistenceProvider

    @MockK
    lateinit var projectAuditAndControlValidator: ProjectAuditAndControlValidator

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var projectPersistenceProvider: ProjectPersistenceProvider

    @InjectMockKs
    lateinit var createProjectAuditControl: CreateProjectAuditControl



    @Test
    fun `audit control is created successfully`() {
        every { auditControlPersistence.countAuditsForProject(PROJECT_ID) } returns 3L
        every { projectAuditAndControlValidator.validateMaxNumberOfAudits(3L)} just Runs
        every { projectAuditAndControlValidator.validateData(auditControlData) } just Runs

        every { projectPersistenceProvider.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { auditControlPersistence.saveAuditControl(any()) } returns auditControl

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        assertThat(createProjectAuditControl.createAudit(PROJECT_ID, auditControlData)).isEqualTo(
            ProjectAuditControl(
                id = 1,
                number = 1,
                projectId = PROJECT_ID,
                projectCustomIdentifier = "01",
                status = AuditStatus.Ongoing,
                controllingBody = ControllingBody.OLAF,
                controlType = AuditControlType.Administrative,
                startDate = DATE.minusDays(1),
                endDate = DATE.plusDays(1),
                finalReportDate = DATE.minusDays(5),
                totalControlledAmount = BigDecimal.valueOf(10000),
                totalCorrectionsAmount = BigDecimal.ZERO,
                comment = null
            )
        )
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_AUDIT_CONTROL_IS_CREATED,
                project = AuditProject(
                    id = PROJECT_ID.toString(),
                    customIdentifier = "01",
                    name = "project acronym",
                ),
                description = "Audit/control 01_AC_1 is created"
            )
        )
    }

}