package io.cloudflight.jems.server.project.repository.auditAndControl

import com.querydsl.core.Tuple
import com.querydsl.core.types.Expression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionFinanceEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.Optional

class AuditControlPersistenceProviderTest: UnitTest() {

    companion object {
        private val start = ZonedDateTime.now().minusDays(1)
        private val end = ZonedDateTime.now().plusDays(1)
        private val final = ZonedDateTime.now().plusWeeks(1)

        val expectedAudit = AuditControl(
            id = 0L,
            number = 14,
            projectId = 75L,
            projectCustomIdentifier = "ID75",
            projectAcronym = "PROJ-75",
            status = AuditControlStatus.Ongoing,
            controllingBody = ControllingBody.RegionalApprobationBody,
            controlType = AuditControlType.Administrative,
            startDate = start,
            endDate = end,
            finalReportDate = final,
            totalControlledAmount = BigDecimal.valueOf(745L),
            totalCorrectionsAmount = BigDecimal.ZERO,
            existsOngoing = false,
            existsClosed = false,
            comment = "dummy comment",
        )

        fun dummyEntity(id: Long): AuditControlEntity {
            val project = mockk<ProjectEntity>()
            every { project.id } returns 78L
            every { project.customIdentifier } returns "ID78"
            every { project.acronym } returns "PROJ-78"
            return AuditControlEntity(
                id = id,
                project = project,
                number = 11,
                status = AuditControlStatus.Closed,
                controllingBody = ControllingBody.NationalApprobationBody,
                controlType = AuditControlType.Administrative,
                startDate = start,
                endDate = end,
                finalReportDate = final,
                totalControlledAmount = BigDecimal.valueOf(9586L),
                comment = "new comment",
            )
        }

        fun expectedAuditAfterUpdate(id: Long, total: BigDecimal) = AuditControl(
            id = id,
            number = 11,
            projectId = 78L,
            projectCustomIdentifier = "ID78",
            projectAcronym = "PROJ-78",
            status = AuditControlStatus.Closed,
            controllingBody = ControllingBody.NationalApprobationBody,
            controlType = AuditControlType.Administrative,
            startDate = start,
            endDate = end,
            finalReportDate = final,
            totalControlledAmount = BigDecimal.valueOf(9586L),
            totalCorrectionsAmount = total,
            existsOngoing = true,
            existsClosed = false,
            comment = "new comment",
        )

        private val toCreate = AuditControlCreate(
            number = 14,
            status = AuditControlStatus.Ongoing,
            controllingBody = ControllingBody.RegionalApprobationBody,
            controlType = AuditControlType.Administrative,
            startDate = start,
            endDate = end,
            finalReportDate = final,
            totalControlledAmount = BigDecimal.valueOf(745L),
            comment = "dummy comment",
        )

    }

    @MockK
    private lateinit var projectRepository: ProjectRepository

    @MockK
    private lateinit var auditControlRepository: AuditControlRepository

    @MockK
    private lateinit var jpaQueryFactory: JPAQueryFactory

