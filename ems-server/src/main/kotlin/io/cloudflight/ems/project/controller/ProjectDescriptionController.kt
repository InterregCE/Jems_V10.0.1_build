package io.cloudflight.ems.project.controller

import io.cloudflight.ems.api.project.ProjectDescriptionApi
import io.cloudflight.ems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.InputProjectManagement
import io.cloudflight.ems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.ems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.ems.project.service.ProjectAdditionalDataService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectDescriptionController(
    private val projectAdditionalDataService: ProjectAdditionalDataService
) : ProjectDescriptionApi {

    @PreAuthorize("@projectAuthorization.canReadProject(#id)")
    override fun getProjectDescription(id: Long): OutputProjectDescription {
        return projectAdditionalDataService.getProjectDescription(id)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#id)")
    override fun updateProjectManagement(id: Long, project: InputProjectManagement): OutputProjectManagement {
        return projectAdditionalDataService.updateProjectManagement(id, project)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#id)")
    override fun updateProjectLongTermPlans(id: Long, project: InputProjectLongTermPlans): OutputProjectLongTermPlans {
        return projectAdditionalDataService.updateProjectLongTermPlans(id, project)
    }

}
