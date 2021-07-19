package io.cloudflight.jems.server.project.service.application.set_assessment_quality

import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.server.project.service.model.ProjectDetail

interface SetAssessmentQualityInteractor {
    fun setQualityAssessment(projectId: Long, result: ProjectAssessmentQualityResult, note: String? = null): ProjectDetail
}
