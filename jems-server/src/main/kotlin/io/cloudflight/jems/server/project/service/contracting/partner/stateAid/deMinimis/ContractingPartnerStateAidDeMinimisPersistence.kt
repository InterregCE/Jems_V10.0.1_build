package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidMinimisEntity
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimis
import org.springframework.stereotype.Repository

@Repository
interface ContractingPartnerStateAidDeMinimisPersistence {

    fun findById(partnerId: Long): ContractingPartnerStateAidDeMinimis?

    fun saveDeMinimis(
        partnerId: Long,
        minimisData: ContractingPartnerStateAidDeMinimis
    ): ProjectContractingPartnerStateAidMinimisEntity
}
