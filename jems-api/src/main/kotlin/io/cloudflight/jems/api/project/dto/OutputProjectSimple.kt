package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import java.time.ZonedDateTime

data class OutputProjectSimple(
    val id: Long?,
    val callName: String,
    val acronym: String,
    val projectStatus: ProjectApplicationStatus,
    val firstSubmissionDate: ZonedDateTime?,
    val lastResubmissionDate: ZonedDateTime?,
    val specificObjectiveCode: String?,
    val programmePriorityCode: String?
)
