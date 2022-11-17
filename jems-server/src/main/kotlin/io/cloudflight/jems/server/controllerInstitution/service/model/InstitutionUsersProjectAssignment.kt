package io.cloudflight.jems.server.controllerInstitution.service.model

data class InstitutionUsersProjectAssignment(
    val userIdsToAdd: MutableSet<Long>,
    val userIdsToRemove: MutableSet<Long>
)
