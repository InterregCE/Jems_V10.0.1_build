package io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.*
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureLastPaymentDate
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class GetContractingMonitoringServiceTest : UnitTest() {

    companion object {
        private const val projectId = 1L
        private const val version = "2.0"
        private const val lumpSumId = 2L
        private const val orderNr = 1

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
            callId = 1L,
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
                orderNr = orderNr,
                programmeLumpSumId = lumpSumId,
                period = 1,
                lumpSumContributions = listOf(),
                fastTrack = true,
                readyForPayment = false,
                comment = null
            )
        )

        private val monitoring = ProjectContractingMonitoring(
            projectId = projectId,
            closureDate = LocalDate.of(2024, 1, 24),
            lastPaymentDates = listOf(
                ContractingClosureLastPaymentDate(774L, 14, "774-abbr",
                    ProjectPartnerRole.PARTNER, false, LocalDate.of(2025, 3, 18)),
            ),
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
            fastTrackLumpSums = lumpSums,
            dimensionCodes = listOf(ContractingDimensionCode(
                id = 0,
                projectId = projectId,
                programmeObjectiveDimension = ProgrammeObjectiveDimension.TypesOfIntervention,
                dimensionCode = "001",
                projectBudgetAmountShare = BigDecimal(10000)
            ))
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

    @MockK
    private lateinit var partnerPersistence: PartnerPersistence

    @InjectMockKs
    lateinit var getContractingMonitoringService: GetContractingMonitoringService

    @Test
    fun `get project monitoring for approved application`() {
        mockkObject(ContractingValidator.Companion)
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every { ContractingValidator.validateProjectStatusForModification(projectSummary) } returns Unit

        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { partnerPersistence.findAllByProjectIdForDropdown(projectId, any(), version) } returns listOf(
            ProjectPartnerSummary(144L, "144-abbr", null, true, ProjectPartnerRole.LEAD_PARTNER, 4),
        )
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring
        every { projectPersistence.getProject(projectId, version) } returns project
        every { projectLumpSumPersistence.getLumpSums(projectId, version) } returns lumpSums
        every { contractingMonitoringPersistence.getPartnerPaymentDate(projectId) } returns mapOf(
            144L to LocalDate.of(2024, 1, 25),
        )

        assertThat(getContractingMonitoringService.getContractingMonitoring(projectId))
            .isEqualTo(
                ProjectContractingMonitoring(
                    projectId = projectId,
                    startDate = null,
                    endDate = null,
                    closureDate = LocalDate.of(2024, 1, 24),
                    lastPaymentDates = listOf(
                        ContractingClosureLastPaymentDate(144L, 4, "144-abbr",
                            ProjectPartnerRole.LEAD_PARTNER, false, LocalDate.of(2024, 1, 25)),
                    ),
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
                    fastTrackLumpSums = lumpSums,
                    dimensionCodes = listOf(ContractingDimensionCode(
                        id = 0,
                        projectId = projectId,
                        programmeObjectiveDimension = ProgrammeObjectiveDimension.TypesOfIntervention,
                        dimensionCode = "001",
                        projectBudgetAmountShare = BigDecimal(10000)
                    ))
                )
            )
    }

    private val validDates = listOf(
        Triple(LocalDate.of(2022, 7, 1), 11, LocalDate.of(2023, 5, 31)),
        Triple(LocalDate.of(2022, 1, 31), 13, LocalDate.of(2023, 2, 27)),
        Triple(LocalDate.of(2024, 1, 31), 1, LocalDate.of(2024, 2, 28)),
        Triple(LocalDate.of(2022, 1, 30), 1, LocalDate.of(2022, 2, 27)),
        Triple(LocalDate.of(2022, 1, 29), 1, LocalDate.of(2022, 2, 27)),
        Triple(LocalDate.of(2022, 1, 28), 1, LocalDate.of(2022, 2, 27)),
        Triple(LocalDate.of(2022, 1, 27), 1, LocalDate.of(2022, 2, 26)),
    )

    @Test
    fun `get project monitoring for approved application including startDate`() {
        mockkObject(ContractingValidator.Companion)
        validDates.forEach {
            `test get project monitoring startDate calc`(it.first, it.second, it.third)
        }
    }

    private fun `test get project monitoring startDate calc`(startDate: LocalDate, duration: Int, expectedEndDate: LocalDate) {
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { partnerPersistence.findAllByProjectIdForDropdown(projectId, any(), version) } returns emptyList()
        every { contractingMonitoringPersistence.getPartnerPaymentDate(projectId) } returns emptyMap()
        every { projectPersistence.getProject(projectId, version) } returns project.copy(duration = duration)
        every {
            contractingMonitoringPersistence.getContractingMonitoring(projectId)
        } returns monitoring.copy(startDate = startDate)
        every { projectLumpSumPersistence.getLumpSums(projectId, version) } returns lumpSums
        every { contractingMonitoringPersistence.existsSavedInstallment(projectId, lumpSumId, orderNr) } returns false

        assertThat(getContractingMonitoringService.getContractingMonitoring(projectId))
            .isEqualTo(
                ProjectContractingMonitoring(
                    projectId = projectId,
                    startDate = startDate,
                    endDate = expectedEndDate,
                    closureDate = LocalDate.of(2024, 1, 24),
                    lastPaymentDates = emptyList(),
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
                    fastTrackLumpSums = lumpSums,
                    dimensionCodes = listOf(ContractingDimensionCode(
                        id = 0,
                        projectId = projectId,
                        programmeObjectiveDimension = ProgrammeObjectiveDimension.TypesOfIntervention,
                        dimensionCode = "001",
                        projectBudgetAmountShare = BigDecimal(10000)
                    ))
                )
            )
    }

    @Test
    fun `get project monitoring for NOT approved application throws exception`() {
        mockkObject(ContractingValidator.Companion)
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every {
            ContractingValidator.validateProjectStatusForModification(projectSummary)
        } throws ContractingModificationDeniedException()

        assertThrows<ContractingModificationDeniedException> {
            getContractingMonitoringService.getContractingMonitoring(projectId)
        }
    }

    @Test
    fun getContractMonitoringDates() {
        every { contractingMonitoringPersistence.getContractingMonitoring(51L) } returns ProjectContractingMonitoring(
            projectId = projectId,
            startDate = LocalDate.of(2022, 1, 31),
            endDate = LocalDate.of(2022, 2, 27),
            lastPaymentDates = mockk(),
            typologyProv94 = mockk(),
            typologyProv94Comment = null,
            typologyProv95 = mockk(),
            typologyProv95Comment = null,
            typologyStrategic = mockk(),
            typologyStrategicComment = null,
            typologyPartnership = mockk(),
            typologyPartnershipComment = null,
            addDates = mockk(),
            fastTrackLumpSums = mockk(),
            dimensionCodes = mockk(),
        )
        every { versionPersistence.getLatestApprovedOrCurrent(51L) } returns "V1"
        every { projectPersistence.getProject(51L, "V1").duration } returns 1

        assertThat(getContractingMonitoringService.getContractMonitoringDates(51L))
            .isEqualTo(Pair(LocalDate.of(2022, 1, 31), LocalDate.of(2022, 2, 27)))
    }

    @Test
    fun `getContractMonitoringDates - no start date`() {
        every { contractingMonitoringPersistence.getContractingMonitoring(52L) } returns ProjectContractingMonitoring(
            projectId = projectId,
            startDate = LocalDate.of(2022, 8, 19),
            endDate = null,
            lastPaymentDates = mockk(),
            typologyProv94 = mockk(),
            typologyProv94Comment = null,
            typologyProv95 = mockk(),
            typologyProv95Comment = null,
            typologyStrategic = mockk(),
            typologyStrategicComment = null,
            typologyPartnership = mockk(),
            typologyPartnershipComment = null,
            addDates = mockk(),
            fastTrackLumpSums = mockk(),
            dimensionCodes = mockk(),
        )
        every { versionPersistence.getLatestApprovedOrCurrent(52L) } returns "V1"
        every { projectPersistence.getProject(52L, "V1").duration } returns null

        assertThat(getContractingMonitoringService.getContractMonitoringDates(52L))
            .isEqualTo(Pair(LocalDate.of(2022, 8, 19), null))
    }

    @Test
    fun `getContractMonitoringDates - no duration`() {
        every { contractingMonitoringPersistence.getContractingMonitoring(50L).startDate } returns null
        every { versionPersistence.getLatestApprovedOrCurrent(50L) } returns "V1"
        every { projectPersistence.getProject(50L, "V1").duration } returns 1

        assertThat(getContractingMonitoringService.getContractMonitoringDates(50L)).isNull()
    }

    @Test
    fun `getContractingMonitoring - including installment info`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        val lumpSum = ProjectLumpSum(
            orderNr = orderNr,
            programmeLumpSumId = lumpSumId,
            period = 1,
            lumpSumContributions = listOf(),
            fastTrack = true,
            readyForPayment = true
        )
        val monitoringOld = ProjectContractingMonitoring(
            projectId = projectId,
            lastPaymentDates = listOf(
                ContractingClosureLastPaymentDate(774L, 14, "774-abbr",
                    ProjectPartnerRole.PARTNER, false, LocalDate.of(2025, 3, 18)),
            ),
            addDates = emptyList(),
            fastTrackLumpSums = listOf(lumpSum),
            dimensionCodes = emptyList(),
            typologyProv94 = ContractingMonitoringExtendedOption.Partly,
            typologyProv95 = ContractingMonitoringExtendedOption.Yes,
            typologyStrategic = ContractingMonitoringOption.No,
            typologyPartnership = ContractingMonitoringOption.Yes,
        )
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoringOld
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { partnerPersistence.findAllByProjectIdForDropdown(projectId, any(), version) } returns emptyList()
        every { contractingMonitoringPersistence.getPartnerPaymentDate(projectId) } returns emptyMap()
        every { projectLumpSumPersistence.getLumpSums(projectId, version) } returns listOf(lumpSum)
        every { contractingMonitoringPersistence.existsSavedInstallment(projectId, lumpSumId, orderNr) } returns true

        assertThat(getContractingMonitoringService.getContractingMonitoring(projectId))
            .isEqualTo(
                ProjectContractingMonitoring(
                    projectId = projectId,
                    lastPaymentDates = emptyList(),
                    addDates = emptyList(),
                    fastTrackLumpSums = listOf(lumpSum.copy(
                        installmentsAlreadyCreated = true
                    )),
                dimensionCodes = emptyList(),
                typologyProv94 = ContractingMonitoringExtendedOption.Partly,
                typologyProv95 = ContractingMonitoringExtendedOption.Yes,
                typologyStrategic = ContractingMonitoringOption.No,
                typologyPartnership = ContractingMonitoringOption.Yes,
            ))

    }
}
