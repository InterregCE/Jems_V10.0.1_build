package io.cloudflight.jems.server.project.repository.auditAndControl

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuditControlPersistenceProviderTest: UnitTest() {


    companion object {
        /*
        const val PROJECT_ID = 49L
        const val AUDIT_CONTROL_ID = 1L
        val DATE = ZonedDateTime.now()


        val auditControlEntity =  AuditControlEntity(
            id = 1,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "01",
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

        val auditControl  = AuditControl(
            id = 0L,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "01",
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

        val expectedAudit = AuditControl(
            id = 1L,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "01",
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
        */
    }

    @MockK
    private lateinit var projectRepository: ProjectRepository

    @MockK
    private lateinit var auditControlRepository: AuditControlRepository

    @InjectMockKs
    private lateinit var auditControlPersistenceProvider: AuditControlPersistenceProvider

    @Test
    fun getProjectIdForAuditControl() {
        val entity = mockk<AuditControlEntity>()
        every { entity.project.id } returns 65L
        every { auditControlRepository.getById(47L) } returns entity
        assertThat(auditControlPersistenceProvider.getProjectIdForAuditControl(47L)).isEqualTo(65L)
    }

    /*
    @Test
    fun `project audit is saved and mapped`() {
        val slot = slot<AuditControlEntity>()
        every {  auditControlRepository.save(capture(slot)) } returns auditControlEntity
        assertThat(auditControlPersistenceProvider.createControl(auditControl)).isEqualTo(expectedAudit)
        assertThat(slot.captured).usingRecursiveComparison().isEqualTo(
            AuditControlEntity(
                id = 0L,
                number = 1,
                projectId = PROJECT_ID,
                projectCustomIdentifier = "01",
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
        )
    }

    @Test
    fun `project audit is found and mapped`() {
        every {
            auditControlRepository.getByIdAndProjectId(
                auditControlId = AUDIT_CONTROL_ID,
                projectId = PROJECT_ID
            )
        } returns auditControlEntity

        assertThat(auditControlPersistenceProvider.getByIdAndProjectId(
            auditControlId = AUDIT_CONTROL_ID,
            projectId = PROJECT_ID )).isEqualTo(expectedAudit)
    }

    @Test
    fun `project audits are found and mapped`() {
        every {
            auditControlRepository.findAllByProjectId(projectId = PROJECT_ID, Pageable.unpaged())
        } returns PageImpl(listOf(auditControlEntity))

        assertThat(auditControlPersistenceProvider.findAllProjectAudits(projectId = PROJECT_ID, Pageable.unpaged())).containsExactly(
            expectedAudit
        )
    }
    */

}
