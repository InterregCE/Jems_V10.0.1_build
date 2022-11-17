package io.cloudflight.jems.server.project.repository.contracting.monitoring

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import io.cloudflight.jems.server.project.entity.contracting.*
import io.cloudflight.jems.server.project.service.contracting.model.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

internal class ContractingMonitoringPersistenceProviderTest: UnitTest() {

    companion object {
        const val projectId = 2L

        private val monitoring = ProjectContractingMonitoring(
            projectId = projectId,
            startDate = ZonedDateTime.parse("2022-07-01T10:00:00+02:00").toLocalDate(),
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
            dimensionCodes = listOf(
                ContractingDimensionCode(
                    id = 0,
                    projectId = projectId,
                    programmeObjectiveDimension = ProgrammeObjectiveDimension.TypesOfIntervention,
                    dimensionCode = "001",
                    projectBudgetAmountShare = BigDecimal(10000)
                )
            )
        )
        private val monitoringEntity = ProjectContractingMonitoringEntity(
            projectId = projectId,
            startDate = ZonedDateTime.parse("2022-07-01T10:00:00+02:00").toLocalDate(),
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
            ),
            dimensionCodes = listOf(
                ContractingDimensionCodeEntity(
                    id = 0,
                    projectId = projectId,
                    programmeObjectiveDimension = ProgrammeObjectiveDimension.TypesOfIntervention,
                    dimensionCode = "001",
                    projectBudgetAmountShare = BigDecimal(10000)
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
            .isEqualTo(ProjectContractingMonitoring(projectId = projectId, addDates = emptyList(), dimensionCodes = emptyList()))
    }

    @Test
    fun `exists saved installment for project fast track entry`() {
        every { projectContractingMonitoringRepository.existsSavedInstallment(projectId, 2L, 3) } returns true
        assertThat(contractingMonitoringPersistence.existsSavedInstallment(projectId, 2L, 3)).isTrue
    }

    @Test
    fun `not exists saved installment for project fast track entry`() {
        every { projectContractingMonitoringRepository.existsSavedInstallment(projectId, 2L, 1) } returns false
        assertThat(contractingMonitoringPersistence.existsSavedInstallment(projectId, 2L, 1)).isFalse
    }

    @Test
    fun `update project monitoring - valid`() {
        val monitoringSlot = slot<ProjectContractingMonitoringEntity>()
        val monitoringToUpdate = ProjectContractingMonitoring(
            projectId = projectId,
            startDate = ZonedDateTime.parse("2022-07-02T10:00:00+02:00").toLocalDate(),
            endDate = null,
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
            )),
            dimensionCodes = emptyList()
        )
        val monitoringEntityToUpdate = ProjectContractingMonitoringEntity(
            projectId = projectId,
            startDate = ZonedDateTime.parse("2022-07-02T10:00:00+02:00").toLocalDate(),
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
