package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.InputProjectManagement
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.ems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.ems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.OutputProjectManagement

interface ProjectDescriptionService {

    fun getProjectDescription(id: Long): OutputProjectDescription

    /**
     * C1
     */
    fun updateOverallObjective(id: Long, projectOverallObjective: String?): String?

    /**
     * C2
     */
    fun updateProjectRelevance(id: Long, projectRelevance: InputProjectRelevance): InputProjectRelevance

    /**
     * C3
     */
    fun updatePartnership(id: Long, projectPartnership: String?): String?

    /**
     * C7
     */
    fun updateProjectManagement(id: Long, projectManagement: InputProjectManagement): OutputProjectManagement

    /**
     * C8
     */
    fun updateProjectLongTermPlans(id: Long, projectLongTermPlans: InputProjectLongTermPlans): OutputProjectLongTermPlans

}
