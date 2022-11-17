package io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetContractingMonitoringTest : UnitTest() {

    @MockK
    lateinit var getContractingMonitoringService: GetContractingMonitoringService

    @InjectMockKs
    lateinit var interactor: GetContractingMonitoring

    @Test
    fun getContractingMonitoring() {
        val mock = mockk<ProjectContractingMonitoring>()
        every { getContractingMonitoringService.getContractingMonitoring(4L) } returns mock
        assertThat(interactor.getContractingMonitoring(4L)).isEqualTo(mock)
    }

}
