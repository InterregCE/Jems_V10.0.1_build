package io.cloudflight.jems.server.project.controller.contracting.management

import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingManagementDTO
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val contractingManagementMapper = Mappers.getMapper(ContractingManagementDTOMapper::class.java)

fun List<ProjectContractingManagement>.toDTO() = map {contractingManagementMapper.map(it)}

fun List<ProjectContractingManagementDTO>.toModel() = map { contractingManagementMapper.map(it)}

@Mapper
interface ContractingManagementDTOMapper {

    fun map(model: ProjectContractingManagement): ProjectContractingManagementDTO

    fun map(dto: ProjectContractingManagementDTO): ProjectContractingManagement
}
