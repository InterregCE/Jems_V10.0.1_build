package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.getDocumentsLocation

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingPartner
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingPartnerDocumentsLocation(
    private val getContractingPartnerDocumentsLocationService: GetContractingPartnerDocumentsLocationService,
): GetContractingPartnerDocumentsLocationInteractor {

    @CanRetrieveProjectContractingPartner
    @ExceptionWrapper(GetContractingPartnerDocumentsLocationException::class)
    override fun getDocumentsLocation(partnerId: Long): ContractingPartnerDocumentsLocation =
        getContractingPartnerDocumentsLocationService.getDocumentsLocation(partnerId)

}
