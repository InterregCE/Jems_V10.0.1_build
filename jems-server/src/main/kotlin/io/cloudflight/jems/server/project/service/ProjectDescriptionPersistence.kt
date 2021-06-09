package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.server.project.service.model.ProjectDescription
import io.cloudflight.jems.server.project.service.model.ProjectLongTermPlans
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectOverallObjective
import io.cloudflight.jems.server.project.service.model.ProjectPartnership
import io.cloudflight.jems.server.project.service.model.ProjectRelevance

interface ProjectDescriptionPersistence {

    fun getProjectDescription(projectId: Long, version: String? = null): ProjectDescription

    fun updateOverallObjective(id: Long, projectOverallObjective: ProjectOverallObjective): ProjectOverallObjective

    fun updateProjectRelevance(id: Long, projectRelevance: ProjectRelevance): ProjectRelevance

    fun updatePartnership(id: Long, projectPartnership: ProjectPartnership): ProjectPartnership

    fun updateProjectManagement(id: Long, projectManagement: ProjectManagement): ProjectManagement

    fun updateProjectLongTermPlans(id: Long, projectLongTermPlans: ProjectLongTermPlans): ProjectLongTermPlans
}
