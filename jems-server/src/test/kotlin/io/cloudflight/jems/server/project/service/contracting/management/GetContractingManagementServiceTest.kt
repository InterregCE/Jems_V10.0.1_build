package io.cloudflight.jems.server.project.service.contracting.management

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement.GetContractingManagementException
import io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement.GetContractingManagementService
import io.cloudflight.jems.server.project.service.contracting.model.ManagementType
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

internal class GetContractingManagementServiceTest: UnitTest() {

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
            callId = 1L,
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

    @InjectMockKs
    lateinit var getContractingManagementService: GetContractingManagementService

    @Test
    fun `get project management for approved application`() {
        mockkObject(ContractingValidator.Companion)
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { ContractingValidator.validateProjectStepAndStatus(projectSummary) } returns Unit
        every { contractingManagementPersistence.getContractingManagement(PROJECT_ID) } returns projectManagers

        Assertions.assertThat(getContractingManagementService.getContractingManagement(PROJECT_ID))
            .isEqualTo(projectManagers)
    }

    @Test
    fun `get project management for NOT approved application throws exception`() {
        mockkObject(ContractingValidator.Companion)
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary
        every { ContractingValidator.validateProjectStepAndStatus(projectSummary) } throws GetContractingManagementException(
            RuntimeException()
        )
        assertThrows<GetContractingManagementException> {
            getContractingManagementService.getContractingManagement(PROJECT_ID)
        }
    }
}
