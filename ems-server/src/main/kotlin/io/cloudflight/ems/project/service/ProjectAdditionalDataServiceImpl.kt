package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.InputProjectManagement
import io.cloudflight.ems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.ems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.ems.project.repository.ProjectLongTermPlansRepository
import io.cloudflight.ems.project.repository.ProjectManagementRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectAdditionalDataServiceImpl(
    private val projectManagementRepo: ProjectManagementRepository,
    private val projectLongTermPlansRepository: ProjectLongTermPlansRepository
) : ProjectAdditionalDataService {

    @Transactional(readOnly = true)
    override fun getProjectDescription(id: Long): OutputProjectDescription {
        return OutputProjectDescription(
            projectManagement = projectManagementRepo.findFirstByProjectId(id)?.toOutputProjectManagement(),
            projectLongTermPlans = projectLongTermPlansRepository.findFirstByProjectId(id)?.toOutputProjectLongTermPlans()
        )
    }

    @Transactional
    override fun updateProjectManagement(id: Long, projectManagement: InputProjectManagement): OutputProjectManagement {
        return projectManagementRepo.save(projectManagement.toEntity(id)).toOutputProjectManagement()
    }

    @Transactional
    override fun updateProjectLongTermPlans(id: Long, projectLongTermPlans: InputProjectLongTermPlans): OutputProjectLongTermPlans {
        return projectLongTermPlansRepository.save(projectLongTermPlans.toEntity(id)).toOutputProjectLongTermPlans()
    }

}
