package io.cloudflight.jems.server.project.controller.contracting.monitoring

import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingMonitoringDTO
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val contractingMonitoringMapper = Mappers.getMapper(ContractingMonitoringDTOMapper::class.java)

fun ProjectContractingMonitoring.toDTO() = contractingMonitoringMapper.map(this)

fun ProjectContractingMonitoringDTO.toModel() = contractingMonitoringMapper.map(this)

@Mapper
interface ContractingMonitoringDTOMapper {

    fun map(model: ProjectContractingMonitoring): ProjectContractingMonitoringDTO

    fun map(dto: ProjectContractingMonitoringDTO): ProjectContractingMonitoring

}
