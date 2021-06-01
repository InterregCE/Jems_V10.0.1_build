package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.status.ProjectDecisionDTO
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.cloudflight.jems.server.project.entity.ProjectDecisionEntity
import io.cloudflight.jems.server.project.entity.ProjectEligibilityAssessment
import io.cloudflight.jems.server.project.entity.ProjectQualityAssessment
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.service.model.ProjectDecision
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.user.service.toOutputUser

fun ProjectStatusHistoryEntity.toOutputProjectStatus() = ProjectStatusDTO(
        id = id,
        status = ApplicationStatusDTO.valueOf(status.name),
        user = user.toOutputUser(),
        updated = updated,
        decisionDate = decisionDate,
        note = note
)

fun ProjectStatusHistoryEntity.toProjectStatus() = ProjectStatus(
    id = id,
    status = status,
    user = user.toUserSummary(),
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

fun ProjectDecisionEntity.toProjectDecisionDTO() = ProjectDecisionDTO(
    qualityAssessment = qualityAssessment?.toOutputProjectQualityAssessment(),
    eligibilityAssessment = eligibilityAssessment?.toOutputProjectEligibilityAssessment(),
    eligibilityDecision = eligibilityDecision?.toOutputProjectStatus(),
    fundingDecision = fundingDecision?.toOutputProjectStatus()
)

fun ProjectDecisionEntity.toProjectDecision() = ProjectDecision(
    qualityAssessment = qualityAssessment?.toOutputProjectQualityAssessment(),
    eligibilityAssessment = eligibilityAssessment?.toOutputProjectEligibilityAssessment(),
    eligibilityDecision = eligibilityDecision?.toProjectStatus(),
    fundingDecision = fundingDecision?.toProjectStatus()
)
