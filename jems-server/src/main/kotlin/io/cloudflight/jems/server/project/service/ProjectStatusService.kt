package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.InputProjectQualityAssessment

interface ProjectStatusService {

    fun setQualityAssessment(projectId: Long, qualityAssessmentData: InputProjectQualityAssessment): ProjectDetailDTO

    fun setEligibilityAssessment(
        projectId: Long,
        eligibilityAssessmentData: InputProjectEligibilityAssessment
    ): ProjectDetailDTO
}
