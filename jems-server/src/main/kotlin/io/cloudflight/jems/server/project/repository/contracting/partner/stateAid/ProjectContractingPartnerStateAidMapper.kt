package io.cloudflight.jems.server.project.repository.contracting.partner.stateAid

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidGrantedByMemberStateEntity
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingStateAidGrantedByMemberStateId
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.MemberStateForGranting


fun Set<ProjectContractingPartnerStateAidGrantedByMemberStateEntity>.toModel() = mapTo(HashSet()) {
    MemberStateForGranting(
        partnerId = it.id.partnerId,
        countryCode = it.id.countryCode,
        country = it.country,
        selected = it.selected,
        amountInEur = it.amount
    )
}

fun Set<MemberStateForGranting>.toEntities() = mapTo(HashSet()) {
    ProjectContractingPartnerStateAidGrantedByMemberStateEntity(
        id =  ProjectContractingStateAidGrantedByMemberStateId(partnerId = it.partnerId, countryCode = it.countryCode),
        country = it.country,
        selected = it.selected,
        amount = it.amountInEur
    )
}
