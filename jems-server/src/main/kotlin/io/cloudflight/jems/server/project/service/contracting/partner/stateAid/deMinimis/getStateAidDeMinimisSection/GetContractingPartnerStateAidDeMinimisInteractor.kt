package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection

import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimisSection

interface GetContractingPartnerStateAidDeMinimisInteractor {

    fun getDeMinimisSection(partnerId: Long): ContractingPartnerStateAidDeMinimisSection?
}
