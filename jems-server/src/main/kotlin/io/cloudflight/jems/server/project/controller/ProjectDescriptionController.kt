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
import io.cloudflight.jems.server.project.authorization.CanRetrieveProject
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.ProjectDescriptionService
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectDescriptionController(
    private val projectDescriptionService: ProjectDescriptionService
) : ProjectDescriptionApi {

    @CanRetrieveProject
    override fun getProjectDescription(projectId: Long): OutputProjectDescription {
        return projectDescriptionService.getProjectDescription(projectId)
    }

    @CanUpdateProject
    override fun updateProjectOverallObjective(projectId: Long, overallObjective: InputProjectOverallObjective): InputProjectOverallObjective? {
        return projectDescriptionService.updateOverallObjective(projectId, overallObjective)
    }

    @CanUpdateProject
    override fun updateProjectRelevance(projectId: Long, project: InputProjectRelevance): InputProjectRelevance {
        return projectDescriptionService.updateProjectRelevance(projectId, project)
    }

    @CanUpdateProject
    override fun updateProjectPartnership(projectId: Long, partnership: InputProjectPartnership): InputProjectPartnership? {
        return projectDescriptionService.updatePartnership(projectId, partnership)
    }

    @CanUpdateProject
    override fun updateProjectManagement(projectId: Long, project: InputProjectManagement): OutputProjectManagement {
        return projectDescriptionService.updateProjectManagement(projectId, project)
    }

    @CanUpdateProject
    override fun updateProjectLongTermPlans(projectId: Long, project: InputProjectLongTermPlans): OutputProjectLongTermPlans {
        return projectDescriptionService.updateProjectLongTermPlans(projectId, project)
    }

}
