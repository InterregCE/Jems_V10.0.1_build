package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAidGberSection

import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGberSection

interface GetContractingPartnersStateAidGberInteractor {
    fun getGberSection(partnerId: Long): ContractingPartnerStateAidGberSection?
}
