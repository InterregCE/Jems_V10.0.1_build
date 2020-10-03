package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectStatus
import io.cloudflight.jems.server.project.entity.ProjectEligibilityAssessment
import io.cloudflight.jems.server.project.entity.ProjectQualityAssessment
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.user.service.toOutputUser

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
    updated = updated,
    note = note
)

fun ProjectEligibilityAssessment.toOutputProjectEligibilityAssessment() = OutputProjectEligibilityAssessment(
    result = result,
    updated = updated,
    note = note
)
