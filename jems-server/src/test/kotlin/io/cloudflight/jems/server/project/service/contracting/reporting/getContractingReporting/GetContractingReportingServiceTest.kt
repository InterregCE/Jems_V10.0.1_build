package io.cloudflight.jems.server.project.service.contracting.reporting.getContractingReporting

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class GetContractingReportingServiceTest: UnitTest() {

    @MockK
    lateinit var contractingReportingPersistence: ContractingReportingPersistence

    @InjectMockKs
    lateinit var getContractingReportingService: GetContractingReportingService

    @Test
    fun getReportingSchedule() {
        val projectId = 95L
        val reportingModel = mockk<ProjectContractingReportingSchedule>()
        every { contractingReportingPersistence.getContractingReporting(projectId) } returns listOf(reportingModel)
        Assertions.assertThat(getContractingReportingService.getReportingSchedule(projectId)).containsExactly(reportingModel)
    }

}
