package io.cloudflight.jems.server.project.controller.contracting.partner.stateAid

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidDeMinimisSectionDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidGberSectionDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.LocationInAssistedAreaDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.MemberStateForGrantingDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidDeMinimisDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidGberDTO
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimis
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimisSection
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGber
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGberSection
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.LocationInAssistedArea
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.MemberStateForGranting
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ContractingPartnerStateAidMapper::class.java)

fun ContractingPartnerStateAidGberSection.toDto() = mapper.map(this)
fun ContractingPartnerStateAidDeMinimisSection.toDto() = mapper.map(this)
fun MemberStateForGranting.toDto() = mapper.map(this)
fun LocationInAssistedArea.toDto() = mapper.map(this)
fun ContractingPartnerStateAidGberDTO.toModel() = mapper.map(this)
fun List<MemberStateForGrantingDTO>.toModel() = map { it.toStateForGrantingModel() }
fun MemberStateForGrantingDTO.toStateForGrantingModel() = mapper.mapToModel(this)
fun ContractingPartnerStateAidDeMinimisDTO.toModel() = mapper.mapToModel(this)

@Mapper
interface ContractingPartnerStateAidMapper {
     fun map(model: ContractingPartnerStateAidGberSection): ContractingPartnerStateAidGberSectionDTO
     fun map(model: ContractingPartnerStateAidDeMinimisSection): ContractingPartnerStateAidDeMinimisSectionDTO
     fun map(model: MemberStateForGranting): MemberStateForGrantingDTO
     fun mapToModel(dto: MemberStateForGrantingDTO): MemberStateForGranting
     fun map(model: LocationInAssistedArea): LocationInAssistedAreaDTO
     fun map(dto: ContractingPartnerStateAidGberDTO): ContractingPartnerStateAidGber
     fun mapToModel(dto: ContractingPartnerStateAidDeMinimisDTO): ContractingPartnerStateAidDeMinimis
}
