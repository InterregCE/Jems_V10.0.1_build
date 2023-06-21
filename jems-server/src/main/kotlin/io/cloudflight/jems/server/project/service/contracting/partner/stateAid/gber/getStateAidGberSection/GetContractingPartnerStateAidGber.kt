package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAidGberSection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingPartner
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGberSection
import org.springframework.stereotype.Service

@Service
class GetContractingPartnerStateAidGber(
    private val getContractingPartnerStateAidGberService: GetContractingPartnerStateAidGberService,
): GetContractingPartnersStateAidGberInteractor {

    @CanRetrieveProjectContractingPartner
    @ExceptionWrapper(GetContractingPartnerStateAidGberException::class)
    override fun getGberSection(partnerId: Long): ContractingPartnerStateAidGberSection? =
        getContractingPartnerStateAidGberService.getGberSection(partnerId)
}
