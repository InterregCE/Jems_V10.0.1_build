package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality

interface ProjectAssessmentPersistence {

    fun eligibilityForStepExists(projectId: Long, step: Int): Boolean

    fun setEligibility(userId: Long, data: ProjectAssessmentEligibility)

    fun qualityForStepExists(projectId: Long, step: Int): Boolean

    fun setQuality(userId: Long, data: ProjectAssessmentQuality)

}
