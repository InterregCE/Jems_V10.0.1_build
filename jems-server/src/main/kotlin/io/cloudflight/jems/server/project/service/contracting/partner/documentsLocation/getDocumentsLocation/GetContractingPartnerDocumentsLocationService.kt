package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.getDocumentsLocation

import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingPartnerDocumentsLocationService(
    private val documentsLocationPersistence: ContractingPartnerDocumentsLocationPersistence
) {

    @Transactional(readOnly = true)
    fun getDocumentsLocation(partnerId: Long): ContractingPartnerDocumentsLocation =
        documentsLocationPersistence.getDocumentsLocation(partnerId)
}
