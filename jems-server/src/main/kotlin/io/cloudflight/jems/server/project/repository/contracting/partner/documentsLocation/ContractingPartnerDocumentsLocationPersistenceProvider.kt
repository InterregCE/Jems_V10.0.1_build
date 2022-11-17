package io.cloudflight.jems.server.project.repository.contracting.partner.documentsLocation

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocationPersistence
import io.cloudflight.jems.server.project.service.projectContractingPartnerDocumentsLocationChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingPartnerDocumentsLocationPersistenceProvider(
    private val documentsLocationRepository: ContractingPartnerDocumentsLocationRepository,
    private val partnerRepository: ProjectPartnerRepository,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectPersistence: ProjectPersistenceProvider
) : ContractingPartnerDocumentsLocationPersistence {

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
        projectId: Long,
        partnerId: Long,
        documentsLocation: ContractingPartnerDocumentsLocation
    ): ContractingPartnerDocumentsLocation {
        val projectPartner =
            partnerRepository.findById(partnerId).orElseThrow { ResourceNotFoundException("project_partner") }
        val currentDocumentsLocation = getDocumentsLocation(partnerId)
        val projectSummary = projectPersistence.getProjectSummary(projectId)

        return documentsLocationRepository.save(documentsLocation.toEntity(projectPartner)).toModel().also {
            if (projectSummary.status.isAlreadyContracted()) {
                auditPublisher.publishEvent(
                    projectContractingPartnerDocumentsLocationChanged(
                        this,
                        projectSummary,
                        projectPartner,
                        currentDocumentsLocation,
                        documentsLocation
                    )
                )
            }
        }
    }
}