package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.server.common.entity.TranslationView
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.service.model.UserStatus
import java.sql.Timestamp
import java.time.LocalDate


interface ProjectRow: TranslationView {
    val id: Long
    val customIdentifier: String

    // NON-historic data
    // call - project_call
    // priorityPolicy - programme_priority_specific_objective
    // firstSubmission - project_status
    // lastResubmission - project_status
    // applicant - account
    // firstStepDecision - project_decision
    // secondStepDecision - project_decision

    val acronym: String
    val duration: Int?

    // projectData - project_transl
    val title: String?
    val intro: String?

    // current Status
    val statusId: Long?
    val status: ApplicationStatus
    val updated: Timestamp
    val decisionDate: LocalDate
    val entryIntoForceDate: LocalDate
    val note: String

    // user summary
    val userId: Long
    val email: String
    val name: String
    val surname: String
    val userStatus: UserStatus

    // user role
    val roleId: Long
    val roleName: String
}
