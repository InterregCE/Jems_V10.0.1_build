package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.application.ApplicationStatus

data class ProjectApplicantAndStatus (
    val projectId: Long,
    val applicantId: Long,
    val collaboratorViewIds: Set<Long>,
    val collaboratorEditIds: Set<Long>,
    val collaboratorManageIds: Set<Long>,
    val projectStatus: ApplicationStatus
) {

    fun getUserIdsWithViewLevel() = collaboratorViewIds union collaboratorEditIds union collaboratorManageIds

    fun getUserIdsWithEditLevel() = collaboratorEditIds union collaboratorManageIds

    fun getUserIdsWithManageLevel() = collaboratorManageIds

}
