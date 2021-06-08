package io.cloudflight.jems.server.project.service.application.set_assessment_eligibility

import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.server.project.service.model.Project

interface SetAssessmentEligibilityInteractor {
    fun setEligibilityAssessment(projectId: Long, result: ProjectAssessmentEligibilityResult, note: String? = null): Project
}
