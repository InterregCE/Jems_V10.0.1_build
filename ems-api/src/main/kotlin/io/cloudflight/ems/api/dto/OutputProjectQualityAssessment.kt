package io.cloudflight.ems.api.dto

import io.cloudflight.ems.api.dto.user.OutputUser

data class OutputProjectQualityAssessment (
    val result: ProjectQualityAssessmentResult,
    val user: OutputUser,
    val note: String? = null
)
