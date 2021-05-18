package io.cloudflight.jems.server.project.repository.partner.cofinancing

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import java.sql.Timestamp
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerCoFinancingPersistenceProvider(
    private val projectPartnerRepo: ProjectPartnerRepository,
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectPersistence: ProjectPersistence,
    ) : ProjectPartnerCoFinancingPersistence {

    @Transactional(readOnly = true)
    override fun getAvailableFundIds(partnerId: Long): Set<Long> =
        getPartnerOrThrow(partnerId).project.call.funds.mapTo(HashSet()) { it.id }

    @Transactional(readOnly = true)
    override fun getCoFinancingAndContributions(partnerId: Long, version: String?): ProjectPartnerCoFinancingAndContribution {
        val partner = getPartnerOrThrow(partnerId)
        return projectVersionUtils.fetch(version, projectPersistence.getProjectIdForPartner(partnerId),
            currentVersionFetcher = {
                ProjectPartnerCoFinancingAndContribution(
                    finances = partner.financing.toCoFinancingModel(),
                    partnerContributions = partner.partnerContributions.toContributionModel(),
                    partnerAbbreviation = partner.abbreviation
                )
            },
            previousVersionFetcher = { timestamp ->
                getPartnerCoFinancingAndContributions(partnerId, timestamp)
            }
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

    private fun getPartnerCoFinancingAndContributions(
        partnerId: Long,
        timestamp: Timestamp,
    ): ProjectPartnerCoFinancingAndContribution {
        val finances = projectPartnerRepo.findPartnerFinancingByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerFinancingHistoricalData()
        val partnerContributions = projectPartnerRepo.findPartnerContributionByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerContributionHistoricalData()
        val partner = projectPartnerRepo.findPartnerIdentityByIdAsOfTimestamp(partnerId, timestamp).first()
        return ProjectPartnerCoFinancingAndContribution(
            finances = finances,
            partnerContributions = partnerContributions,
            partnerAbbreviation = partner.abbreviation
        )
    }

}
