package io.cloudflight.jems.server.project.repository.contracting.monitoring

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import io.cloudflight.jems.server.project.entity.contracting.*
import io.cloudflight.jems.server.project.repository.contracting.partner.lastPayment.ContractingPartnerPaymentDateRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.contracting.model.*
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureLastPaymentDate
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*

internal class ContractingMonitoringPersistenceProviderTest: UnitTest() {

    companion object {
        const val projectId = 2L

        private val monitoring = ProjectContractingMonitoring(
            projectId = projectId,
            startDate = ZonedDateTime.parse("2022-07-01T10:00:00+02:00").toLocalDate(),
            endDate = null,
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
            closureDate = LocalDate.of(2024, 1, 24),
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

    @MockK private lateinit var projectContractingMonitoringRepository: ProjectContractingMonitoringRepository
    @MockK private lateinit var contractingPartnerPaymentDateRepository: ContractingPartnerPaymentDateRepository
    @MockK private lateinit var partnerRepository: ProjectPartnerRepository

    @InjectMockKs
    lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistenceProvider

    @Test
    fun `getContractingMonitoring - fetched`() {
        every {
            projectContractingMonitoringRepository.findByProjectId(projectId)
        } returns Optional.of(monitoringEntity)

        assertThat(contractingMonitoringPersistence.getContractingMonitoring(projectId))
            .isEqualTo(monitoring)
    }

    @Test
    fun `getContractingMonitoring - empty`() {
        every { projectContractingMonitoringRepository.findByProjectId(projectId) } returns Optional.empty()

        assertThat(contractingMonitoringPersistence.getContractingMonitoring(projectId))
            .isEqualTo(ProjectContractingMonitoring(projectId = projectId, addDates = emptyList(), dimensionCodes = emptyList(),
                typologyProv94 = ContractingMonitoringExtendedOption.No, typologyProv95 = ContractingMonitoringExtendedOption.No,
                typologyStrategic = ContractingMonitoringOption.No, typologyPartnership = ContractingMonitoringOption.No,
                lastPaymentDates = emptyList(),
            ))
    }

    @Test
    fun getPartnerPaymentDate() {
        val projectId = 445L
        every { contractingPartnerPaymentDateRepository.findAllByPartnerProjectId(projectId) } returns listOf(
            ProjectContractingPartnerPaymentDateEntity(945L, mockk(), LocalDate.of(2025, 3, 18)),
            ProjectContractingPartnerPaymentDateEntity(946L, mockk(), LocalDate.of(2026, 6, 30)),
        )
        assertThat(contractingMonitoringPersistence.getPartnerPaymentDate(projectId)).containsExactlyEntriesOf(mapOf(
            945L to LocalDate.of(2025, 3, 18),
            946L to LocalDate.of(2026, 6, 30),
        ))
    }

    @Test
    fun updateClosureDate() {
        val projectId = 447L
        val newDate = LocalDate.of(2028, 4, 22)
        val entity = ProjectContractingMonitoringEntity(
            projectId, mockk(), closureDate = LocalDate.of(1994, 5, 14), ContractingMonitoringExtendedOption.No,
            null, ContractingMonitoringExtendedOption.Yes, null, ContractingMonitoringOption.Yes,
            null, ContractingMonitoringOption.No, null, emptyList(), emptyList(),
        )
        assertThat(entity.closureDate).isNotEqualTo(newDate)
        every { projectContractingMonitoringRepository.findByProjectId(projectId) } returns Optional.of(entity)

        assertThat(contractingMonitoringPersistence.updateClosureDate(projectId, newDate)).isEqualTo(newDate)
        assertThat(entity.closureDate).isEqualTo(newDate)
    }

    @Test
    fun `updateClosureDate - missing contracting`() {
        val projectId = 449L
        val newDate = LocalDate.of(2028, 12, 31)

        every { projectContractingMonitoringRepository.findByProjectId(projectId) } returns Optional.empty()

        assertThat(contractingMonitoringPersistence.updateClosureDate(projectId, newDate)).isNull()
    }

    @Test
    fun updatePartnerPaymentDate() {
        val projectId = 452L
        val entity184 = ProjectContractingPartnerPaymentDateEntity(184L, mockk(), LocalDate.of(2024, 1, 24))
        val entity185 = ProjectContractingPartnerPaymentDateEntity(185L, mockk(), LocalDate.of(2025, 2, 25))
        val entity186 = ProjectContractingPartnerPaymentDateEntity(186L, mockk(), LocalDate.of(2026, 3, 26))
        every { contractingPartnerPaymentDateRepository.findAllByPartnerProjectId(projectId) } returns
                listOf(entity184, entity185, entity186)

        val toDeleteSlot = slot<Iterable<ProjectContractingPartnerPaymentDateEntity>>()
        every { contractingPartnerPaymentDateRepository.deleteAll(capture(toDeleteSlot)) } answers { }

        every { partnerRepository.getById(187L) } returns mockk()

        every { contractingPartnerPaymentDateRepository.save(any()) } returnsArgument 0

        val toSave = mapOf(
            185L to LocalDate.of(1999, 9, 19),
            186L to null,
            187L to LocalDate.of(1998, 8, 18),
        )
        assertThat(contractingMonitoringPersistence.updatePartnerPaymentDate(projectId, toSave))
            .containsExactlyEntriesOf(mapOf(
                185L to LocalDate.of(1999, 9, 19),
                187L to LocalDate.of(1998, 8, 18),
            ))

        assertThat(toDeleteSlot.captured).containsExactlyInAnyOrder(entity184, entity186)
        assertThat(entity185.lastPaymentDate).isEqualTo(LocalDate.of(1999, 9, 19))
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
            closureDate = LocalDate.of(2024, 1, 24),
            lastPaymentDates = listOf(
                ContractingClosureLastPaymentDate(774L, 14, "774-abbr",
                    ProjectPartnerRole.PARTNER, false, LocalDate.of(2025, 3, 18)),
            ),
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
            dimensionCodes = listOf(
                ContractingDimensionCode(
                    id = 2L,
                    projectId = projectId,
                    programmeObjectiveDimension = ProgrammeObjectiveDimension.RegionalAndSeaBasinStrategy,
                    dimensionCode = "003",
                    projectBudgetAmountShare = BigDecimal(100),
                ),
            ),
        )

        every { projectContractingMonitoringRepository.save(capture(monitoringSlot)) } returnsArgument 0
        val monitoringUpdated = contractingMonitoringPersistence.updateContractingMonitoring(monitoringToUpdate)

        assertThat(monitoringUpdated).isEqualTo(monitoringToUpdate.copy(
            lastPaymentDates = emptyList(),
        ))

        assertThat(monitoringSlot.captured.projectId).isEqualTo(projectId)
        assertThat(monitoringSlot.captured.startDate).isEqualTo(ZonedDateTime.parse("2022-07-02T10:00:00+02:00").toLocalDate())
        assertThat(monitoringSlot.captured.closureDate).isEqualTo(LocalDate.of(2024, 1, 24))
        assertThat(monitoringSlot.captured.typologyProv94).isEqualTo(ContractingMonitoringExtendedOption.No)
        assertThat(monitoringSlot.captured.typologyProv94Comment).isEqualTo("typologyProv94Comment")
        assertThat(monitoringSlot.captured.typologyProv95).isEqualTo(ContractingMonitoringExtendedOption.Partly)
        assertThat(monitoringSlot.captured.typologyProv95Comment).isEqualTo("typologyProv95Comment")
    }
}
