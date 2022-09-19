package io.cloudflight.jems.server.project.controller.contracting.contracts

import io.cloudflight.jems.api.project.dto.contracting.ProjectContractInfoDTO
import io.cloudflight.jems.api.project.dto.contracting.ContractInfoUpdateDTO
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ContractsInfoDTOMapper::class.java)

fun ProjectContractInfo.toDTO() = mapper.map(this)

fun ProjectContractInfo.toUpdateContractInfoDTO() = mapper.toUpdateContractInfoDTO(this)

fun ContractInfoUpdateDTO.toModel() = mapper.map(this)

@Mapper
abstract class ContractsInfoDTOMapper {

   abstract fun map(model: ProjectContractInfo): ProjectContractInfoDTO

    @Mappings(
        Mapping(target = "projectStartDate", ignore = true),
        Mapping(target = "projectEndDate", ignore = true),
        Mapping(target = "subsidyContractDate", ignore = true),
    )
   abstract fun map(dto: ContractInfoUpdateDTO): ProjectContractInfo

   fun toUpdateContractInfoDTO(model: ProjectContractInfo): ContractInfoUpdateDTO =
       ContractInfoUpdateDTO(website = model.website, partnershipAgreementDate = model.partnershipAgreementDate)
}
