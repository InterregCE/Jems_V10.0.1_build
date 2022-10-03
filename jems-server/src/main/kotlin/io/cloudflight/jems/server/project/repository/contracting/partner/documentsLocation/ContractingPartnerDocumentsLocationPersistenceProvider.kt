package io.cloudflight.jems.server.project.repository.contracting.partner.documentsLocation

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocationPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingPartnerDocumentsLocationPersistenceProvider(
    private val documentsLocationRepository: ContractingPartnerDocumentsLocationRepository,
    private val partnerRepository: ProjectPartnerRepository
): ContractingPartnerDocumentsLocationPersistence {

    @Transactional(readOnly = true)
    override fun getDocumentsLocation(partnerId: Long): ContractingPartnerDocumentsLocation {
        return documentsLocationRepository
            .findByProjectPartnerId(partnerId)
            .let {
                when {
                    it.isPresent -> it.get().toModel()
                    else -> ContractingPartnerDocumentsLocation(partnerId = partnerId)
                }
            }
    }

    @Transactional
    override fun updateDocumentsLocation(
        partnerId: Long,
        documentsLocation: ContractingPartnerDocumentsLocation
    ): ContractingPartnerDocumentsLocation {
        val projectPartner = partnerRepository.findById(partnerId).orElseThrow { ResourceNotFoundException("project_partner") }
        return documentsLocationRepository.save(documentsLocation.toEntity(projectPartner)).toModel()
    }
}
