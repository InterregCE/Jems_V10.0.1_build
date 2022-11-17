package io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringStartDate

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringStartDate
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class GetContractingMonitoringStartDateTest : UnitTest() {

    companion object {
        private val startDate = LocalDate.of(2022, 5, 19)

        private val monitoring = ProjectContractingMonitoring(
            projectId = 1L,
            addDates = listOf(),
            dimensionCodes = listOf(),
            startDate = startDate
        )
    }

    @MockK
    lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistence

    @InjectMockKs
    lateinit var interactor: GetContractingMonitoringStartDate

    @Test
    fun `get start date`() {
        every { contractingMonitoringPersistence.getContractingMonitoring(1L) } returns monitoring
        Assertions.assertThat(interactor.getStartDate(1L))
            .isEqualTo(ProjectContractingMonitoringStartDate(startDate))
    }
}
