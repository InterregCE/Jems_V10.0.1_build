package io.cloudflight.jems.server.project.service.contracting.management

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.ContractingDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.management.updateProjectContractingManagement.UpdateContractingManagement
import io.cloudflight.jems.server.project.service.contracting.management.updateProjectContractingManagement.UpdateContractingManagementException
import io.cloudflight.jems.server.project.service.contracting.model.ManagementType
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

class UpdateContractingManagementTest : UnitTest() {

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

        private val projectSummary = ProjectSummary(
            id = 12L,
            customIdentifier = "TSTCM",
            callName = "Test contracting management",
            acronym = "TCM",
            status = ApplicationStatus.APPROVED,
            firstSubmissionDate = ZonedDateTime.parse("2022-06-20T10:00:00+02:00"),
            lastResubmissionDate = ZonedDateTime.parse("2022-07-20T10:00:00+02:00"),
            specificObjectiveCode = "SO1.1",
            programmePriorityCode = "P1",
        )
    }

    @MockK
    lateinit var contractingManagementPersistence: ContractingManagementPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistenceProvider

    @RelaxedMockK
    lateinit var validator: ContractingValidator

    @InjectMockKs
    lateinit var updateContractingManagement: UpdateContractingManagement

    @Test
    fun `add project management to approved application`() {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { validator.validateProjectStepAndStatus(projectSummary) } returns Unit
        every { validator.validateManagerContacts(projectManagers) } returns Unit
        every { contractingManagementPersistence.updateContractingManagement(projectManagers) } returns projectManagers

        Assertions.assertThat(updateContractingManagement.updateContractingManagement(PROJECT_ID, projectManagers))
            .isEqualTo(projectManagers)
    }

    @Test
    fun `add project management to NOT approved application`() {
        every { validator.validateManagerContacts(projectManagers) } returns Unit
        every { contractingManagementPersistence.updateContractingManagement(projectManagers) } returns projectManagers
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { validator.validateProjectStepAndStatus(projectSummary) } throws UpdateContractingManagementException(
            ContractingDeniedException()
        )

        assertThrows<UpdateContractingManagementException> {
            updateContractingManagement.updateContractingManagement(
                PROJECT_ID, projectManagers
            )
        }.cause to (ContractingDeniedException().code == "S-PCM-001")
    }
}
