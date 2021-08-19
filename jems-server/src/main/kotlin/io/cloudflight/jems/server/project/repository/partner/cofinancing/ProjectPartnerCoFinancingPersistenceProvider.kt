package io.cloudflight.jems.server.project.repository.partner.cofinancing

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.copy
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Repository
class ProjectPartnerCoFinancingPersistenceProvider(
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val projectVersionUtils: ProjectVersionUtils
) : ProjectPartnerCoFinancingPersistence {

    @Transactional(readOnly = true)
    override fun getAvailableFundIds(partnerId: Long): Set<Long> =
        getPartnerOrThrow(partnerId).project.call.funds.mapTo(HashSet()) { it.id }

    @Transactional(readOnly = true)
    override fun getCoFinancingAndContributions(
        partnerId: Long,
        version: String?
    ): ProjectPartnerCoFinancingAndContribution {
        return projectVersionUtils.fetch(version,
            projectId = projectVersionUtils.fetchProjectId(version, partnerId,
                currentVersionOnlyFetcher = { projectPartnerRepository.getProjectIdForPartner(partnerId) },
                historicVersionFetcher = { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(partnerId) }
            ),
            currentVersionFetcher = {
                getPartnerOrThrow(partnerId).extractCoFinancingAndContribution()
            },
            previousVersionFetcher = { timestamp ->
                getPartnerCoFinancingAndContributions(partnerId, timestamp)
            }
        ) ?: ProjectPartnerCoFinancingAndContribution(
            finances = emptyList(),
            partnerContributions = emptyList(),
            partnerAbbreviation = ""
        )
    }

    @Transactional
    override fun updateCoFinancingAndContribution(
        partnerId: Long,
        finances: Collection<UpdateProjectPartnerCoFinancing>,
        partnerContributions: List<ProjectPartnerContribution>
    ): ProjectPartnerCoFinancingAndContribution =
        getPartnerOrThrow(partnerId).let { entity ->
            val savedEntity = projectPartnerRepository.save(
                entity.copy(
                    newFinancing = finances,
                    newPartnerContributions = partnerContributions,
                )
            )

            ProjectPartnerCoFinancingAndContribution(
                finances = savedEntity.financing.toCoFinancingModel(),
                partnerContributions = savedEntity.partnerContributions.toContributionModel(),
                partnerAbbreviation = savedEntity.abbreviation
            )
        }

    private fun getPartnerOrThrow(partnerId: Long): ProjectPartnerEntity =
        projectPartnerRepository.findById(partnerId).orElseThrow { ResourceNotFoundException("projectPartner") }

    private fun getPartnerCoFinancingAndContributions(
        partnerId: Long,
        timestamp: Timestamp,
    ): ProjectPartnerCoFinancingAndContribution {
        val finances = projectPartnerRepository.findPartnerFinancingByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerFinancingHistoricalData()
        val partnerContributions = projectPartnerRepository.findPartnerContributionByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerContributionHistoricalData()
        val partnerAbbrev =
            projectPartnerRepository.findPartnerIdentityByIdAsOfTimestamp(partnerId, timestamp).firstOrNull()?.abbreviation
        return ProjectPartnerCoFinancingAndContribution(
            finances = finances,
            partnerContributions = partnerContributions,
            partnerAbbreviation = partnerAbbrev ?: ""
        )
    }

}
