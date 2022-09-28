package io.cloudflight.jems.server.project.controller.contracting.partner.beneficialOwner

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerBeneficialOwnerDTO
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val beneficialOwnerMapper = Mappers.getMapper(ContractingPartnerBeneficialOwnerMapper::class.java)

fun List<ContractingPartnerBeneficialOwner>.toDto() = map { it.toDto() }

fun ContractingPartnerBeneficialOwner.toDto() = beneficialOwnerMapper.map(this)

fun List<ContractingPartnerBeneficialOwnerDTO>.toModel() = map { it.toModel() }

fun ContractingPartnerBeneficialOwnerDTO.toModel() = ContractingPartnerBeneficialOwner(
    id = id ?: 0L,
    partnerId = partnerId,
    firstName = firstName,
    lastName = lastName,
    vatNumber = vatNumber,
    birth = birth
)

@Mapper
interface ContractingPartnerBeneficialOwnerMapper {
    fun map(model: ContractingPartnerBeneficialOwner): ContractingPartnerBeneficialOwnerDTO
}
