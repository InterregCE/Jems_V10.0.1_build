package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.ems.api.project.dto.status.OutputProjectQualityAssessment
import io.cloudflight.ems.api.project.dto.status.OutputProjectStatus
import io.cloudflight.ems.project.entity.ProjectEligibilityAssessment
import io.cloudflight.ems.project.entity.ProjectQualityAssessment
import io.cloudflight.ems.project.entity.ProjectStatus
import io.cloudflight.ems.user.service.toOutputUser

fun ProjectStatus.toOutputProjectStatus() = OutputProjectStatus(
        id = id,
        status = status,
        user = user.toOutputUser(),
        updated = updated,
        decisionDate = decisionDate,
        note = note
)

fun ProjectQualityAssessment.toOutputProjectQualityAssessment() = OutputProjectQualityAssessment(
    result = result,
    user = user.toOutputUser(),
    updated = updated,
    note = note
)

fun ProjectEligibilityAssessment.toOutputProjectEligibilityAssessment() = OutputProjectEligibilityAssessment(
    result = result,
    user = user.toOutputUser(),
    updated = updated,
    note = note
)
