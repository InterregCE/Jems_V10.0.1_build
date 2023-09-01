package io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.deMinimis

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidMinimisEntity
import io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.toEntities
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimis
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.ContractingPartnerStateAidDeMinimisPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingPartnerStateAidDeMinimisPersistenceProvider(
    private val repository: ContractingPartnerStateAidDeMinimisRepository
): ContractingPartnerStateAidDeMinimisPersistence {

    @Transactional(readOnly = true)
    override fun findById(partnerId: Long): ContractingPartnerStateAidDeMinimis? =
       this.repository.findById(partnerId).map{ it.toModel() }.orElse(null)

    @Transactional
    override fun saveDeMinimis(
        partnerId: Long,
        minimisData: ContractingPartnerStateAidDeMinimis
    ): ProjectContractingPartnerStateAidMinimisEntity =
        this.repository.save(
            ProjectContractingPartnerStateAidMinimisEntity(
                partnerId = partnerId,
                selfDeclarationSubmissionDate = minimisData.selfDeclarationSubmissionDate,
                baseForGranting = minimisData.baseForGranting,
                aidGrantedByCountry = minimisData.aidGrantedByCountry,
                memberStatesGranting = minimisData.memberStatesGranting.toEntities(),
                comment = minimisData.comment,
                amountGrantingAid = minimisData.amountGrantingAid,
            )
        )

}
