package io.cloudflight.jems.server.project.controller.contracting.reporting

import io.cloudflight.jems.api.project.dto.contracting.reporting.ProjectContractingReportingScheduleDTO
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val contractingReportingMapper = Mappers.getMapper(ContractingReportingMapper::class.java)

fun List<ProjectContractingReportingSchedule>.toDto() = map { it.toDto() }
fun ProjectContractingReportingSchedule.toDto() = contractingReportingMapper.map(this)

fun List<ProjectContractingReportingScheduleDTO>.toModel() = map { it.toModel() }
fun ProjectContractingReportingScheduleDTO.toModel() = ProjectContractingReportingSchedule(
    id = id ?: 0L,
    type = ContractingDeadlineType.valueOf(type.name),
    periodNumber = periodNumber,
    date = date,
    comment = comment,
)

@Mapper
interface ContractingReportingMapper {

    fun map(model: ProjectContractingReportingSchedule): ProjectContractingReportingScheduleDTO

}
