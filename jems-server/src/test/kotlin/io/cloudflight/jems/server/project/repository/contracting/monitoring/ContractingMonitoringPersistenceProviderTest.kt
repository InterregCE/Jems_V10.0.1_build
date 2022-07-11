package io.cloudflight.jems.server.project.repository.contracting.monitoring

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.ContractingMonitoringAddDateId
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingMonitoringAddDateEntity
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringOption
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringAddDate
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.Optional

internal class ContractingMonitoringPersistenceProviderTest: UnitTest() {

    companion object {
        const val projectId = 2L

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
        private val monitoringEntity = ProjectContractingMonitoringEntity(
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
            addDates = listOf(
                ProjectContractingMonitoringAddDateEntity(
                    ContractingMonitoringAddDateId(projectId = projectId, number = 1),
                    entryIntoForceDate = ZonedDateTime.parse("2022-07-22T10:00:00+02:00").toLocalDate(),
                    comment = "comment"
                )
            )
        )
    }

    @MockK
    lateinit var projectContractingMonitoringRepository: ProjectContractingMonitoringRepository

    @InjectMockKs
    lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistenceProvider

    @Test
    fun `project monitoring is fetched and mapped`() {
        every {
            projectContractingMonitoringRepository.findByProjectId(projectId)
        } returns Optional.of(monitoringEntity)

        assertThat(contractingMonitoringPersistence.getContractingMonitoring(projectId))
            .isEqualTo(monitoring)
    }

    @Test
    fun `project monitoring returns empty if not found`() {
        every { projectContractingMonitoringRepository.findByProjectId(projectId) } returns Optional.empty()

        assertThat(contractingMonitoringPersistence.getContractingMonitoring(projectId))
            .isEqualTo(ProjectContractingMonitoring(projectId = projectId, addDates = emptyList()))
    }

    @Test
    fun `update project monitoring - valid`() {
        val monitoringSlot = slot<ProjectContractingMonitoringEntity>()
        val monitoringToUpdate = ProjectContractingMonitoring(
            projectId = projectId,
            startDate = ZonedDateTime.parse("2022-07-02T10:00:00+02:00").toLocalDate(),
            endDate = ZonedDateTime.parse("2022-07-11T10:00:00+02:00").toLocalDate(),
            typologyProv94 = ContractingMonitoringExtendedOption.No,
            typologyProv94Comment = "typologyProv94Comment",
            typologyProv95 = ContractingMonitoringExtendedOption.Partly,
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
        val monitoringEntityToUpdate = ProjectContractingMonitoringEntity(
            projectId = projectId,
            startDate = ZonedDateTime.parse("2022-07-02T10:00:00+02:00").toLocalDate(),
            endDate = ZonedDateTime.parse("2022-07-11T10:00:00+02:00").toLocalDate(),
            typologyProv94 = ContractingMonitoringExtendedOption.No,
            typologyProv94Comment = "typologyProv94Comment",
            typologyProv95 = ContractingMonitoringExtendedOption.Partly,
            typologyProv95Comment = "typologyProv95Comment",
            typologyStrategic = ContractingMonitoringOption.No,
            typologyStrategicComment = "typologyStrategicComment",
            typologyPartnership = ContractingMonitoringOption.Yes,
            typologyPartnershipComment = "typologyPartnershipComment",
            addDates = listOf(
                ProjectContractingMonitoringAddDateEntity(
                    ContractingMonitoringAddDateId(projectId = projectId, number = 1),
                    entryIntoForceDate = ZonedDateTime.parse("2022-07-22T10:00:00+02:00").toLocalDate(),
                    comment = "comment"
                )
            )
        )
        every { projectContractingMonitoringRepository.save(capture(monitoringSlot)) } returns monitoringEntityToUpdate
        val monitoringUpdated = contractingMonitoringPersistence.updateContractingMonitoring(monitoringToUpdate)

        assertThat(monitoringSlot.captured.toModel()).isEqualTo(monitoringToUpdate)
        assertThat(monitoringUpdated).isNotNull
        assertThat(monitoringUpdated).isEqualTo(monitoringToUpdate)
    }
}
