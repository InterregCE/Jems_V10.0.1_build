package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.repository.description.ProjectLongTermPlansRepository
import io.cloudflight.jems.server.project.repository.description.ProjectManagementRepository
import io.cloudflight.jems.server.project.repository.description.ProjectOverallObjectiveRepository
import io.cloudflight.jems.server.project.repository.description.ProjectPartnershipRepository
import io.cloudflight.jems.server.project.repository.description.ProjectRelevanceRepository
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectDescription
import io.cloudflight.jems.server.project.service.model.ProjectLongTermPlans
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectOverallObjective
import io.cloudflight.jems.server.project.service.model.ProjectPartnership
import io.cloudflight.jems.server.project.service.model.ProjectRelevance
import io.cloudflight.jems.server.project.service.toEntity
import io.cloudflight.jems.server.project.service.toProjectLongTermPlans
import io.cloudflight.jems.server.project.service.toProjectManagement
import io.cloudflight.jems.server.project.service.toProjectOverallObjective
import io.cloudflight.jems.server.project.service.toProjectPartnership
import io.cloudflight.jems.server.project.service.toProjectRelevance
import io.cloudflight.jems.server.project.service.toRelevanceBenefits
import io.cloudflight.jems.server.project.service.toRelevanceSpfRecipients
import io.cloudflight.jems.server.project.service.toRelevanceStrategies
import io.cloudflight.jems.server.project.service.toRelevanceSynergies
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Repository
class ProjectDescriptionPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectOverallObjectiveRepository: ProjectOverallObjectiveRepository,
    private val projectRelevanceRepository: ProjectRelevanceRepository,
    private val projectPartnershipRepository: ProjectPartnershipRepository,
    private val projectManagementRepository: ProjectManagementRepository,
    private val projectLongTermPlansRepository: ProjectLongTermPlansRepository
) : ProjectDescriptionPersistence {

    @Transactional(readOnly = true)
    override fun getProjectDescription(projectId: Long, version: String?): ProjectDescription {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                getProjectDescription(projectId)
            },
            // grouped this will be only one result
            previousVersionFetcher = { timestamp ->
                getProjectDescriptionHistoricalData(projectId, timestamp)
            }
        ) ?: throw ApplicationVersionNotFoundException()
    }

    private fun getProjectDescription(projectId: Long): ProjectDescription {
        return ProjectDescription(
            // C1
            projectOverallObjective = projectOverallObjectiveRepository.findFirstByProjectId(projectId)?.toProjectOverallObjective(),
            // C2
            projectRelevance = projectRelevanceRepository.findFirstByProjectId(projectId)?.toProjectRelevance(),
            // C3
            projectPartnership = projectPartnershipRepository.findFirstByProjectId(projectId)?.toProjectPartnership(),
            // C7
            projectManagement = projectManagementRepository.findFirstByProjectId(projectId)?.toProjectManagement(),
            // C8
            projectLongTermPlans = projectLongTermPlansRepository.findFirstByProjectId(projectId)?.toProjectLongTermPlans()
        )
    }

    private fun getProjectDescriptionHistoricalData(projectId: Long, timestamp: Timestamp): ProjectDescription {
        return ProjectDescription(
            // C1
            projectOverallObjective = projectOverallObjectiveRepository
                .findByProjectIdAsOfTimestamp(projectId, timestamp).toProjectOverallObjective(),
            // C2
            projectRelevance = projectRelevanceRepository
                .findByProjectIdAsOfTimestamp(projectId, timestamp).toProjectRelevance(
                    projectRelevanceRepository.findBenefitsByProjectIdAsOfTimestamp(projectId, timestamp).toRelevanceBenefits(),
                    projectRelevanceRepository.findSpfRecipientsByProjectIdAsOfTimestamp(projectId, timestamp).toRelevanceSpfRecipients(),
                    projectRelevanceRepository.findStrategiesByProjectIdAsOfTimestamp(projectId, timestamp).toRelevanceStrategies(),
                    projectRelevanceRepository.findSynergiesByProjectIdAsOfTimestamp(projectId, timestamp).toRelevanceSynergies()
                ),
            // C3
            projectPartnership = projectPartnershipRepository
                .findByProjectIdAsOfTimestamp(projectId, timestamp).toProjectPartnership(),
            // C7
            projectManagement = projectManagementRepository
                .findByProjectIdAsOfTimestamp(projectId, timestamp).toProjectManagement(),
            // C8
            projectLongTermPlans = projectLongTermPlansRepository
                .findByProjectIdAsOfTimestamp(projectId, timestamp).toProjectLongTermPlans()
        )
    }

    @Transactional
    override fun updateOverallObjective(id: Long, projectOverallObjective: ProjectOverallObjective): ProjectOverallObjective {
        return projectOverallObjectiveRepository.save(projectOverallObjective.toEntity(id)).toProjectOverallObjective()
    }

    @Transactional
    override fun updateProjectRelevance(id: Long, projectRelevance: ProjectRelevance): ProjectRelevance {
        return projectRelevanceRepository.save(projectRelevance.toEntity(id)).toProjectRelevance()
    }

    @Transactional
    override fun updatePartnership(id: Long, projectPartnership: ProjectPartnership): ProjectPartnership {
        return projectPartnershipRepository.save(projectPartnership.toEntity(id)).toProjectPartnership()
    }

    @Transactional
    override fun updateProjectManagement(id: Long, projectManagement: ProjectManagement): ProjectManagement {
        return projectManagementRepository.save(projectManagement.toEntity(id)).toProjectManagement()
    }

    @Transactional
    override fun updateProjectLongTermPlans(id: Long, projectLongTermPlans: ProjectLongTermPlans): ProjectLongTermPlans {
        return projectLongTermPlansRepository.save(projectLongTermPlans.toEntity(id)).toProjectLongTermPlans()
    }
}
