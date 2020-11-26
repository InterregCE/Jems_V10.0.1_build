package io.cloudflight.jems.server.project.repository.partner.cofinancing

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerCoFinancingPersistenceProvider(
    private val projectPartnerRepo: ProjectPartnerRepository
) : ProjectPartnerCoFinancingPersistence {

    @Transactional(readOnly = true)
    override fun getAvailableFundIds(partnerId: Long): Set<Long> =
        getPartnerOrThrow(partnerId).project.call.funds.mapTo(HashSet()) { it.id }

    @Transactional(readOnly = true)
    override fun getCoFinancingAndContributions(partnerId: Long): ProjectPartnerCoFinancingAndContribution {
        val partner = getPartnerOrThrow(partnerId)
        return ProjectPartnerCoFinancingAndContribution(
            finances = partner.financing.toCoFinancingModel(),
            partnerContributions = partner.partnerContributions.toContributionModel(),
            partnerAbbreviation = partner.abbreviation
        )
    }

    @Transactional
    override fun updateCoFinancingAndContribution(
        partnerId: Long,
        finances: Collection<UpdateProjectPartnerCoFinancing>,
        partnerContributions: List<ProjectPartnerContribution>
    ): ProjectPartnerCoFinancingAndContribution {
        val partner = getPartnerOrThrow(partnerId)
        val availableFundsGroupedById = partner.project.call.funds.associateBy { it.id }

        val updatedPartner = projectPartnerRepo.save(
            partner.copy(
                financing = finances.toCoFinancingEntity(partnerId, availableFundsGroupedById),
                partnerContributions = partnerContributions.toContributionEntity(partnerId)
            )
        )

        return ProjectPartnerCoFinancingAndContribution(
            finances = updatedPartner.financing.toCoFinancingModel(),
            partnerContributions = updatedPartner.partnerContributions.toContributionModel(),
            partnerAbbreviation = updatedPartner.abbreviation
        )
    }

    private fun getPartnerOrThrow(partnerId: Long): ProjectPartnerEntity =
        projectPartnerRepo.findById(partnerId).orElseThrow { ResourceNotFoundException("projectPartner") }

}
