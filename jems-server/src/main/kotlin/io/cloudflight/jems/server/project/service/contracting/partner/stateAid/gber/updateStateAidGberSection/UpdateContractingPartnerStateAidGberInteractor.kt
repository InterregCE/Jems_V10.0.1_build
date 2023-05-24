package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.updateStateAidGberSection

import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGber
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGberSection

interface UpdateContractingPartnerStateAidGberInteractor {

    fun updateGberSection(
        partnerId: Long,
        gberData: ContractingPartnerStateAidGber
    ): ContractingPartnerStateAidGberSection
}
