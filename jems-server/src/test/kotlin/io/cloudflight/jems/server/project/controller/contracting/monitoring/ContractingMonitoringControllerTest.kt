package io.cloudflight.jems.server.project.controller.contracting.monitoring

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectiveDimensionDTO
import io.cloudflight.jems.api.project.dto.contracting.*
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.model.*
import io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringStartDate.GetContractingMonitoringStartDateException
import io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringStartDate.GetContractingMonitoringStartDateInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.getLastApprovedPeriods.GetLastApprovedPeriodsInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring.UpdateContractingMonitoringException
import io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring.UpdateContractingMonitoringInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class ContractingMonitoringControllerTest: UnitTest() {

    companion object {
        private const val projectId = 2L
        private val yesterday = LocalDate.now().minusDays(1)
        private val tomorrow = LocalDate.now().plusDays(1)

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

        private val monitoringDTO = ProjectContractingMonitoringDTO(
            projectId = projectId,
            startDate = ZonedDateTime.parse("2022-07-01T10:00:00+02:00").toLocalDate(),
            endDate = ZonedDateTime.parse("2022-07-10T10:00:00+02:00").toLocalDate(),
            typologyProv94 = ContractingMonitoringExtendedOptionDTO.Partly,
            typologyProv94Comment = "typologyProv94Comment",
            typologyProv95 = ContractingMonitoringExtendedOptionDTO.Yes,
            typologyProv95Comment = "typologyProv95Comment",
            typologyStrategic = ContractingMonitoringOptionDTO.No,
            typologyStrategicComment = "typologyStrategicComment",
            typologyPartnership = ContractingMonitoringOptionDTO.Yes,
            typologyPartnershipComment = "typologyPartnershipComment",
            addDates = listOf(ProjectContractingMonitoringAddDateDTO(
                projectId = projectId,
                number = 1,
                entryIntoForceDate = ZonedDateTime.parse("2022-07-22T10:00:00+02:00").toLocalDate(),
                comment = "comment"
            )),
            dimensionCodes = listOf(
                ContractingDimensionCodeDTO(
                    id = 0,
                    projectId = projectId,
                    programmeObjectiveDimension = ProgrammeObjectiveDimensionDTO.TypesOfIntervention,
                    dimensionCode = "001",
                    projectBudgetAmountShare = BigDecimal(10000)
                )
            )
        )

        private val dummyPeriod = ProjectPeriodForMonitoring(
            number = 1,
            start = 1,
            end = 6,
            startDate = yesterday,
            endDate = tomorrow,
        )

        private val dummyPeriodExpected = ProjectPeriodForMonitoringDTO(
            number = 1,
            start = 1,
            end = 6,
            startDate = yesterday,
            endDate = tomorrow,
        )

        private val startDate = ProjectContractingMonitoringStartDate(
            yesterday
        )
    }

    @MockK
    lateinit var getContractingMonitoringInteractor: GetContractingMonitoringInteractor

    @MockK
    lateinit var updateContractingMonitoringInteractor: UpdateContractingMonitoringInteractor

    @MockK
    lateinit var getLastApprovedPeriodsInteractor: GetLastApprovedPeriodsInteractor

    @MockK
    lateinit var getStartDateInteractor: GetContractingMonitoringStartDateInteractor

    @InjectMockKs
    lateinit var contractingMonitoringController: ContractingMonitoringController

    @Test
    fun getContractingMonitoring() {
        every { getContractingMonitoringInteractor.getContractingMonitoring(projectId) } returns monitoring
        assertThat(contractingMonitoringController.getContractingMonitoring(projectId))
            .isEqualTo(monitoring.toDTO())
    }

    @Test
    fun updateContractingMonitoring() {
        val projectMonitoringSlot = slot<ProjectContractingMonitoring>()
        every {
            updateContractingMonitoringInteractor.updateContractingMonitoring(projectId, capture(projectMonitoringSlot))
        } returns monitoring

        assertThat(contractingMonitoringController.updateContractingMonitoring(projectId, monitoringDTO))
            .isEqualTo(monitoringDTO)
        assertThat(projectMonitoringSlot.captured).isEqualTo(monitoring)
    }

    @Test
    fun updateContractingMonitoringException() {
        every {
            updateContractingMonitoringInteractor.updateContractingMonitoring(projectId, monitoring)
        } throws UpdateContractingMonitoringException(ContractingModificationDeniedException())

        assertThrows<UpdateContractingMonitoringException> {
            contractingMonitoringController.updateContractingMonitoring(projectId, monitoringDTO)
        }
    }

    @Test
    fun getContractingMonitoringPeriods() {
        every { getLastApprovedPeriodsInteractor.getPeriods(projectId) } returns listOf(dummyPeriod)
        assertThat(contractingMonitoringController.getContractingMonitoringPeriods(projectId))
            .containsExactly(dummyPeriodExpected)
    }

    @Test
    fun getContractingMonitoringStartDate() {
        every { getStartDateInteractor.getStartDate(projectId) } returns startDate
        assertThat(contractingMonitoringController.getContractingMonitoringStartDate(projectId))
            .isEqualTo(ProjectContractingMonitoringStartDateDTO(yesterday))
    }

    @Test
    fun getContractingMonitoringStartDateException() {
        every {
            getStartDateInteractor.getStartDate(projectId)
        } throws GetContractingMonitoringStartDateException(RuntimeException())

        assertThrows<GetContractingMonitoringStartDateException> {
            contractingMonitoringController.getContractingMonitoringStartDate(projectId)
        }
    }
}
