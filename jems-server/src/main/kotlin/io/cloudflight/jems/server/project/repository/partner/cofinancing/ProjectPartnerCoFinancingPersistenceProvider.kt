package io.cloudflight.jems.server.project.repository.partner.cofinancing

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.budget.cofinancing.ProjectPartnerCoFinancingRepository
import io.cloudflight.jems.server.project.repository.budget.cofinancing.ProjectPartnerContributionSpfRepository
import io.cloudflight.jems.server.project.repository.budget.cofinancing.ProjectPartnerSpfCoFinancingRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.partner.copy
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Repository
class ProjectPartnerCoFinancingPersistenceProvider(
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val projectPartnerCoFinancingRepository: ProjectPartnerCoFinancingRepository,
    private val projectPartnerSpfCoFinancingRepository: ProjectPartnerSpfCoFinancingRepository,
    private val projectPartnerContributionSpfRepository: ProjectPartnerContributionSpfRepository,
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectRepository: ProjectRepository
) : ProjectPartnerCoFinancingPersistence {

    @Transactional(readOnly = true)
    override fun getAvailableFunds(partnerId: Long): Set<ProgrammeFund> =
       projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(partnerId)?.let { projectId ->
            projectRepository.getById(projectId)
                .call.funds.map { it.setupId.programmeFund.toModel() }
                .toSet()
        } ?: throw ProjectNotFoundException()

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
                getPartnerOrThrow(partnerId).extractCoFinancingAndContribution(
                    finances = projectPartnerCoFinancingRepository.findAllByCoFinancingFundIdPartnerId(partnerId)
                )
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

    @Transactional(readOnly = true)
    override fun getCoFinancingAndContributionsForPartnerList(
        partnerIds: List<Long>,
        projectId: Long,
        version: String?
    ): Map<Long, List<ProjectPartnerCoFinancing>>? {
        return projectVersionUtils.fetch(version,
            projectId,
            currentVersionFetcher = {
                projectPartnerCoFinancingRepository.findPartnersFinancingById(partnerIds).toPerPartnerFinancing()
            },
            previousVersionFetcher = { timestamp ->
                projectPartnerCoFinancingRepository.findPartnersFinancingByIdAsOfTimestamp(partnerIds, timestamp).toPerPartnerFinancing()
            }
        ) ?: partnerIds.associateBy({it}, { emptyList()})
    }

    @Transactional
    override fun updateCoFinancingAndContribution(
        partnerId: Long,
        finances: List<UpdateProjectPartnerCoFinancing>,
        partnerContributions: List<ProjectPartnerContribution>
    ): ProjectPartnerCoFinancingAndContribution {
        val partner = getPartnerOrThrow(partnerId)
        val availableFundsGroupedById = partner.project.call.funds
            .map { it.setupId.programmeFund }
            .associateBy { it.id }

        val updatedPartner = projectPartnerRepository.save(
            partner.copy(
                newPartnerContributions = partnerContributions,
            )
        )

        projectPartnerCoFinancingRepository.deleteByCoFinancingFundIdPartnerId(partnerId)
        val financesSaved = projectPartnerCoFinancingRepository.saveAll(
            finances.toCoFinancingEntity(
                partnerId,
                availableFundsGroupedById
            )
        )

        return ProjectPartnerCoFinancingAndContribution(
            finances = financesSaved.toCoFinancingModel(),
            partnerContributions = updatedPartner.partnerContributions.toContributionModel(updatedPartner.abbreviation),
            partnerAbbreviation = updatedPartner.abbreviation
        )
    }

    @Transactional(readOnly = true)
    override fun getSpfCoFinancingAndContributions(
        partnerId: Long,
        version: String?
    ): ProjectPartnerCoFinancingAndContributionSpf {
        return projectVersionUtils.fetch(version,
            projectId = projectVersionUtils.fetchProjectId(version, partnerId,
                currentVersionOnlyFetcher = { projectPartnerRepository.getProjectIdForPartner(partnerId) },
                historicVersionFetcher = { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(partnerId) }
            ),
            currentVersionFetcher = {
                ProjectPartnerCoFinancingAndContributionSpf(
                    finances = projectPartnerSpfCoFinancingRepository.findAllByCoFinancingFundIdPartnerId(partnerId)
                        .toSpfCoFinancingModel(),
                    partnerContributions = projectPartnerContributionSpfRepository.findAllByPartnerId(partnerId)
                        .toContributionSpfModel()
                )
            },
            previousVersionFetcher = { timestamp ->
                getPartnerSpfCoFinancingAndContributions(partnerId, timestamp)
            }
        ) ?: ProjectPartnerCoFinancingAndContributionSpf(
            finances = emptyList(),
            partnerContributions = emptyList()
        )
    }

    @Transactional
    override fun updateSpfCoFinancingAndContribution(
        partnerId: Long,
        finances: List<UpdateProjectPartnerCoFinancing>,
        partnerContributions: List<ProjectPartnerContributionSpf>
    ): ProjectPartnerCoFinancingAndContributionSpf {
        val partner = getPartnerOrThrow(partnerId)
        val availableFundsGroupedById = partner.project.call.funds
            .map { it.setupId.programmeFund }
            .associateBy { it.id }

        projectPartnerContributionSpfRepository.deleteByPartnerId(partnerId)
        val contributions = projectPartnerContributionSpfRepository.saveAll(partnerContributions.toEntity(partnerId))

        projectPartnerSpfCoFinancingRepository.deleteByCoFinancingFundIdPartnerId(partnerId)
        val financesSaved = projectPartnerSpfCoFinancingRepository.saveAll(
            finances.toSpfCoFinancingEntity(
                partnerId,
                availableFundsGroupedById
            )
        )

        return ProjectPartnerCoFinancingAndContributionSpf(
            finances = financesSaved.toSpfCoFinancingModel(),
            partnerContributions = contributions.toContributionSpfModel()
        )
    }

    private fun getPartnerOrThrow(partnerId: Long): ProjectPartnerEntity =
        projectPartnerRepository.findById(partnerId).orElseThrow { ResourceNotFoundException("projectPartner") }

    private fun getPartnerCoFinancingAndContributions(
        partnerId: Long,
        timestamp: Timestamp,
    ): ProjectPartnerCoFinancingAndContribution {
        val finances = projectPartnerCoFinancingRepository.findPartnerFinancingByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerFinancingHistoricalData()
        val partnerAbbrev =
            projectPartnerRepository.findPartnerIdentityByIdAsOfTimestamp(partnerId, timestamp)
                .firstOrNull()?.abbreviation ?: ""
        val partnerContributions =
            projectPartnerRepository.findPartnerContributionByIdAsOfTimestamp(partnerId, timestamp)
                .toProjectPartnerContributionHistoricalData(partnerAbbrev)
        return ProjectPartnerCoFinancingAndContribution(
            finances = finances,
            partnerContributions = partnerContributions,
            partnerAbbreviation = partnerAbbrev
        )
    }

    private fun getPartnerSpfCoFinancingAndContributions(
        partnerId: Long,
        timestamp: Timestamp,
    ): ProjectPartnerCoFinancingAndContributionSpf {
        val finances = projectPartnerSpfCoFinancingRepository.findPartnerFinancingByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerFinancingHistoricalData()
        val partnerContributions =
            projectPartnerContributionSpfRepository.findPartnerContributionSpfByIdAsOfTimestamp(partnerId, timestamp)
                .toProjectPartnerContributionSpfHistoricalData()
        return ProjectPartnerCoFinancingAndContributionSpf(
            finances = finances,
            partnerContributions = partnerContributions
        )
    }

}
