package io.cloudflight.jems.server.project.controller.contracting.reporting

import io.cloudflight.jems.api.project.dto.contracting.reporting.ContractingDeadlineTypeDTO
import io.cloudflight.jems.api.project.dto.contracting.reporting.ProjectContractingReportingScheduleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.contracting.reporting.getContractingReporting.GetContractingReportingInteractor
import io.cloudflight.jems.server.project.service.contracting.reporting.updateContractingReporting.UpdateContractingReportingInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ContractingReportingControllerTest: UnitTest() {

    companion object {

        private val YESTERDAY = LocalDate.now().minusDays(1)

        private val reportingSchedule = ProjectContractingReportingSchedule(
            id = 18L,
            type = ContractingDeadlineType.Both,
            periodNumber = 4,
            date = YESTERDAY,
            comment = "dummy comment",
        )

        private val reportingScheduleDto = ProjectContractingReportingScheduleDTO(
            id = 18L,
            type = ContractingDeadlineTypeDTO.Both,
            periodNumber = 4,
            date = YESTERDAY,
            comment = "dummy comment",
        )

        private val toCreateSchedule = ProjectContractingReportingScheduleDTO(
            id = null,
            type = ContractingDeadlineTypeDTO.Finance,
            periodNumber = 2,
            date = YESTERDAY,
            comment = "dummy comment",
        )

        private val toCreateScheduleModel = ProjectContractingReportingSchedule(
            id = 0L,
            type = ContractingDeadlineType.Finance,
            periodNumber = 2,
            date = YESTERDAY,
            comment = "dummy comment",
        )
    }

    @MockK
    lateinit var getContractingReportingInteractor: GetContractingReportingInteractor
    @MockK
    lateinit var updateContractingReportingInteractor: UpdateContractingReportingInteractor

    @InjectMockKs
    lateinit var controller: ContractingReportingController

    @Test
    fun getReportingSchedules() {
        val projectId = 10L
        every { getContractingReportingInteractor.getReportingSchedule(projectId) } returns
            listOf(reportingSchedule)
        assertThat(controller.getReportingSchedule(projectId))
            .containsExactly(reportingScheduleDto)
    }

    @Test
    fun updateReportingSchedule() {
        val projectId = 15L

        val updateModelSlot = slot<Collection<ProjectContractingReportingSchedule>>()
        every {
            updateContractingReportingInteractor.updateReportingSchedule(projectId, capture(updateModelSlot))
        } returns listOf(reportingSchedule)

        val toCreate = listOf(toCreateSchedule, toCreateSchedule)
        assertThat(controller.updateReportingSchedule(projectId, toCreate))
            .containsExactly(reportingScheduleDto)

        assertThat(updateModelSlot.captured).hasSize(2)
        assertThat(updateModelSlot.captured).containsExactly(toCreateScheduleModel, toCreateScheduleModel)
    }
}
