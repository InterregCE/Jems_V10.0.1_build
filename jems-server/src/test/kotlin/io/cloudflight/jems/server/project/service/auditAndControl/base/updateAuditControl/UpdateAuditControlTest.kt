package io.cloudflight.jems.server.project.service.auditAndControl.base.updateAuditControl

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.validator.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate
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

class UpdateAuditControlTest: UnitTest() {


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


        val auditControl = AuditControl(
            id = 1,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "01",
            projectAcronym = "01 Acr",
            status = AuditControlStatus.Ongoing,
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
    lateinit var updateProjectAudit: UpdateAuditControl

    @Test
    fun updateAudit() {

        val expectedAuditControl = AuditControl(
            id = 1,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "01",
            projectAcronym = "01 Acr",
            status = AuditControlStatus.Ongoing,
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
        every { auditControlPersistence.getById(auditControlId = AUDIT_CONTROL_ID) } returns auditControl

        val auditControlSlot = slot<AuditControlUpdate>()
        every { auditControlPersistence.updateControl(AUDIT_CONTROL_ID, capture(auditControlSlot)) } returns expectedAuditControl

        val  update = AuditControlUpdate(
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.OnTheSpot,
            startDate = DATE.minusDays(1),
            endDate = DATE.plusDays(1),
            finalReportDate = DATE.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(300000),
            comment = "updated audit"
        )
        assertThat(updateProjectAudit.updateAudit(auditControlId = AUDIT_CONTROL_ID, update)).isEqualTo(expectedAuditControl)
        assertThat(auditControlSlot.captured).isEqualTo(update)
    }

}
