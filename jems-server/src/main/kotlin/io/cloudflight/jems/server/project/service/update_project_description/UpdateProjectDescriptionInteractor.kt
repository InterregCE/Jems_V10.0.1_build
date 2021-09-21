package io.cloudflight.jems.server.project.service.update_project_description

import io.cloudflight.jems.server.project.service.model.ProjectLongTermPlans
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectOverallObjective
import io.cloudflight.jems.server.project.service.model.ProjectPartnership
import io.cloudflight.jems.server.project.service.model.ProjectRelevance

interface UpdateProjectDescriptionInteractor {

    /**
     * C1
     */
    fun updateOverallObjective(projectId: Long, projectOverallObjective: ProjectOverallObjective): ProjectOverallObjective

    /**
     * C2
     */
    fun updateProjectRelevance(projectId: Long, projectRelevance: ProjectRelevance): ProjectRelevance

    /**
     * C3
     */
    fun updatePartnership(projectId: Long, projectPartnership: ProjectPartnership): ProjectPartnership

    /**
     * C7
     */
    fun updateProjectManagement(projectId: Long, projectManagement: ProjectManagement): ProjectManagement

    /**
     * C8
     */
    fun updateProjectLongTermPlans(projectId: Long, projectLongTermPlans: ProjectLongTermPlans): ProjectLongTermPlans

}
