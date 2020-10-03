package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.InputProjectManagement
import io.cloudflight.jems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.jems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.jems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.jems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.jems.server.project.entity.description.ProjectOverallObjective
import io.cloudflight.jems.server.project.entity.description.ProjectPartnership
import io.cloudflight.jems.server.project.repository.description.ProjectLongTermPlansRepository
import io.cloudflight.jems.server.project.repository.description.ProjectManagementRepository
import io.cloudflight.jems.server.project.repository.description.ProjectOverallObjectiveRepository
import io.cloudflight.jems.server.project.repository.description.ProjectPartnershipRepository
import io.cloudflight.jems.server.project.repository.description.ProjectRelevanceRepository
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
    override fun updateOverallObjective(id: Long, projectOverallObjective: InputProjectOverallObjective): InputProjectOverallObjective {
        return projectOverallObjectiveRepository.save(projectOverallObjective.toEntity(id)).toOutputProjectOverallObjective()
    }

    @Transactional
    override fun updateProjectRelevance(id: Long, projectRelevance: InputProjectRelevance): InputProjectRelevance {
        return projectRelevanceRepository.save(projectRelevance.toEntity(id)).toOutputProjectRelevance()
    }

    @Transactional
    override fun updatePartnership(id: Long, projectPartnership: InputProjectPartnership): InputProjectPartnership {
        return projectPartnershipRepository.save(projectPartnership.toEntity(id)).toOutputProjectPartnership()
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
