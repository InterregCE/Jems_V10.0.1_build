package io.cloudflight.ems.api.project.dto.description

data class InputProjectRelevanceBenefit(
    val group: ProjectTargetGroup,
    val specification: String? = null
)
