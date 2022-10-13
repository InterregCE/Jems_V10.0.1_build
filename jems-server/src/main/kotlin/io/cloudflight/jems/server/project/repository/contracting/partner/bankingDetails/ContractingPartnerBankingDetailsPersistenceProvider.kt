package io.cloudflight.jems.server.project.repository.contracting.partner.bankingDetails

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetailsPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails.GetContractingPartnerBankingDetailsPartnerNotFoundException
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.updateBankingDetails.UpdateContractingPartnerBankingDetailsNotAllowedException
import io.cloudflight.jems.server.project.service.projectContractingPartnerBankingDetailsChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingPartnerBankingDetailsPersistenceProvider(
    private val contractingPartnerBankingDetailsRepository: ContractingPartnerBankingDetailsRepository,
    private val partnerRepository: ProjectPartnerRepository,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectPersistence: ProjectPersistenceProvider,
) : ContractingPartnerBankingDetailsPersistence {

    @Transactional(readOnly = true)
    override fun getBankingDetails(partnerId: Long): ContractingPartnerBankingDetails? {
        partnerRepository.findById(partnerId)
            .orElseThrow { GetContractingPartnerBankingDetailsPartnerNotFoundException() }
        return contractingPartnerBankingDetailsRepository.findByPartnerId(partnerId)?.toModel()
    }

    @Transactional
    override fun updateBankingDetails(
        partnerId: Long,
        projectId: Long,
        bankingDetails: ContractingPartnerBankingDetails
    ): ContractingPartnerBankingDetails {
        val projectPartner =
            partnerRepository.findById(partnerId).orElseThrow { ResourceNotFoundException("project_partner") }
        if (projectPartner == null || projectPartner.project.id != projectId) {
            throw UpdateContractingPartnerBankingDetailsNotAllowedException()
        }
        val currentBankingDetails = getBankingDetails(partnerId)
        val projectSummary = projectPersistence.getProjectSummary(projectId)

        return contractingPartnerBankingDetailsRepository.save(bankingDetails.toEntity()).toModel().also {
            if (projectSummary.status.isAlreadyContracted()) {
                auditPublisher.publishEvent(
                    projectContractingPartnerBankingDetailsChanged(
                        this,
                        projectSummary,
                        projectPartner,
                        currentBankingDetails,
                        bankingDetails
                    )
                )
            }
        }
    }
}