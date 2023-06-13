package io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.gber

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidGberEntity
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGber
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.ContractingPartnerStateAidGberPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingPartnerStateAidGberPersistenceProvider(
    private val repository: ContractingPartnerStateAidGberRepository
): ContractingPartnerStateAidGberPersistence {

    @Transactional(readOnly = true)
    override fun findById(partnerId: Long): ContractingPartnerStateAidGber? =
        this.repository.findById(partnerId).map{ it.toModel() }.orElse(null)

    @Transactional
    override fun saveGber(
        partnerId: Long,
        gberData: ContractingPartnerStateAidGber
    ): ProjectContractingPartnerStateAidGberEntity =
        this.repository.save(ProjectContractingPartnerStateAidGberEntity(
            partnerId = partnerId,
            aidIntensity = gberData.aidIntensity,
            locationInAssistedArea = gberData.locationInAssistedArea,
            comment = gberData.comment
        ))
}
