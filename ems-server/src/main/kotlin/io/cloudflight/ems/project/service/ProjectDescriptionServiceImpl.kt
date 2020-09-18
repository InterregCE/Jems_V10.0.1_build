package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.InputProjectManagement
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.ems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.ems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.ems.project.entity.description.ProjectOverallObjective
import io.cloudflight.ems.project.entity.description.ProjectPartnership
import io.cloudflight.ems.project.repository.description.ProjectLongTermPlansRepository
import io.cloudflight.ems.project.repository.description.ProjectManagementRepository
import io.cloudflight.ems.project.repository.description.ProjectOverallObjectiveRepository
import io.cloudflight.ems.project.repository.description.ProjectPartnershipRepository
import io.cloudflight.ems.project.repository.description.ProjectRelevanceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectDescriptionServiceImpl(
    private val projectOverallObjectiveRepository: ProjectOverallObjectiveRepository,
    private val projectRelevanceRepository: ProjectRelevanceRepository,
    private val projectPartnershipRepository: ProjectPartnershipRepository,
    private val projectManagementRepository: ProjectManagementRepository,
    private val projectLongTermPlansRepository: ProjectLongTermPlansRepository
) : ProjectDescriptionService {

    @Transactional(readOnly = true)
    override fun getProjectDescription(id: Long): OutputProjectDescription {
        return OutputProjectDescription(
            // C1
            projectOverallObjective = projectOverallObjectiveRepository.findFirstByProjectId(id)?.projectOverallObjective,
            // C2
            projectRelevance = projectRelevanceRepository.findFirstByProjectId(id)?.toOutputProjectRelevance(),
            // C3
            projectPartnership = projectPartnershipRepository.findFirstByProjectId(id)?.projectPartnership,
            // C7
            projectManagement = projectManagementRepository.findFirstByProjectId(id)?.toOutputProjectManagement(),
            // C8
            projectLongTermPlans = projectLongTermPlansRepository.findFirstByProjectId(id)?.toOutputProjectLongTermPlans()
        )
    }

    @Transactional
    override fun updateOverallObjective(id: Long, projectOverallObjective: String?): String? {
        return projectOverallObjectiveRepository.save(
            ProjectOverallObjective(projectId = id, projectOverallObjective = projectOverallObjective)
        ).projectOverallObjective
    }

    @Transactional
    override fun updateProjectRelevance(id: Long, projectRelevance: InputProjectRelevance): InputProjectRelevance {
        return projectRelevanceRepository.save(projectRelevance.toEntity(id)).toOutputProjectRelevance()
    }

    @Transactional
    override fun updatePartnership(id: Long, projectPartnership: String?): String? {
        return projectPartnershipRepository.save(
            ProjectPartnership(projectId = id, projectPartnership = projectPartnership)
        ).projectPartnership
    }

    @Transactional
    override fun updateProjectManagement(id: Long, projectManagement: InputProjectManagement): OutputProjectManagement {
        return projectManagementRepository.save(projectManagement.toEntity(id)).toOutputProjectManagement()
    }

    @Transactional
    override fun updateProjectLongTermPlans(id: Long, projectLongTermPlans: InputProjectLongTermPlans): OutputProjectLongTermPlans {
        return projectLongTermPlansRepository.save(projectLongTermPlans.toEntity(id)).toOutputProjectLongTermPlans()
    }

}
