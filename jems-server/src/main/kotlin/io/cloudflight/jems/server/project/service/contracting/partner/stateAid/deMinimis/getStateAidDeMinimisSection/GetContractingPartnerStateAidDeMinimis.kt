package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingPartner
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimisSection
import org.springframework.stereotype.Service

@Service
class GetContractingPartnerStateAidDeMinimis(
    private val getContractingPartnerStateAidDeMinimisService: GetContractingPartnerStateAidDeMinimisService,
 ): GetContractingPartnerStateAidDeMinimisInteractor {

    @CanRetrieveProjectContractingPartner
    @ExceptionWrapper(GetContractingPartnerStateAidDeMinimisException::class)
    override fun getDeMinimisSection(partnerId: Long): ContractingPartnerStateAidDeMinimisSection? =
        getContractingPartnerStateAidDeMinimisService.getDeMinimisSection(partnerId)

}
