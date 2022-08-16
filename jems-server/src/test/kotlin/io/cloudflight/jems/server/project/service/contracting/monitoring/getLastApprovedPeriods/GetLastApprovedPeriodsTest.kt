package io.cloudflight.jems.server.project.service.contracting.monitoring.getLastApprovedPeriods

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectPeriodForMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class GetLastApprovedPeriodsTest : UnitTest() {

    companion object {
        private val period = ProjectPeriod(
            number = 3,
            start = 25,
            end = 36,
        )

        private val expectedPeriodWithDates = ProjectPeriodForMonitoring(
            number = 3,
            start = 25,
            end = 36,
            startDate = LocalDate.of(2024, 5, 19),
            endDate = LocalDate.of(2025, 5, 18),
        )

        private val expectedPeriodWithoutDates = ProjectPeriodForMonitoring(
            number = 3,
            start = 25,
            end = 36,
            startDate = null,
            endDate = null,
        )
    }

    @MockK
    lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistence
    @MockK
    lateinit var projectPersistence: ProjectPersistenceProvider
    @MockK
    lateinit var versionPersistence: ProjectVersionPersistence

    @InjectMockKs
    lateinit var interactor: GetLastApprovedPeriods

    @Test
    fun `getPeriods - with date`() {
        every { versionPersistence.getLatestApprovedOrCurrent(15L) } returns "V.2"
        every { projectPersistence.getProjectPeriods(15L, "V.2") } returns listOf(period)
        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns LocalDate.of(2022, 5, 19)
        every { contractingMonitoringPersistence.getContractingMonitoring(15L) } returns monitoring

        assertThat(interactor.getPeriods(15L)).containsExactly(expectedPeriodWithDates)
    }

    @Test
    fun `getPeriods - without date`() {
        every { versionPersistence.getLatestApprovedOrCurrent(17L) } returns "V.3"
        every { projectPersistence.getProjectPeriods(17L, "V.3") } returns listOf(period)
        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns null
        every { contractingMonitoringPersistence.getContractingMonitoring(17L) } returns monitoring

        assertThat(interactor.getPeriods(17L)).containsExactly(expectedPeriodWithoutDates)
    }

}
