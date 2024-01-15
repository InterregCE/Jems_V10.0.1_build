package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.status.ProjectDecisionDTO
import io.cloudflight.jems.api.project.dto.status.ProjectModificationDecisionDTO
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.cloudflight.jems.server.project.controller.auditAndControl.correction.toSimpleDto
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
import io.cloudflight.jems.server.project.service.model.ProjectModificationDecision
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.user.controller.toDto

fun ProjectStatus.toDto() = ProjectStatusDTO(
    id = id,
    status = ApplicationStatusDTO.valueOf(status.name),
    user = user.toDto(),
    updated = updated,
    decisionDate = decisionDate,
    entryIntoForceDate = entryIntoForceDate,
    note = note
)

fun ProjectModificationDecision.toDto() = ProjectModificationDecisionDTO(
    projectStatus = projectStatus.toDto(),
    corrections = corrections.map { it.toSimpleDto() }
)

fun ProjectAssessmentQuality.toDto() = OutputProjectQualityAssessment(
    result = result,
    updated = updated!!,
    note = note
)

fun ProjectAssessmentEligibility.toDto() = OutputProjectEligibilityAssessment(
    result = result,
    updated = updated!!,
    note = note
)

fun ProjectAssessment.toDto() = ProjectDecisionDTO(
    qualityAssessment = assessmentQuality?.toDto(),
    eligibilityAssessment = assessmentEligibility?.toDto(),
    eligibilityDecision = eligibilityDecision?.toDto(),
    preFundingDecision = preFundingDecision?.toDto(),
    finalFundingDecision = fundingDecision?.toDto(),
    modificationDecision = modificationDecision?.toDto(),
)
