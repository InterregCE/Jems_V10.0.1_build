package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectDescriptionApi
import io.cloudflight.jems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.InputProjectManagement
import io.cloudflight.jems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.jems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.jems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.jems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.jems.server.project.service.get_project_description.GetProjectDescriptionInteractor
import io.cloudflight.jems.server.project.service.update_project_description.UpdateProjectDescriptionInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectDescriptionController(
    private val getProjectDescriptionInteractor: GetProjectDescriptionInteractor,
    private val updateProjectDescriptionInteractor: UpdateProjectDescriptionInteractor
) : ProjectDescriptionApi {

    override fun getProjectDescription(projectId: Long, version: String?): OutputProjectDescription {
        return getProjectDescriptionInteractor.getProjectDescription(projectId, version).toDto()
    }

    override fun updateProjectOverallObjective(projectId: Long, overallObjective: InputProjectOverallObjective): InputProjectOverallObjective? {
        return updateProjectDescriptionInteractor.updateOverallObjective(projectId, overallObjective.toModel()).toDto()
    }

    override fun updateProjectRelevance(projectId: Long, projectRelevance: InputProjectRelevance): InputProjectRelevance {
        return updateProjectDescriptionInteractor.updateProjectRelevance(projectId, projectRelevance.toModel()).toDto()
    }

    override fun updateProjectPartnership(projectId: Long, projectPartnership: InputProjectPartnership): InputProjectPartnership? {
        return updateProjectDescriptionInteractor.updatePartnership(projectId, projectPartnership.toModel()).toDto()
    }

    override fun updateProjectManagement(projectId: Long, projectManagement: InputProjectManagement): OutputProjectManagement {
        return updateProjectDescriptionInteractor.updateProjectManagement(projectId, projectManagement.toModel()).toDto()
    }

    override fun updateProjectLongTermPlans(projectId: Long, projectLongTermPlans: InputProjectLongTermPlans): OutputProjectLongTermPlans {
        return updateProjectDescriptionInteractor.updateProjectLongTermPlans(projectId, projectLongTermPlans.toModel()).toDto()
    }

}