    @InjectMockKs
    private lateinit var persistence: AuditControlPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(projectRepository, auditControlRepository, jpaQueryFactory)
    }

    @Test
    fun getProjectIdForAuditControl() {
        val entity = mockk<AuditControlEntity>()
        every { entity.project.id } returns 65L
        every { auditControlRepository.getReferenceById(47L) } returns entity
        assertThat(persistence.getProjectIdForAuditControl(47L)).isEqualTo(65L)
    }

    @Test
    fun createControl() {
        val project = mockk<ProjectEntity>()
        every { project.id } returns 75L
        every { project.customIdentifier } returns "ID75"
        every { project.acronym } returns "PROJ-75"
        every { projectRepository.getReferenceById(75L) } returns project

        val created = slot<AuditControlEntity>()
        every { auditControlRepository.save(capture(created)) } returnsArgument 0

        assertThat(persistence.createControl(75L, toCreate)).isEqualTo(expectedAudit)
        assertThat(created.captured.project).isEqualTo(project)
        assertThat(created.captured.number).isEqualTo(14)
        assertThat(created.captured.status).isEqualTo(AuditControlStatus.Ongoing)
        assertThat(created.captured.controllingBody).isEqualTo(ControllingBody.RegionalApprobationBody)
        assertThat(created.captured.controlType).isEqualTo(AuditControlType.Administrative)
        assertThat(created.captured.startDate).isEqualTo(start)
        assertThat(created.captured.endDate).isEqualTo(end)
        assertThat(created.captured.finalReportDate).isEqualTo(final)
        assertThat(created.captured.totalControlledAmount).isEqualTo(BigDecimal.valueOf(745L))
        assertThat(created.captured.comment).isEqualTo("dummy comment")
    }

    @Test
    fun updateControl() {
        val project = mockk<ProjectEntity>()
        every { project.id } returns 78L
        every { project.customIdentifier } returns "ID78"
        every { project.acronym } returns "PROJ-78"
        mockGetTotalCorrectionsAmount(auditControlId = 42L, total = BigDecimal.valueOf(3475L))

        val toUpdateEntity = AuditControlEntity(
            id = 42L,
            project = project,
            number = 11,
            status = AuditControlStatus.Closed,
            controllingBody = ControllingBody.GoA,
            controlType = AuditControlType.OnTheSpot,
            startDate = start.plusYears(1),
            endDate = end.plusYears(1),
            finalReportDate = final.plusYears(1),
            totalControlledAmount = BigDecimal.valueOf(396L),
            comment = "old comment",
        )
        every { auditControlRepository.getReferenceById(42L) } returns toUpdateEntity

        val toUpdate = AuditControlUpdate(
            controllingBody = ControllingBody.NationalApprobationBody,
            controlType = AuditControlType.Administrative,
            startDate = start,
            endDate = end,
            finalReportDate = final,
            totalControlledAmount = BigDecimal.valueOf(9586L),
            comment = "new comment",
        )
        assertThat(persistence.updateControl(42L, toUpdate))
            .isEqualTo(expectedAuditAfterUpdate(id = 42L, total = BigDecimal.valueOf(3475L)))

        assertThat(toUpdateEntity.status).isEqualTo(AuditControlStatus.Closed)
        assertThat(toUpdateEntity.controllingBody).isEqualTo(ControllingBody.NationalApprobationBody)
        assertThat(toUpdateEntity.controlType).isEqualTo(AuditControlType.Administrative)
        assertThat(toUpdateEntity.startDate).isEqualTo(start)
        assertThat(toUpdateEntity.endDate).isEqualTo(end)
        assertThat(toUpdateEntity.finalReportDate).isEqualTo(final)
        assertThat(toUpdateEntity.totalControlledAmount).isEqualTo(BigDecimal.valueOf(9586L))
        assertThat(toUpdateEntity.comment).isEqualTo("new comment")
    }

    @Test
    fun getById() {
        every { auditControlRepository.getReferenceById(51L) } returns dummyEntity(51L)
        mockGetTotalCorrectionsAmount(auditControlId = 51L, total = BigDecimal.valueOf(3475L))
        assertThat(persistence.getById(51L))
            .isEqualTo(expectedAuditAfterUpdate(id = 51L, total = BigDecimal.valueOf(3475L)))
    }

    @Test
    fun findAllProjectAudits() {
        every { auditControlRepository.findAllByProjectId(84L, Pageable.unpaged()) } returns PageImpl(listOf(dummyEntity(84L)))
        mockGetTotalCorrectionsAmount(auditControlId = 84L, total = BigDecimal.valueOf(3475L))
        assertThat(persistence.findAllProjectAudits(84L, Pageable.unpaged()))
            .containsExactly(expectedAuditAfterUpdate(id = 84L, total = BigDecimal.valueOf(3475L)))
    }

    @Test
    fun updateAuditControlStatus() {
        val entity = dummyEntity(15L)
        assertThat(entity.status).isNotEqualTo(AuditControlStatus.Ongoing)
        every { auditControlRepository.findById(15L) } returns Optional.of(entity)
        mockGetTotalCorrectionsAmount(auditControlId = 15L, total = BigDecimal.valueOf(3218L))

        assertThat(persistence.updateAuditControlStatus(15L, AuditControlStatus.Ongoing))
            .isEqualTo(expectedAuditAfterUpdate(id= 15L, BigDecimal.valueOf(3218L)).copy(status = AuditControlStatus.Ongoing))
        assertThat(entity.status).isEqualTo(AuditControlStatus.Ongoing)
    }

    @Test
    fun countAuditsForProject() {
        every { auditControlRepository.countAllByProjectId(977L) } returns 45
        assertThat(persistence.countAuditsForProject(977L)).isEqualTo(45)
    }

    private fun mockGetTotalCorrectionsAmount(auditControlId: Long, total: BigDecimal) {
        val financeSpec = QAuditControlCorrectionFinanceEntity.auditControlCorrectionFinanceEntity
        val query = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any(), any(), any(), any()) } returns query
        every { query.from(financeSpec) } returns query
        every { query.where(financeSpec.correction.auditControl.id.eq(auditControlId)) } returns query
        every { query.groupBy(financeSpec.correction.auditControl.id) } returns query
        val tuple = mockk<Tuple>()
        every { query.fetch() } returns listOf(tuple)
        every { tuple.get(0, Long::class.java) } returns auditControlId
        every { tuple.get(1, BigDecimal::class.java) } returns total
        every { tuple.get(4, Int::class.java) } returns 2
        every { tuple.get(2, Int::class.java) } returns 2
        every { tuple.get(3, Int::class.java) } returns 0
    }

}
