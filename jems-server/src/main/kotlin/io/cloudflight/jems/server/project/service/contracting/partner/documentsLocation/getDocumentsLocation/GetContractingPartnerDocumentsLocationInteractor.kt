package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.getDocumentsLocation

import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation

interface GetContractingPartnerDocumentsLocationInteractor {
    fun getDocumentsLocation(partnerId: Long): ContractingPartnerDocumentsLocation
}
