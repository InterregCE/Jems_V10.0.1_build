package io.cloudflight.jems.server.project.controller.contracting.monitoring

import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingMonitoringDTO
import io.cloudflight.jems.api.project.dto.contracting.ProjectPeriodForMonitoringDTO
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectPeriodForMonitoring
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val contractingMonitoringMapper = Mappers.getMapper(ContractingMonitoringDTOMapper::class.java)

fun ProjectContractingMonitoring.toDTO() = contractingMonitoringMapper.map(this)

fun ProjectContractingMonitoringDTO.toModel() = contractingMonitoringMapper.map(this)

fun List<ProjectPeriodForMonitoring>.toDTO() = map { contractingMonitoringMapper.map(it) }

@Mapper
interface ContractingMonitoringDTOMapper {

    fun map(model: ProjectContractingMonitoring): ProjectContractingMonitoringDTO

    fun map(dto: ProjectContractingMonitoringDTO): ProjectContractingMonitoring

    fun map(model: ProjectPeriodForMonitoring): ProjectPeriodForMonitoringDTO

}
