package io.cloudflight.jems.server.project.repository.contracting.reporting

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ContractingReportingPersistenceProviderTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 2756L

        private fun entity(id: Long) = ProjectContractingReportingEntity(
            id = id,
            project = mockk(),
            type = ContractingDeadlineType.Finance,
            periodNumber = id.toInt(),
            deadline = LocalDate.of(2022, 8, 9),
            comment = "dummy comment",
            number = 1,
            finalReport = false,
        )

        private fun entityNew(id: Long) = ProjectContractingReportingEntity(
            id = id,
            project = mockk(),
            type = ContractingDeadlineType.Content,
            periodNumber = id.toInt() + 100,
            deadline = LocalDate.of(2023, 2, 25),
            comment = "dummy comment new",
            number = 1,
            finalReport = false,
        )

        private val model10 = ProjectContractingReportingSchedule(
            id = 10L,
            type = ContractingDeadlineType.Finance,
            periodNumber = 10,
            date = LocalDate.of(2022, 8, 9),
            comment = "dummy comment",
            number = 1,
            linkedSubmittedProjectReportNumbers = setOf(),
            linkedDraftProjectReportNumbers = setOf(),
            finalReport = false,
        )

        private fun modelNew(id: Long) = ProjectContractingReportingSchedule(
            id = id,
            type = ContractingDeadlineType.Content,
            periodNumber = id.toInt() + 100,
            date = LocalDate.of(2023, 2, 25),
            comment = "dummy comment new",
            number = 1,
            linkedSubmittedProjectReportNumbers = setOf(),
            linkedDraftProjectReportNumbers = setOf(),
            finalReport = false,
        )
    }

    @MockK
    lateinit var projectContractingReportingRepository: ProjectContractingReportingRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var projectReportRepository: ProjectReportRepository

    @InjectMockKs
    lateinit var persistence: ContractingReportingPersistenceProvider


    @BeforeEach
    fun reset() {
        clearMocks(projectContractingReportingRepository, projectRepository)
    }

    @Test
    fun getContractingReporting() {
        every { projectContractingReportingRepository.findTop50ByProjectIdOrderByDeadline(PROJECT_ID) } returns
            mutableListOf(entity(10L))
        assertThat(persistence.getContractingReporting(PROJECT_ID)).containsExactly(model10)
    }

    @Test
    fun getContractingReportingDeadline() {
        every { projectContractingReportingRepository.findByProjectIdAndId(PROJECT_ID, 10L) } returns entity(10L)
        every { projectReportRepository.findAllByProjectIdAndDeadlineId(PROJECT_ID, 10L) } returns listOf()

        assertThat(persistence.getContractingReportingDeadline(PROJECT_ID, 10L)).isEqualTo(model10)
    }

    @Test
    fun updateContractingReporting() {
        every { projectContractingReportingRepository.findTop50ByProjectIdOrderByDeadline(PROJECT_ID) } returnsMany listOf(
            // before update
            mutableListOf(entity(15L), entity(16L)),
            // after update
            mutableListOf(entityNew(16L), entityNew(0L /* created */)),
        )
        val deleted = slot<Iterable<ProjectContractingReportingEntity>>()
        every { projectContractingReportingRepository.deleteAll(capture(deleted)) } answers { }

        val created = mutableListOf<ProjectContractingReportingEntity>()
        every { projectContractingReportingRepository.save(capture(created)) } returnsArgument 0
        every { projectReportRepository.findAllByProjectIdAndDeadlineNotNull(PROJECT_ID) } returns listOf()

        val deadlines = listOf(
            modelNew(16L) /* to be updated */,
            modelNew(0L) /* to be created */,
        )

        assertThat(persistence.updateContractingReporting(PROJECT_ID, deadlines)).containsExactlyElementsOf(deadlines)

        assertThat(deleted.captured.map { it.id }).containsExactly(15L)
        assertThat(created).hasSize(1)
        with(created.first()) {
            assertThat(type).isEqualTo(ContractingDeadlineType.Content)
            assertThat(periodNumber).isEqualTo(100)
            assertThat(deadline).isEqualTo(LocalDate.of(2023, 2, 25))
            assertThat(comment).isEqualTo("dummy comment new")
        }
        verify(exactly = 0) { projectRepository.getReferenceById(any()) }
    }

    @Test
    fun `updateContractingReporting - first creation`() {
        every { projectContractingReportingRepository.findTop50ByProjectIdOrderByDeadline(PROJECT_ID) } returnsMany listOf(
            // before update
            mutableListOf(),
            // after update
            mutableListOf(entityNew(0L /* created */)),
        )
        val deleted = slot<Iterable<ProjectContractingReportingEntity>>()
        every { projectContractingReportingRepository.deleteAll(capture(deleted)) } answers { }

        val created = mutableListOf<ProjectContractingReportingEntity>()
        every { projectContractingReportingRepository.save(capture(created)) } returnsArgument 0

        every { projectRepository.getReferenceById(PROJECT_ID) } returns mockk()

        val deadlines = listOf(
            modelNew(0L) /* to be created */,
        )

        assertThat(persistence.updateContractingReporting(PROJECT_ID, deadlines)).containsExactlyElementsOf(deadlines)

        assertThat(deleted.captured).isEmpty()
        assertThat(created).hasSize(1)
    }

    @Test
    fun `getScheduleIdsWhosePeriodsAndDatesNotProper`() {
        val maxDuration = 2;
        val deadlines = mutableListOf(entity(1L), entity(5L))
        every { projectContractingReportingRepository.findAllByProjectIdAndPeriodNumberGreaterThan(PROJECT_ID, maxDuration) } returns
            deadlines.filter { s -> s.periodNumber!! > maxDuration } as MutableList<ProjectContractingReportingEntity>

        assertThat(persistence.getScheduleIdsWhosePeriodsAndDatesNotProper(PROJECT_ID, maxDuration)).hasSize(1)
    }

}
