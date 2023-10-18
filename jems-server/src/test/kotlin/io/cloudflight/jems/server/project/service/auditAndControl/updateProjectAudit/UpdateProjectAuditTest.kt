package io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit

import io.cloudflight.jems.server.UnitTest
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
import java.math.BigDecimal
import java.time.ZonedDateTime

class UpdateProjectAuditTest: UnitTest() {


    companion object {
        private const val PROJECT_ID = 49L
        private const val AUDIT_CONTROL_ID = 1L
        val DATE = ZonedDateTime.now()


        val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 1L,
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED
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

    @InjectMockKs
    lateinit var updateProjectAudit: UpdateProjectAuditControl

    @Test
    fun `updateAudit`() {

        val expectedAuditControl = ProjectAuditControl(
            id = 1,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "01",
            status = AuditStatus.Ongoing,
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.OnTheSpot,
            startDate = DATE.minusDays(1),
            endDate = DATE.plusDays(1),
            finalReportDate = DATE.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(300000),
            totalCorrectionsAmount = BigDecimal.ZERO,
            comment = "updated audit"
        )

        every { projectAuditAndControlValidator.validateData(any()) } just Runs
        every { auditControlPersistence.getByIdAndProjectId(auditControlId = AUDIT_CONTROL_ID, projectId = PROJECT_ID) } returns auditControl

        val auditControlSlot = slot<ProjectAuditControl>()
        every { auditControlPersistence.saveAuditControl(capture(auditControlSlot)) } returns expectedAuditControl


        val  update = ProjectAuditControlUpdate(
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.OnTheSpot,
            startDate = DATE.minusDays(1),
            endDate = DATE.plusDays(1),
            finalReportDate = DATE.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(300000),
            comment = "updated audit"
        )
       assertThat(updateProjectAudit.updateAudit(projectId =  PROJECT_ID, auditControlId = AUDIT_CONTROL_ID, update)).isEqualTo(expectedAuditControl)
    }
}
