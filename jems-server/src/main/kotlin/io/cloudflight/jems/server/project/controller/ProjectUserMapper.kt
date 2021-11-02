package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.ProjectUserDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.status.ProjectDecisionDTO
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.user.controller.toDto
import io.cloudflight.jems.server.user.service.model.ProjectWithUsers
import org.springframework.data.domain.Page

fun ProjectWithUsers.toDto() = ProjectUserDTO(
    id = id,
    customIdentifier = customIdentifier,
    acronym = acronym,
    projectStatus = projectStatus.toDTO(),
    assignedUserIds = assignedUserIds,
)

fun Page<ProjectWithUsers>.toDto() = map { it.toDto() }
