package io.cloudflight.jems.server.project.service.contracting.reporting.updateContractingReporting

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.ArgumentMatchers.any
import java.time.LocalDate
import java.time.ZonedDateTime

class UpdateContractingReportingTest : UnitTest() {

    companion object {
        private fun deadlineAt(periodNr: Int, date: LocalDate) = ProjectContractingReportingSchedule(
            id = 100L + periodNr,
            type = ContractingDeadlineType.Both,
            periodNumber = periodNr,
            date = date,
            comment = "",
        )

        private fun invalidInput(isPeriodInvalid: Boolean) = ProjectContractingReportingSchedule(
            id = 10L,
            type = ContractingDeadlineType.Finance,
            periodNumber =  if (isPeriodInvalid) null else 1,
            date = if (isPeriodInvalid) LocalDate.of(2022, 8, 9) else null,
            comment = "dummy comment",
        )
    }

    @MockK
    lateinit var contractingReportingPersistence: ContractingReportingPersistence
    @MockK
    lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistence
    @MockK
    lateinit var projectPersistence: ProjectPersistenceProvider
    @MockK
    lateinit var versionPersistence: ProjectVersionPersistence
    @MockK
    lateinit var generalValidator: GeneralValidatorService


    @InjectMockKs
    lateinit var interactor: UpdateContractingReporting

