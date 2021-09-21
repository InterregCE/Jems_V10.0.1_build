package io.cloudflight.jems.server.project.repository.assessment

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentEligibilityEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentId
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentQualityEntity
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.user.entity.UserEntity

fun ProjectAssessmentQuality.toEntity(project: ProjectEntity, user: UserEntity) = ProjectAssessmentQualityEntity(
    id = ProjectAssessmentId(project, step),
    result = result,
    user = user,
    note = note,
)

fun ProjectAssessmentEligibility.toEntity(project: ProjectEntity, user: UserEntity) = ProjectAssessmentEligibilityEntity(
    id = ProjectAssessmentId(project, step),
    result = result,
    user = user,
    note = note,
)
