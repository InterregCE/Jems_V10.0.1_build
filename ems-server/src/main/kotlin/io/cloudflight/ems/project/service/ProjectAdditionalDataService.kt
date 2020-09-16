package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.InputProjectManagement
import io.cloudflight.ems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.ems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.OutputProjectManagement

interface ProjectAdditionalDataService {

    fun getProjectDescription(id: Long): OutputProjectDescription

    fun updateProjectManagement(id: Long, projectManagement: InputProjectManagement): OutputProjectManagement

    fun updateProjectLongTermPlans(id: Long, projectLongTermPlans: InputProjectLongTermPlans): OutputProjectLongTermPlans

}
