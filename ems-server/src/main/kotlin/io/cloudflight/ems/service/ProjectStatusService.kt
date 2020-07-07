package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProjectStatus
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.ProjectApplicationStatus

interface ProjectStatusService {

    fun setProjectStatus(projectId: Long, statusChange: InputProjectStatus): OutputProject

}
