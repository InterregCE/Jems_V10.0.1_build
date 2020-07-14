package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectQualityAssessment
import io.cloudflight.ems.entity.ProjectQualityAssessment

fun ProjectQualityAssessment.toOutputProjectQualityAssessment() = OutputProjectQualityAssessment(
    result = result,
    user = user.toOutputUser(),
    note = note
)
