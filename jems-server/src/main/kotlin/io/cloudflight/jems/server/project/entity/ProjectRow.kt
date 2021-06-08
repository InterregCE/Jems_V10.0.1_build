package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectRow: TranslationView {
    val id: Long

    // NON-historic data
    // call - project_call
    // priorityPolicy - programme_priority_specific_objective
    // currentStatus - project_status
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
}
