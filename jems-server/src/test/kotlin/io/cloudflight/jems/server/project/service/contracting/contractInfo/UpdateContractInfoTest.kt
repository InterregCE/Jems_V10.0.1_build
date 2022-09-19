package io.cloudflight.jems.server.project.service.contracting.contractInfo

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.contractInfo.updateContractInfo.UpdateContractInfo
import io.cloudflight.jems.server.project.service.contracting.contractInfo.updateContractInfo.UpdateContractInfoException
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.ZonedDateTime

class UpdateContractInfoTest: UnitTest() {

    companion object {
        private val projectContractingInfo = ProjectContractInfo(
            projectStartDate = null,
            projectEndDate = null,
            website = "tgci.gov",
            subsidyContractDate = null,
            partnershipAgreementDate = LocalDate.of(2022, 9, 12)
        )

        private fun projectSummary(applicationStatus: ApplicationStatus) = ProjectSummary(
            id = 1L,
            customIdentifier = "TGCI",
            callName = "Test Contract Info",
            acronym = "TCI",
            status = applicationStatus,
            firstSubmissionDate = ZonedDateTime.parse("2022-06-20T10:00:00+02:00"),
            lastResubmissionDate = ZonedDateTime.parse("2022-07-20T10:00:00+02:00"),
            specificObjectiveCode = "SO1.1",
            programmePriorityCode = "P1"
        )
    }

    @RelaxedMockK
    lateinit var projectContractInfoPersistence: ProjectContractInfoPersistence
    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistenceProvider
    @RelaxedMockK
    lateinit var validator: ContractingValidator

    @InjectMockKs
    lateinit var updateContractInfo: UpdateContractInfo


    @Test
    fun `update project contract info`() {
        every { projectPersistence.getProjectSummary(1L) } returns projectSummary(ApplicationStatus.APPROVED)
        every { validator.validateProjectStepAndStatus(projectSummary(ApplicationStatus.APPROVED)) } just Runs
        every { projectContractInfoPersistence.updateContractInfo(1L, any()) } returns projectContractingInfo

        assertThat(updateContractInfo.updateContractInfo(1L, ProjectContractInfo(
            projectStartDate = null,
            projectEndDate = null,
            website = "tgci.gov",
            subsidyContractDate = null,
            partnershipAgreementDate = LocalDate.of(2022, 9, 12)
        ) )).isEqualTo(projectContractingInfo)
    }

    @Test
    fun `update contract info for not approved projects throws exception`() {
        every { projectPersistence.getProjectSummary(1L) } returns projectSummary(ApplicationStatus.SUBMITTED)
        every { validator.validateProjectStatusForModification(projectSummary(ApplicationStatus.SUBMITTED)) } throws UpdateContractInfoException(
            ContractingModificationDeniedException()
        )

        assertThrows<UpdateContractInfoException> {
            updateContractInfo.updateContractInfo(1L, projectContractingInfo)
        }
    }
}
