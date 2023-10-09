package io.cloudflight.jems.server.project.repository.auditAndControl

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

class AuditControlPersistenceProviderTest: UnitTest() {


    companion object {

        const val PROJECT_ID = 49L
        const val AUDIT_CONTROL_ID = 1L
        val DATE = ZonedDateTime.now()


        val auditControlEntity =  AuditControlEntity(
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

        val auditControl  = ProjectAuditControl(
            id = 0L,
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

        val expectedAudit = ProjectAuditControl(
            id = 1L,
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
    lateinit var auditControlRepository: AuditControlRepository

    @InjectMockKs
    lateinit var auditControlPersistenceProvider: AuditControlPersistenceProvider


    @Test
    fun `project audit is saved and mapped`() {
        val slot = slot<AuditControlEntity>()
        every {  auditControlRepository.save(capture(slot)) } returns auditControlEntity
        assertThat(auditControlPersistenceProvider.saveAuditControl(auditControl)).isEqualTo(expectedAudit)
        assertThat(slot.captured).usingRecursiveComparison().isEqualTo(
            AuditControlEntity(
                id = 0L,
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
    }

    @Test
    fun `project audit is found and mapped`() {
        every {
            auditControlRepository.findByIdAndProjectId(
                auditControlId = AUDIT_CONTROL_ID,
                projectId = PROJECT_ID
            )
        } returns auditControlEntity

        assertThat(auditControlPersistenceProvider.findByIdAndProjectId(
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

}