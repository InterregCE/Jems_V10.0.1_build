package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation

interface ContractingPartnerDocumentsLocationPersistence {

    fun getDocumentsLocation(partnerId: Long): ContractingPartnerDocumentsLocation

    fun updateDocumentsLocation(
        projectId: Long,
        partnerId: Long,
        documentsLocation: ContractingPartnerDocumentsLocation
    ): ContractingPartnerDocumentsLocation
}