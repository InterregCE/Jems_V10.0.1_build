package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.updateDocumentsLocation

import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation

interface UpdateContractingPartnerDocumentsLocationInteractor {
    fun updateDocumentsLocation(
        projectId: Long,
        partnerId: Long,
        documentsLocation: ContractingPartnerDocumentsLocation
    ): ContractingPartnerDocumentsLocation
}