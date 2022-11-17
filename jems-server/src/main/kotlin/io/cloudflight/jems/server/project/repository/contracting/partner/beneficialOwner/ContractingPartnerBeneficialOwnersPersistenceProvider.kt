package io.cloudflight.jems.server.project.repository.contracting.partner.beneficialOwner

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerBeneficialOwnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwnersPersistence
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectContractingPartnerBeneficialOwnerChanged
import io.cloudflight.jems.server.project.service.projectContractingPartnerBeneficialOwnerCreated
import io.cloudflight.jems.server.project.service.projectContractingPartnerBeneficialOwnersDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingPartnerBeneficialOwnersPersistenceProvider(
    private val beneficialOwnersRepository: ContractingPartnerBeneficialOwnersRepository,
    private val partnerRepository: ProjectPartnerRepository,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectPersistence: ProjectPersistenceProvider
) : ContractingPartnerBeneficialOwnersPersistence {

    @Transactional(readOnly = true)
    override fun getBeneficialOwners(partnerId: Long): List<ContractingPartnerBeneficialOwner> {
        return beneficialOwnersRepository.findTop10ByProjectPartnerId(partnerId).toModel()
    }

    @Transactional
    override fun updateBeneficialOwners(
        projectId: Long,
        partnerId: Long,
        beneficialOwners: List<ContractingPartnerBeneficialOwner>
    ): List<ContractingPartnerBeneficialOwner> {
        val projectSummary = projectPersistence.getProjectSummary(projectId)
        val existingById = beneficialOwnersRepository
            .findTop10ByProjectPartnerId(partnerId).associateBy { it.id }
        val projectPartner =
            if (existingById[0] != null) existingById[0]!!.projectPartner else partnerRepository.findById(partnerId)
                .orElseThrow { ResourceNotFoundException("project_partner") }
        val stayedBeneficialOwnerIds = beneficialOwners.filter { it.id > 0 }.mapTo(HashSet()) { it.id }

        deleteBeneficialOwners(existingById, stayedBeneficialOwnerIds, projectSummary, projectPartner)
        processBeneficialOwners(beneficialOwners, existingById, projectSummary, projectPartner)

        return beneficialOwnersRepository.findTop10ByProjectPartnerId(partnerId).toModel()
    }

    private fun processBeneficialOwners(
        beneficialOwners: List<ContractingPartnerBeneficialOwner>,
        existingById: Map<Long, ProjectContractingPartnerBeneficialOwnerEntity>,
        projectSummary: ProjectSummary,
        projectPartner: ProjectPartnerEntity
    ) {
        beneficialOwners.forEach { newData ->
            existingById[newData.id].let { existing ->
                when {
                    existing != null && (existing.toModel() != newData) -> {
                        updateBeneficialOwner(existing, newData, projectSummary, projectPartner)
                    }

                    existing == null -> {
                        addBeneficialOwner(newData, projectSummary, projectPartner)
                    }
                }
            }
        }
    }

    private fun addBeneficialOwner(
        newData: ContractingPartnerBeneficialOwner,
        projectSummary: ProjectSummary,
        projectPartner: ProjectPartnerEntity
    ) {
        beneficialOwnersRepository.save(newData.toEntity(projectPartner)).also {
            if (projectSummary.status.isAlreadyContracted()) {
                auditPublisher.publishEvent(
                    projectContractingPartnerBeneficialOwnerCreated(
                        this,
                        projectSummary,
                        it.projectPartner,
                        newData
                    )
                )
            }
        }
    }

    private fun updateBeneficialOwner(
        existing: ProjectContractingPartnerBeneficialOwnerEntity,
        newData: ContractingPartnerBeneficialOwner,
        projectSummary: ProjectSummary,
        projectPartner: ProjectPartnerEntity
    ) {
        val previousExisting = existing.toModel()
        existing.updateWith(newData).also {
            if (projectSummary.status.isAlreadyContracted()) {
                auditPublisher.publishEvent(
                    projectContractingPartnerBeneficialOwnerChanged(
                        this,
                        projectSummary,
                        projectPartner,
                        previousExisting,
                        newData
                    )
                )
            }
        }
    }

    private fun deleteBeneficialOwners(
        existingById: Map<Long, ProjectContractingPartnerBeneficialOwnerEntity>,
        stayedBeneficialOwnerIds: HashSet<Long>,
        projectSummary: ProjectSummary,
        projectPartner: ProjectPartnerEntity
    ) {
        val hasRemoved = existingById.minus(stayedBeneficialOwnerIds).values
        beneficialOwnersRepository.deleteAll(hasRemoved).also {
            if (hasRemoved.isNotEmpty() && projectSummary.status.isAlreadyContracted()) {
                auditPublisher.publishEvent(
                    projectContractingPartnerBeneficialOwnersDeleted(
                        this,
                        projectSummary,
                        projectPartner,
                        hasRemoved
                    )
                )
            }
        }
    }

    private fun ProjectContractingPartnerBeneficialOwnerEntity.updateWith(newData: ContractingPartnerBeneficialOwner) {
        firstName = newData.firstName
        lastName = newData.lastName
        birth = newData.birth
        vatNumber = newData.vatNumber
    }
}