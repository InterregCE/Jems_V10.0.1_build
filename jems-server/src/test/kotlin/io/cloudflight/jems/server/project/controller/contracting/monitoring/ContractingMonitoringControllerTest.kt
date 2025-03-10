package io.cloudflight.jems.server.project.controller.contracting.monitoring

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectiveDimensionDTO
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.contracting.*
import io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate.ContractingClosureDTO
import io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate.ContractingClosureLastPaymentDateDTO
import io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate.ContractingClosureLastPaymentDateUpdateDTO
import io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate.ContractingClosureUpdateDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.model.*
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosure
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureLastPaymentDate
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureUpdate
import io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringProjectBudget.GetContractingMonitoringProjectBudgetInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringStartDate.GetContractingMonitoringStartDateException
import io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringStartDate.GetContractingMonitoringStartDateInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.getLastApprovedPeriods.GetLastApprovedPeriodsInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring.UpdateContractingMonitoringException
import io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring.UpdateContractingMonitoringInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingPartnerPaymentDate.UpdateContractingPartnerPaymentDateInteractor
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
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
            closureDate = LocalDate.of(2024, 1, 24),
            lastPaymentDates = listOf(
                ContractingClosureLastPaymentDateDTO(774L, 14, "774-abbr",
                    ProjectPartnerRoleDTO.PARTNER, false, LocalDate.of(2025, 3, 18)),
            ),
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

        private val dummyPeriod = ProjectPeriod(
            number = 1,
            start = 1,
            end = 6,
            startDate = yesterday,
            endDate = tomorrow,
        )

        private val dummyPeriodExpected = ProjectPeriodDTO(
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
    private lateinit var updateContractingPartnerPaymentDate: UpdateContractingPartnerPaymentDateInteractor

    @MockK
    lateinit var getLastApprovedPeriodsInteractor: GetLastApprovedPeriodsInteractor

    @MockK
    lateinit var getStartDateInteractor: GetContractingMonitoringStartDateInteractor

    @MockK
    lateinit var getContractingMonitoringProjectBudgetInteractor: GetContractingMonitoringProjectBudgetInteractor

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
    fun updateContractingPartnerPaymentDate() {
        val projectId = 554L
        val closureSlot = slot<ContractingClosureUpdate>()
        every {
            updateContractingPartnerPaymentDate.updatePartnerPaymentDate(projectId, capture(closureSlot))
        } returns ContractingClosure(monitoring.closureDate, monitoring.lastPaymentDates)

        val toUpdate = ContractingClosureUpdateDTO(monitoring.closureDate,
            listOf(
                ContractingClosureLastPaymentDateUpdateDTO(774L, LocalDate.of(2025, 3, 18)),
                ContractingClosureLastPaymentDateUpdateDTO(-1L, null),
            ),
        )
        assertThat(contractingMonitoringController.updateContractingPartnerPaymentDate(projectId, toUpdate))
            .isEqualTo(ContractingClosureDTO(monitoringDTO.closureDate, monitoringDTO.lastPaymentDates))
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

    @Test
    fun getContractingMonitoringProjectBudgetInteractor() {
        every {
            getContractingMonitoringProjectBudgetInteractor.getProjectBudget(projectId, "1.0")
        } returns BigDecimal(1055)

        assertThat(contractingMonitoringController.getContractingMonitoringProjectBudget(projectId, "1.0"))
            .isEqualTo(BigDecimal(1055))
    }
}
