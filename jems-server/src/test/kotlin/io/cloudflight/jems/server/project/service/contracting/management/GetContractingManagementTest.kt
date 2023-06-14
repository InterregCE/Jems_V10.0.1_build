package io.cloudflight.jems.server.project.service.contracting.management

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement.GetContractingManagement
import io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement.GetContractingManagementService
import io.cloudflight.jems.server.project.service.contracting.model.ManagementType
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetContractingManagementTest : UnitTest() {
    companion object {

        private const val PROJECT_ID = 12L

        private val projectManagers = listOf(
            ProjectContractingManagement(
                projectId = PROJECT_ID,
                managementType = ManagementType.ProjectManager,
                title = "Mr",
                firstName = "Test",
                lastName = "UserOne",
                email = "testuser1@jems.eu",
                telephone = "9212347801"
            ),
            ProjectContractingManagement(
                projectId = PROJECT_ID,
                managementType = ManagementType.CommunicationManager,
                title = "Mr",
                firstName = "Test",
                lastName = "UserTwo",
                email = "testuser2@jems.eu",
                telephone = "8271929316"
            ),
            ProjectContractingManagement(
                projectId = PROJECT_ID,
                managementType = ManagementType.FinanceManager,
                title = "Mrs",
                firstName = "Test",
                lastName = "UserThree",
                email = "testuser2@jems.eu",
                telephone = "56121347893"
            ),
        )
    }

    @MockK
    lateinit var getContractingManagementService: GetContractingManagementService

    @InjectMockKs
    lateinit var getContractingManagement: GetContractingManagement

    @Test
    fun `get project management for approved application`() {
        every { getContractingManagementService.getContractingManagement(PROJECT_ID) } returns projectManagers
        assertThat(getContractingManagement.getContractingManagement(PROJECT_ID)).isEqualTo(projectManagers)
    }


}
