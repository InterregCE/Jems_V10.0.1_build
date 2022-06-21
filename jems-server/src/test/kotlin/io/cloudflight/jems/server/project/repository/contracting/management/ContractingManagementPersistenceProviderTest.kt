package io.cloudflight.jems.server.project.repository.contracting.management

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.Contact
import io.cloudflight.jems.server.project.entity.contracting.ContractingManagementId
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingManagementEntity
import io.cloudflight.jems.server.project.service.contracting.model.ManagementType
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ContractingManagementPersistenceProviderTest: UnitTest() {

    companion object {
        const val PROJECT_ID = 12L
        private val projectManagersEntities = mutableListOf(
            ProjectContractingManagementEntity(
                managementId = ContractingManagementId(
                    projectId = PROJECT_ID,
                    managementType = ManagementType.ProjectManager
                ),
                contact = Contact(
                    title = "Mr",
                    firstName = "Test",
                    lastName = "One",
                    email = "testuser01@jems.eu",
                    telephone = "0928376124"
                )
            ),
            ProjectContractingManagementEntity(
                managementId = ContractingManagementId(
                    projectId = PROJECT_ID,
                    managementType = ManagementType.FinanceManager
                ),
                contact = Contact(
                    title = "Mr",
                    firstName = "Test",
                    lastName = "Two",
                    email = "testuser02@jems.eu",
                    telephone = "9826712456"
                )
            ),
            ProjectContractingManagementEntity(
                managementId = ContractingManagementId(
                    projectId = PROJECT_ID,
                    managementType = ManagementType.CommunicationManager
                ),
                contact = Contact(
                    title = "Mr",
                    firstName = "Test",
                    lastName = "Three",
                    email = "testuser03@jems.eu",
                    telephone = "0928376124"
                )
            )
        )

        private val projectManagersModelList = listOf(
            ProjectContractingManagement(
                projectId = PROJECT_ID,
                managementType = ManagementType.ProjectManager,
                title = "Mr",
                firstName = "Test",
                lastName = "One",
                email = "testuser01@jems.eu",
                telephone = "0928376124"
            ),
            ProjectContractingManagement(
                projectId = PROJECT_ID,
                managementType = ManagementType.FinanceManager,
                title = "Mr",
                firstName = "Test",
                lastName = "Two",
                email = "testuser02@jems.eu",
                telephone = "9826712456"
            ),
            ProjectContractingManagement(
                projectId = PROJECT_ID,
                managementType = ManagementType.CommunicationManager,
                title = "Mr",
                firstName = "Test",
                lastName = "Three",
                email = "testuser03@jems.eu",
                telephone = "0928376124"
            )
        )
    }

    @MockK
    lateinit var projectContractingManagementRepository: ProjectContractingManagementRepository

    @InjectMockKs
    lateinit var contractingManagementPersistence: ContractingManagementPersistenceProvider

    @Test
    fun `project managers are fetched and mapped`() {
        every { projectContractingManagementRepository.findByManagementIdProjectId(PROJECT_ID) } returns projectManagersEntities
        assertThat(contractingManagementPersistence.getContractingManagement(PROJECT_ID)).containsAll(projectManagersModelList)
    }

    @Test
    fun `update project managers - everything valid`() {
        val managersSlot = slot<List<ProjectContractingManagementEntity>>()
        val projectManagerToUpdate = ProjectContractingManagement(
            projectId = PROJECT_ID,
            managementType = ManagementType.ProjectManager,
            title = "Mr",
            firstName = "Test",
            lastName = "Four",
            email = "testuser04@jems.eu",
            telephone = "4444444440"
        )
        val managers = projectManagersModelList.toMutableList()
        managers[0] = projectManagerToUpdate
        every { projectContractingManagementRepository.saveAll(capture(managersSlot)) } returns projectManagersEntities
        contractingManagementPersistence.updateContractingManagement(managers)

        assertThat(managersSlot.captured.toModelList()).containsAll(managers)
        val manager = managersSlot.captured.first{ it.managementId.managementType == ManagementType.ProjectManager}
        assertThat(manager).isNotNull
        assertThat(manager.contact.telephone).isEqualTo(managers[0].telephone)
        assertThat(manager.contact.email).isEqualTo(managers[0].email)
        assertThat(manager.contact.lastName).isEqualTo(managers[0].lastName)
    }
}
