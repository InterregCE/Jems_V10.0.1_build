package io.cloudflight.jems.server.controllerInstitution.service

import io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution.AssignUsersToInstitutionException
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import org.springframework.stereotype.Service

@Service
class ControllerInstitutionValidator {

    fun validateInstitutionUsers(institutionUsers: List<ControllerInstitutionUser>, emailsOfUsersThatCanBePersisted: List<String>) {
        val institutionUsersEmails = institutionUsers.map { it.userEmail.lowercase() }.toMutableList()
        institutionUsersEmails.removeAll(emailsOfUsersThatCanBePersisted)
        if (institutionUsersEmails.isNotEmpty())
            throw AssignUsersToInstitutionException()
    }


}
