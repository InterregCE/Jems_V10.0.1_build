package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import java.time.ZonedDateTime

data class OutputProjectSimple(
    val id: Long?,
    val customIdentifier: String,
    val callName: String,
    val acronym: String,
    val projectStatus: ApplicationStatusDTO,
    val firstSubmissionDate: ZonedDateTime?,
    val lastResubmissionDate: ZonedDateTime?,
    val step2Active: Boolean? = false,
    val specificObjectiveCode: String?,
    val programmePriorityCode: String?
)
