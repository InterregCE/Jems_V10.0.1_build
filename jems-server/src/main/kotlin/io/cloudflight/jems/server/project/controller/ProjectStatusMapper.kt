package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ProjectDecisionDTO
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.cloudflight.jems.server.project.service.model.ProjectDecision
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.user.controller.toDto

fun ProjectStatus.toDto() = ProjectStatusDTO(
    id = id,
    status = ApplicationStatusDTO.valueOf(status.name),
    user = user.toDto(),
    updated = updated,
    decisionDate = decisionDate,
    note = note
)

fun ProjectDecision.toDto() = ProjectDecisionDTO(
    qualityAssessment = qualityAssessment,
    eligibilityAssessment = eligibilityAssessment,
    eligibilityDecision = eligibilityDecision?.toDto(),
    fundingDecision = fundingDecision?.toDto()
)
