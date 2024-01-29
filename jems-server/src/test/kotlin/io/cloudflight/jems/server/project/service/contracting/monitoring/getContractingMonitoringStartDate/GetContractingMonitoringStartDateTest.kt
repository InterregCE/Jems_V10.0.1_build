package io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringStartDate

import io.cloudflight.jems.server.UnitTest
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
    }

    @MockK
    lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistence

    @InjectMockKs
    lateinit var interactor: GetContractingMonitoringStartDate

    @Test
    fun `get start date`() {
        every { contractingMonitoringPersistence.getContractingMonitoring(1L).startDate } returns startDate
        Assertions.assertThat(interactor.getStartDate(1L))
            .isEqualTo(ProjectContractingMonitoringStartDate(startDate))
    }
}
