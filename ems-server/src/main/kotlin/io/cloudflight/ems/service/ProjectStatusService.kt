package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProjectQualityAssessment
import io.cloudflight.ems.api.dto.InputProjectStatus
import io.cloudflight.ems.api.dto.OutputProject

interface ProjectStatusService {

    fun setProjectStatus(projectId: Long, statusChange: InputProjectStatus): OutputProject

    fun setQualityAssessment(projectId: Long, qualityAssessmentData: InputProjectQualityAssessment): OutputProject

}
