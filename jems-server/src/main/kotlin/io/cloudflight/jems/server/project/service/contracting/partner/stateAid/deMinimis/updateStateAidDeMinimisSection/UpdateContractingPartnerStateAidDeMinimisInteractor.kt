package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.updateStateAidDeMinimisSection

import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimis
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimisSection

interface UpdateContractingPartnerStateAidDeMinimisInteractor {

    fun updateDeMinimisSection(
        partnerId: Long,
        deMinimisData: ContractingPartnerStateAidDeMinimis
    ): ContractingPartnerStateAidDeMinimisSection
}
