package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectEligibilityAssessment
import io.cloudflight.ems.api.dto.OutputProjectQualityAssessment
import io.cloudflight.ems.entity.ProjectEligibilityAssessment
import io.cloudflight.ems.entity.ProjectQualityAssessment

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
