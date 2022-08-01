package io.cloudflight.jems.server.project.service.contracting.monitoring

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringOption
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringAddDate
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoring
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

internal class GetContractingMonitoringTest : UnitTest() {

    companion object {
        private const val projectId = 1L
        private const val version = "2.0"

        private val project = ProjectFull(
            id = projectId,
            customIdentifier = "identifier",
            callSettings = mockk(),
            acronym = "acronym",
            applicant = mockk(),
            projectStatus = mockk(),
            duration = 11
        )
        private val projectSummary = ProjectSummary(
            id = projectId,
            customIdentifier = "TSTCM",
            callName = "Test contracting monitoring",
            acronym = "TCM",
            status = ApplicationStatus.APPROVED,
            firstSubmissionDate = ZonedDateTime.parse("2022-06-20T10:00:00+02:00"),
            lastResubmissionDate = ZonedDateTime.parse("2022-07-20T10:00:00+02:00"),
            specificObjectiveCode = "SO1.1",
            programmePriorityCode = "P1"
        )

        private val lumpSums = listOf(
            ProjectLumpSum(
                programmeLumpSumId = 1,
                period = 1,
                lumpSumContributions = listOf(),
                isFastTrack = true,
                readyForPayment = false,
                comment = null
            )
        )

        private val monitoring = ProjectContractingMonitoring(
            projectId = projectId,
            typologyProv94 = ContractingMonitoringExtendedOption.Partly,
            typologyProv94Comment = "typologyProv94Comment",
            typologyProv95 = ContractingMonitoringExtendedOption.Yes,
            typologyProv95Comment = "typologyProv95Comment",
            typologyStrategic = ContractingMonitoringOption.No,
            typologyStrategicComment = "typologyStrategicComment",
            typologyPartnership = ContractingMonitoringOption.Yes,
            typologyPartnershipComment = "typologyPartnershipComment",
            addDates = listOf(ProjectContractingMonitoringAddDate(
                projectId = projectId,
                number = 1,
                entryIntoForceDate = ZonedDateTime.parse("2022-07-22T10:00:00+02:00").toLocalDate(),
                comment = "comment"
            )),
            fastTrackLumpSums = lumpSums
        )
    }

    @MockK
    lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistenceProvider

    @MockK
    lateinit var versionPersistence: ProjectVersionPersistence

    @MockK
    lateinit var projectLumpSumPersistence: ProjectLumpSumPersistence

    @RelaxedMockK
    lateinit var validator: ContractingValidator

    @InjectMockKs
    lateinit var getContractingMonitoring: GetContractingMonitoring

    @Test
    fun `get project monitoring for approved application`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every { validator.validateProjectStatusForModification(projectSummary) } returns Unit
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { projectPersistence.getProject(projectId, version) } returns project
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring
        every { projectLumpSumPersistence.getLumpSums(1, "2.0")} returns lumpSums

        assertThat(getContractingMonitoring.getContractingMonitoring(projectId))
            .isEqualTo(
                ProjectContractingMonitoring(
                    projectId = projectId,
                    startDate = null,
                    endDate = null,
                    typologyProv94 = ContractingMonitoringExtendedOption.Partly,
                    typologyProv94Comment = "typologyProv94Comment",
                    typologyProv95 = ContractingMonitoringExtendedOption.Yes,
                    typologyProv95Comment = "typologyProv95Comment",
                    typologyStrategic = ContractingMonitoringOption.No,
                    typologyStrategicComment = "typologyStrategicComment",
                    typologyPartnership = ContractingMonitoringOption.Yes,
                    typologyPartnershipComment = "typologyPartnershipComment",
                    addDates = listOf(ProjectContractingMonitoringAddDate(
                        projectId = projectId,
                        number = 1,
                        entryIntoForceDate = ZonedDateTime.parse("2022-07-22T10:00:00+02:00").toLocalDate(),
                        comment = "comment"
                    )),
                    fastTrackLumpSums = lumpSums
                )
            )
    }

    @Test
    fun `get project monitoring for approved application including startDate`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every { validator.validateProjectStatusForModification(projectSummary) } returns Unit
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { projectPersistence.getProject(projectId, version) } returns project
        every {
            contractingMonitoringPersistence.getContractingMonitoring(projectId)
        } returns monitoring.copy(startDate = ZonedDateTime.parse("2022-07-01T10:00:00+02:00").toLocalDate())
        every { projectLumpSumPersistence.getLumpSums(1, "2.0")} returns lumpSums

        assertThat(getContractingMonitoring.getContractingMonitoring(projectId))
            .isEqualTo(
                ProjectContractingMonitoring(
                    projectId = projectId,
                    startDate = ZonedDateTime.parse("2022-07-01T10:00:00+02:00").toLocalDate(),
                    endDate = ZonedDateTime.parse("2023-06-01T10:00:00+02:00").toLocalDate(),
                    typologyProv94 = ContractingMonitoringExtendedOption.Partly,
                    typologyProv94Comment = "typologyProv94Comment",
                    typologyProv95 = ContractingMonitoringExtendedOption.Yes,
                    typologyProv95Comment = "typologyProv95Comment",
                    typologyStrategic = ContractingMonitoringOption.No,
                    typologyStrategicComment = "typologyStrategicComment",
                    typologyPartnership = ContractingMonitoringOption.Yes,
                    typologyPartnershipComment = "typologyPartnershipComment",
                    addDates = listOf(ProjectContractingMonitoringAddDate(
                        projectId = projectId,
                        number = 1,
                        entryIntoForceDate = ZonedDateTime.parse("2022-07-22T10:00:00+02:00").toLocalDate(),
                        comment = "comment"
                    )),
                    fastTrackLumpSums = lumpSums
                )
            )
    }

    @Test
    fun `get project monitoring for NOT approved application throws exception`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every {
            validator.validateProjectStatusForModification(projectSummary)
        } throws ContractingModificationDeniedException()

        assertThrows<ContractingModificationDeniedException> {
            getContractingMonitoring.getContractingMonitoring(projectId)
        }
    }


}
