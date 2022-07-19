package io.cloudflight.jems.server.project.service.contracting.monitoring

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringOption
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringAddDate
import io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring.UpdateContractingMonitoring
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class UpdateContractingMonitoringTest : UnitTest() {

    companion object {
        private const val projectId = 1L

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
        private val monitoring = ProjectContractingMonitoring(
            projectId = projectId,
            startDate = ZonedDateTime.parse("2022-07-01T10:00:00+02:00").toLocalDate(),
            endDate = ZonedDateTime.parse("2022-07-10T10:00:00+02:00").toLocalDate(),
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
            ))
        )
    }

    @MockK
    lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistenceProvider

    @RelaxedMockK
    lateinit var validator: ContractingValidator

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var updateContractingMonitoring: UpdateContractingMonitoring

    @BeforeEach
    fun setup() {
        clearMocks(auditPublisher)
    }

    @Test
    fun `add project management to approved application`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every { validator.validateProjectStatusForModification(projectSummary) } returns Unit
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring
        every { contractingMonitoringPersistence.updateContractingMonitoring(monitoring) } returns monitoring

        assertThat(updateContractingMonitoring.updateContractingMonitoring(projectId, monitoring)).isEqualTo(monitoring)
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @Test
    fun `add project management to contracted application`() {
        every {
            projectPersistence.getProjectSummary(projectId)
        } returns projectSummary.copy(status = ApplicationStatus.CONTRACTED)
        every { validator.validateProjectStatusForModification(projectSummary) } returns Unit
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring
        every { contractingMonitoringPersistence.updateContractingMonitoring(monitoring) } returns monitoring

        assertThat(updateContractingMonitoring.updateContractingMonitoring(projectId, monitoring))
            .isEqualTo(monitoring)
        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_CONTRACT_MONITORING_CHANGED,
                project = AuditProject(id = projectId.toString()),
                description = "Fields changed:\n(no-change)"
            )
        )
    }

    @Test
    fun `add project management to contracted application and fields changed`() {
        every {
            projectPersistence.getProjectSummary(projectId)
        } returns projectSummary.copy(status = ApplicationStatus.CONTRACTED)
        every { validator.validateProjectStatusForModification(projectSummary) } returns Unit
        val oldDate = ZonedDateTime.parse("2022-06-02T10:00:00+02:00").toLocalDate()
        every {
            contractingMonitoringPersistence.getContractingMonitoring(projectId)
        } returns monitoring.copy(
            startDate = oldDate,
            typologyProv94 = ContractingMonitoringExtendedOption.No,
            typologyProv95 = ContractingMonitoringExtendedOption.Partly,
            typologyStrategic = ContractingMonitoringOption.Yes,
            typologyPartnership = ContractingMonitoringOption.No,
            addDates = listOf(
                ProjectContractingMonitoringAddDate(projectId, 1, oldDate, "comment1"),
                ProjectContractingMonitoringAddDate(projectId, 2, oldDate, "comment2")
            )
        )
        every { contractingMonitoringPersistence.updateContractingMonitoring(monitoring) } returns monitoring

        assertThat(updateContractingMonitoring.updateContractingMonitoring(projectId, monitoring))
            .isEqualTo(monitoring)
        val event = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(event)) }
        assertThat(event.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_CONTRACT_MONITORING_CHANGED,
                project = AuditProject(id = projectId.toString()),
                description = "Fields changed:\n" +
                    "startDate changed from 2022-06-02 to 2022-07-01,\n" +
                    "typologyProv94 changed from No to Partly,\n" +
                    "typologyProv95 changed from Partly to Yes,\n" +
                    "typologyStrategic changed from Yes to No,\n" +
                    "typologyPartnership changed from No to Yes,\n" +
                    "addSubContractDates changed from [\n" +
                    "  2022-06-02\n" +
                    "  2022-06-02\n" +
                    "] to [\n" +
                    "  2022-07-22\n" +
                    "]"
            )
        )
    }

    @Test
    fun `add project management to NOT approved application`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every {
            validator.validateProjectStatusForModification(projectSummary)
        } throws ContractingModificationDeniedException()

        assertThrows<ContractingModificationDeniedException> {
            updateContractingMonitoring.updateContractingMonitoring(projectId, monitoring)
        }
    }
}