    @BeforeEach
    fun setup() {
        clearMocks(contractingReportingPersistence, contractingMonitoringPersistence, projectPersistence, generalValidator, versionPersistence)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), any(), any()) } returns emptyMap()
    }

    @ParameterizedTest(name = "updateReportingSchedule status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["APPROVED", "CONTRACTED", "IN_MODIFICATION",
        "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"])
    fun updateReportingSchedule(status: ApplicationStatus) {
        val projectId = 100L + status.ordinal
        val version = "V_${status.ordinal}"
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version

        val project = mockk<ProjectFull>()
        every { project.projectStatus.status } returns status
        every { project.periods } returns listOf(ProjectPeriod(number = 1, start = 1, end = 12))
        every { projectPersistence.getProject(projectId, version) } returns project

        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns LocalDate.of(2022, 8, 9)
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring
        every { contractingReportingPersistence.updateContractingReporting(projectId, any()) } returnsArgument 1

        val reporting = listOf(
            ProjectContractingReportingSchedule(
                id = 44L,
                type = ContractingDeadlineType.Content,
                periodNumber = 1,
                date = LocalDate.of(2022, 8, 9) /* first possible date */,
                comment = "dummy comment 44",
            ),
            ProjectContractingReportingSchedule(
                id = 45L,
                type = ContractingDeadlineType.Finance,
                periodNumber = 1,
                date = LocalDate.of(2023, 8, 8) /* last possible date */,
                comment = "dummy comment 45",
            ),
        )
        assertThat(interactor.updateReportingSchedule(projectId, reporting)).containsExactlyElementsOf(reporting)
        verify(exactly = 2) { generalValidator.maxLength(any<String>(), 2000, any()) }
    }

    @ParameterizedTest(name = "updateReportingSchedule - wrong status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["APPROVED", "CONTRACTED", "IN_MODIFICATION",
        "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"], mode = EnumSource.Mode.EXCLUDE)
    fun `updateReportingSchedule - wrong status`(status: ApplicationStatus) {
        val projectId = 200L + status.ordinal
        val version = "V_${status.ordinal}"
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version

        val project = mockk<ProjectFull>()
        every { project.projectStatus.status } returns status
        every { projectPersistence.getProject(projectId, version) } returns project

        assertThrows<ProjectHasNotBeenApprovedYet> { interactor.updateReportingSchedule(projectId, emptyList()) }
        verify(exactly = 0) { contractingReportingPersistence.updateContractingReporting(any(), any()) }
    }

    @Test
    fun `updateReportingSchedule - missing start date`() {
        val projectId = 300L
        val version = "V_1.1"
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version

        val project = mockk<ProjectFull>()
        every { project.projectStatus.status } returns ApplicationStatus.APPROVED
        every { projectPersistence.getProject(projectId, version) } returns project

        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns null
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring

        assertThrows<ContractingStartDateIsMissing> { interactor.updateReportingSchedule(projectId, emptyList()) }
        verify(exactly = 0) { contractingReportingPersistence.updateContractingReporting(any(), any()) }
    }

    @Test
    fun `updateReportingSchedule - reached max amount`() {
        val projectId = 301L
        val version = "V_1.2"
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version

        val project = mockk<ProjectFull>()
        every { project.projectStatus.status } returns ApplicationStatus.APPROVED
        every { project.periods } returns listOf(ProjectPeriod(number = 1, start = 1, end = 12))
        every { projectPersistence.getProject(projectId, version) } returns project

        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns LocalDate.of(2022, 8, 9)
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring

        // mock very long list without instantiation
        val reportingDeadlines = mockk<Collection<ProjectContractingReportingSchedule>>()
        every { reportingDeadlines.size } returns 51
        every { reportingDeadlines.iterator() } returns object : Iterator<ProjectContractingReportingSchedule> {
            override fun hasNext() = false
            override fun next() = throw NoSuchElementException()
        }

        assertThrows<MaxAmountOfDeadlinesReached> { interactor.updateReportingSchedule(projectId, reportingDeadlines) }
        verify(exactly = 0) { contractingReportingPersistence.updateContractingReporting(any(), any()) }
    }

    @Test
    fun `updateReportingSchedule - wrong period numbers`() {
        val projectId = 302L
        val version = "V_1.3"
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version

        val project = mockk<ProjectFull>()
        every { project.projectStatus.status } returns ApplicationStatus.APPROVED
        every { project.periods } returns listOf(ProjectPeriod(number = 1, start = 1, end = 12))
        every { projectPersistence.getProject(projectId, version) } returns project

        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns LocalDate.of(2022, 8, 9)
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring

        val reporting = listOf(
            ProjectContractingReportingSchedule(
                id = 49L,
                type = ContractingDeadlineType.Both,
                periodNumber = 2,
                date = LocalDate.of(2022, 8, 9),
                comment = "dummy comment 44",
            ),
        )

        assertThrows<InvalidPeriodNumbers> { interactor.updateReportingSchedule(projectId, reporting) }
        verify(exactly = 0) { contractingReportingPersistence.updateContractingReporting(any(), any()) }
    }

    @Test
    fun `updateReportingSchedule - deadlines calculated successfully`() {
        val projectId = 303L
        val version = "V_1.4"
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version

        val project = mockk<ProjectFull>()
        every { project.projectStatus.status } returns ApplicationStatus.APPROVED
        every { project.periods } returns listOf(
            ProjectPeriod(number = 1, start = 1, end = 1),
            ProjectPeriod(number = 2, start = 2, end = 2),
            ProjectPeriod(number = 3, start = 3, end = 3),
        )
        every { projectPersistence.getProject(projectId, version) } returns project

        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns LocalDate.of(2022, 1, 31)
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring

        val reporting = listOf(
            deadlineAt(1, LocalDate.of(2022, 1, 31)),
            deadlineAt(1, LocalDate.of(2022, 2, 27)),
            deadlineAt(2, LocalDate.of(2022, 2, 28)),
            deadlineAt(2, LocalDate.of(2022, 3, 30)),
            deadlineAt(3, LocalDate.of(2022, 3, 31)),
            deadlineAt(3, LocalDate.of(2022, 4, 29)),
        )

        every { contractingReportingPersistence.updateContractingReporting(projectId, any()) } returnsArgument 1
        interactor.updateReportingSchedule(projectId, reporting)
        // no exception thrown
    }

    @Test
    fun `updateReportingSchedule - deadlines do not fit periods`() {
        val projectId = 304L
        val version = "V_1.5"
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version

        val project = mockk<ProjectFull>()
        every { project.projectStatus.status } returns ApplicationStatus.APPROVED
        every { project.periods } returns listOf(
            ProjectPeriod(number = 1, start = 1, end = 1),
            ProjectPeriod(number = 2, start = 2, end = 2),
            ProjectPeriod(number = 3, start = 3, end = 3),
        )
        every { projectPersistence.getProject(projectId, version) } returns project

        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns LocalDate.of(2022, 1, 31)
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring

        val reporting = listOf(
            deadlineAt(1, LocalDate.of(2022, 1, 30)),
            deadlineAt(1, LocalDate.of(2022, 2, 28)),
            deadlineAt(2, LocalDate.of(2022, 2, 27)),
            deadlineAt(2, LocalDate.of(2022, 3, 31)),
            deadlineAt(3, LocalDate.of(2022, 3, 30)),
            deadlineAt(3, LocalDate.of(2022, 4, 30)),
        )

        val ex = assertThrows<DeadlinesDoNotFitPeriod> { interactor.updateReportingSchedule(projectId, reporting) }
        assertThat(ex.formErrors).hasSize(6)
        assertThat(ex.message).isEqualTo("Following dates are invalid: 2022-01-30 does not fit into period 1 (2022-01-31 - 2022-02-27), " +
            "2022-02-28 does not fit into period 1 (2022-01-31 - 2022-02-27), " +
            "2022-02-27 does not fit into period 2 (2022-02-28 - 2022-03-30), " +
            "2022-03-31 does not fit into period 2 (2022-02-28 - 2022-03-30), " +
            "2022-03-30 does not fit into period 3 (2022-03-31 - 2022-04-29), " +
            "2022-04-30 does not fit into period 3 (2022-03-31 - 2022-04-29)")
    }

    @Test
    fun `updateReportingSchedule - invalid period number`() {
        val projectId = 305L
        val version = "V_2"
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        val project = mockk<ProjectFull>()
        every { project.projectStatus.status } returns ApplicationStatus.APPROVED
        every { project.periods } returns listOf(
            ProjectPeriod(number = 1, start = 1, end = 1),
            ProjectPeriod(number = 2, start = 2, end = 2),
            ProjectPeriod(number = 3, start = 3, end = 3),
        )
        every { projectPersistence.getProject(projectId, version) } returns project

        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns LocalDate.of(2022, 1, 31)
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring

        val inputData = mutableListOf(invalidInput(true))
        assertThrows<EmptyPeriodNumber> {
            interactor.updateReportingSchedule(projectId, inputData)
        }
    }

    @Test
    fun `updateReportingSchedule - invalid deadline date`() {
        val projectId = 306L
        val version = "V_3"
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        val project = mockk<ProjectFull>()
        every { project.projectStatus.status } returns ApplicationStatus.APPROVED
        every { project.periods } returns listOf(
            ProjectPeriod(number = 1, start = 1, end = 1),
            ProjectPeriod(number = 2, start = 2, end = 2),
            ProjectPeriod(number = 3, start = 3, end = 3),
        )
        every { projectPersistence.getProject(projectId, version) } returns project

        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns LocalDate.of(2022, 1, 31)
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring

        val inputData = mutableListOf(invalidInput(false))
        assertThrows<EmptyDeadlineDate> {
            interactor.updateReportingSchedule(projectId, inputData)
        }
    }

    @Test
    fun `clearNoLongerAvailablePeriodsAndDates`() {
        val projectId = 307L
        val version = "v4.0"
        val maxNewDuration = 3;
        val invalidPeriodNumberList = listOf(4L);
        val versions = listOf(
            ProjectVersion(
                "v4.0",
                projectId,
                ZonedDateTime.now(),
                mockk(),
                ApplicationStatus.APPROVED,
                false
            ),
            ProjectVersion(
                "v3.0",
                projectId,
                ZonedDateTime.now().minusDays(1),
                mockk(),
                ApplicationStatus.APPROVED,
                false
            )
        )

        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { versionPersistence.getAllVersionsByProjectId(projectId) } returns versions

        val currentProject = mockk<ProjectFull>()
        every { currentProject.projectStatus.status } returns ApplicationStatus.APPROVED
        every { currentProject.duration } returns 3

        val oldProject = mockk<ProjectFull>()
        every { oldProject.projectStatus.status } returns ApplicationStatus.APPROVED
        every { oldProject.duration } returns 4

        every { projectPersistence.getProject(projectId, "v3.0") } returns oldProject
        every { projectPersistence.getProject(projectId) } returns currentProject

        val monitoring = mockk<ProjectContractingMonitoring>()
        every { monitoring.startDate } returns LocalDate.of(2022, 1, 31)
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring
        every { contractingReportingPersistence.getScheduleIdsWhosePeriodsAndDatesNotProper(projectId, maxNewDuration) } returns invalidPeriodNumberList
        every { contractingReportingPersistence.clearPeriodAndDatesFor(any()) } returns Unit

        interactor.checkNoLongerAvailablePeriodsAndDatesToRemove(projectId)
        verify(exactly = 1) { contractingReportingPersistence.clearPeriodAndDatesFor(invalidPeriodNumberList) }
    }

}
