package io.cloudflight.jems.server.project.service.auditAndControl.listProjectAudits

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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class ListProjectAuditsTest: UnitTest() {

    companion object {

        private const val PROJECT_ID = 49L

        val auditControls = listOf(
            ProjectAuditControl(
                id = 1,
                number = 1,
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

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistenceProvider

    @InjectMockKs
    lateinit var listProjectAudits: ListProjectAudits


    @Test
    fun listProjectAudits() {
        every { auditControlPersistence.findAllProjectAudits(PROJECT_ID, Pageable.unpaged()) } returns PageImpl(auditControls)

        assertThat(listProjectAudits.listForProject(PROJECT_ID, Pageable.unpaged())).containsExactly(
            ProjectAuditControl(
                id = 1,
                number = 1,
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