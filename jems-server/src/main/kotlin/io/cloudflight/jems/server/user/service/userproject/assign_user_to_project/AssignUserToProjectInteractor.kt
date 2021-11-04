package io.cloudflight.jems.server.user.service.userproject.assign_user_to_project

import io.cloudflight.jems.server.user.service.model.UpdateProjectUser

interface AssignUserToProjectInteractor {
    fun updateUserAssignmentsOnProject(data: Set<UpdateProjectUser>)
}
