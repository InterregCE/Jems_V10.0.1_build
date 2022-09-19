package io.cloudflight.jems.server.project.service.contracting.contractInfo

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo.GetContractInfo
import io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo.GetContractInfoException
import io.cloudflight.jems.server.project.service.contracting.model.*
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
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

internal class GetContractInfoTest: UnitTest(){

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

        private val projectContractMonitoring = ProjectContractingMonitoring(
            projectId = 1L,
            startDate = LocalDate.of(2022, 8, 1),
            endDate = LocalDate.of(2023, 8, 1),
            typologyProv94 = ContractingMonitoringExtendedOption.Partly,
            typologyProv94Comment = "typologyProv94Comment",
            typologyProv95 = ContractingMonitoringExtendedOption.Yes,
            typologyProv95Comment = "typologyProv95Comment",
            typologyStrategic = ContractingMonitoringOption.No,
            typologyStrategicComment = "typologyStrategicComment",
            typologyPartnership = ContractingMonitoringOption.Yes,
            typologyPartnershipComment = "typologyPartnershipComment",
            addDates = listOf(
                ProjectContractingMonitoringAddDate(
                projectId = 1L,
                number = 1,
                entryIntoForceDate = LocalDate.of(2022, 7, 6),
                comment = "comment"
            ),
            ProjectContractingMonitoringAddDate(
                projectId = 1L,
                number = 2,
                entryIntoForceDate = LocalDate.of(2022, 8, 22),
                comment = "comment"
            )
            ),
            fastTrackLumpSums = null
        )
    }


    @RelaxedMockK
    lateinit var projectContractInfoPersistence: ProjectContractInfoPersistence
    @RelaxedMockK
    lateinit var getContractingMonitoringService: GetContractingMonitoringService
    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistenceProvider
    @RelaxedMockK
    lateinit var validator: ContractingValidator

    @InjectMockKs
    lateinit var getContractInfo: GetContractInfo


    @Test
    fun `get project contract info`() {
        every { projectPersistence.getProjectSummary(1L) } returns projectSummary(ApplicationStatus.APPROVED)
        every { validator.validateProjectStepAndStatus(projectSummary(ApplicationStatus.APPROVED)) } just Runs
        every { getContractingMonitoringService.getProjectContractingMonitoring(1L) } returns projectContractMonitoring
        every { projectContractInfoPersistence.getContractInfo(1L) } returns projectContractingInfo

        assertThat(getContractInfo.getContractInfo(1L)).isEqualTo(
            ProjectContractInfo(
                projectStartDate = LocalDate.of(2022, 8, 1),
                projectEndDate = LocalDate.of(2023, 8, 1),
                website = "tgci.gov",
                subsidyContractDate = LocalDate.of(2022, 8, 22),
                partnershipAgreementDate = LocalDate.of(2022, 9, 12)
            )
        )
    }

    @Test
    fun `get project contract info throws exception for not approved application`() {
        every { projectPersistence.getProjectSummary(1L) } returns projectSummary(ApplicationStatus.SUBMITTED)
        every { validator.validateProjectStepAndStatus(projectSummary(ApplicationStatus.SUBMITTED)) } throws GetContractInfoException(
            RuntimeException()
        )
        assertThrows<GetContractInfoException> {
            getContractInfo.getContractInfo(1L)
        }

    }
}
