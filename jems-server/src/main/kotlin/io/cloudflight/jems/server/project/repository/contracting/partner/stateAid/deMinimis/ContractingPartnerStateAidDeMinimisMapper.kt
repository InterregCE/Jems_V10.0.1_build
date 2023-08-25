package io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.deMinimis

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidMinimisEntity
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidGrantedByMemberStateEntity
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimis
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.MemberStateForGranting
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import java.util.Optional


private val mapper = Mappers.getMapper(ContractingPartnerStateAidDeMinimisMapper::class.java)

fun ProjectContractingPartnerStateAidMinimisEntity.toModel() = ContractingPartnerStateAidDeMinimis(
    selfDeclarationSubmissionDate = selfDeclarationSubmissionDate,
    baseForGranting = baseForGranting,
    aidGrantedByCountry = aidGrantedByCountry,
    memberStatesGranting = memberStatesGranting.toModel(),
    comment = comment,
    amountGrantingAid = amountGrantingAid
)
fun Set<ProjectContractingPartnerStateAidGrantedByMemberStateEntity>.toModel() = mapTo(HashSet()) { it.toModel() }
fun ProjectContractingPartnerStateAidGrantedByMemberStateEntity.toModel() = mapper.map(this)

@Mapper
interface ContractingPartnerStateAidDeMinimisMapper {
    fun map(entity: Optional<ProjectContractingPartnerStateAidMinimisEntity>): ContractingPartnerStateAidDeMinimis
    fun map(model: MemberStateForGranting): ProjectContractingPartnerStateAidGrantedByMemberStateEntity

    @Mappings(
        Mapping(target = "partnerId", source = "entity.id.partnerId"),
        Mapping(target = "countryCode", source = "entity.id.countryCode"),
        Mapping(target = "amountInEur", source = "entity.amount")
    )
    fun map(entity: ProjectContractingPartnerStateAidGrantedByMemberStateEntity): MemberStateForGranting
}
