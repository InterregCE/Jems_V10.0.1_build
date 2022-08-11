package io.cloudflight.jems.server.project.controller.contracting.monitoring

import io.cloudflight.jems.api.project.dto.contracting.ContractingMonitoringExtendedOptionDTO
import io.cloudflight.jems.api.project.dto.contracting.ContractingMonitoringOptionDTO
import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingMonitoringAddDateDTO
import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingMonitoringDTO
import io.cloudflight.jems.api.project.dto.contracting.ProjectPeriodForMonitoringDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringOption
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringAddDate
import io.cloudflight.jems.server.project.service.contracting.model.ProjectPeriodForMonitoring
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
            ))
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
            ))
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
    }

    @MockK
    lateinit var getContractingMonitoringInteractor: GetContractingMonitoringInteractor

    @MockK
    lateinit var updateContractingMonitoringInteractor: UpdateContractingMonitoringInteractor

    @MockK
    lateinit var getLastApprovedPeriodsInteractor: GetLastApprovedPeriodsInteractor

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

}
