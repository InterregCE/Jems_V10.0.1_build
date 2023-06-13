package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidGberEntity
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGber
import org.springframework.stereotype.Repository

@Repository
interface ContractingPartnerStateAidGberPersistence {

    fun findById(partnerId: Long): ContractingPartnerStateAidGber?

    fun saveGber(partnerId: Long, gberData: ContractingPartnerStateAidGber): ProjectContractingPartnerStateAidGberEntity
}
