package io.cloudflight.jems.server.project.controller.contracting.management

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement.GetContractingManagementInteractor
import io.cloudflight.jems.server.project.service.contracting.management.updateProjectContractingManagement.UpdateContractingManagementInteractor
import io.cloudflight.jems.server.project.service.contracting.model.ManagementType
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ContractingManagementControllerTest: UnitTest() {

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
            )
        )
    }




    @MockK
    lateinit var getContractingManagementInteractor: GetContractingManagementInteractor

    @MockK
    lateinit var updateContractingManagementInteractor: UpdateContractingManagementInteractor

    @InjectMockKs
    lateinit var contractingManagementController: ContractingManagementController



    @Test
    fun getContractingManagement() {
        every { getContractingManagementInteractor.getContractingManagement(PROJECT_ID) } returns projectManagers
        Assertions.assertThat(contractingManagementController.getContractingManagement(PROJECT_ID)).containsAll(
            projectManagers.toDTO())
    }

    @Test
    fun updateContractingManagement() {
        val projectManagersSlot = slot<List<ProjectContractingManagement>>()
        every { updateContractingManagementInteractor.updateContractingManagement(PROJECT_ID, capture(projectManagersSlot)) } returns projectManagers
        Assertions.assertThat(contractingManagementController.updateContractingManagement(PROJECT_ID, projectManagers.toDTO()).containsAll(
            projectManagers.toDTO()))
    }
}
