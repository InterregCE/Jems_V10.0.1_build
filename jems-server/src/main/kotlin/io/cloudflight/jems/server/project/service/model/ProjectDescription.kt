package io.cloudflight.jems.server.project.service.model

data class ProjectDescription(
    // section C1
    val projectOverallObjective: ProjectOverallObjective?,
    // section C2
    val projectRelevance: ProjectRelevance?,
    // section C3
    val projectPartnership: ProjectPartnership?,
    // section C7
    val projectManagement: ProjectManagement?,
    // section C8
    val projectLongTermPlans: ProjectLongTermPlans?
)
