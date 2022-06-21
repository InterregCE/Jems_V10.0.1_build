package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import java.time.ZonedDateTime

data class ProjectSummary(
    val id: Long,
    val customIdentifier: String,
    val callName: String,
    val acronym: String,
    val status: ApplicationStatus,
    val firstSubmissionDate: ZonedDateTime? = null,
    val lastResubmissionDate: ZonedDateTime? = null,
    val specificObjectiveCode: String? = null,
    val programmePriorityCode: String? = null,
) {
    fun isInStep2() = status.isInStep2()

    fun isInStep1() = status.isInStep1()
}

