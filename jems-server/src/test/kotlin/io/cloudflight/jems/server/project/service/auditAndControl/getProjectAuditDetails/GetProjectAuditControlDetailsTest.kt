package io.cloudflight.jems.server.project.service.auditAndControl.getProjectAuditDetails

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit.UpdateProjectAuditTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetProjectAuditControlDetailsTest: UnitTest() {

    companion object {

        private const val PROJECT_ID = 49L
        private const val AUDIT_CONTROL_ID = 1L

        val auditControl = ProjectAuditControl(
            id = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "01",
            status = AuditStatus.Ongoing,
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.Administrative,
            startDate = UpdateProjectAuditTest.DATE.minusDays(1),
            endDate = UpdateProjectAuditTest.DATE.plusDays(1),
            finalReportDate = UpdateProjectAuditTest.DATE.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(10000),
            totalCorrectionsAmount = BigDecimal.ZERO,
            comment = null
        )
    }

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistenceProvider

    @InjectMockKs
    lateinit var getProjectAuditControlDetails: GetProjectAuditControlDetails

    @Test
    fun getProjectAuditDetails() {

        every {
            auditControlPersistence.findByIdAndProjectId(
                auditControlId = AUDIT_CONTROL_ID,
                projectId = PROJECT_ID
            )
        } returns auditControl

        assertThat(getProjectAuditControlDetails.getDetails(projectId = PROJECT_ID, auditId = AUDIT_CONTROL_ID)).isEqualTo(
            ProjectAuditControl(
                id = 1,
                projectId = PROJECT_ID,
                projectCustomIdentifier = "01",
                status = AuditStatus.Ongoing,
                controllingBody = ControllingBody.OLAF,
                controlType = AuditControlType.Administrative,
                startDate = UpdateProjectAuditTest.DATE.minusDays(1),
                endDate = UpdateProjectAuditTest.DATE.plusDays(1),
                finalReportDate = UpdateProjectAuditTest.DATE.minusDays(5),
                totalControlledAmount = BigDecimal.valueOf(10000),
                totalCorrectionsAmount = BigDecimal.ZERO,
                comment = null
            )
        )
    }
}