package io.cloudflight.jems.server.controllerInstitution.service

import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution.AssignUsersToInstitutionException
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import org.springframework.stereotype.Service

@Service
class ControllerInstitutionValidator(
    private val generalValidator: GeneralValidatorService
) {

    fun validateInstitutionUsers(institutionUsers: List<ControllerInstitutionUser>, emailsOfUsersThatCanBePersisted: List<String>) {
        val institutionUsersEmails = institutionUsers.map { it.userEmail.lowercase() }.toMutableList()
        institutionUsersEmails.removeAll(emailsOfUsersThatCanBePersisted)
        if (institutionUsersEmails.isNotEmpty())
            throw AssignUsersToInstitutionException()
    }

    fun validateInputData(institutionController: UpdateControllerInstitution) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(institutionController.name, MAX_LENGTH_NAME, "name"),
            generalValidator.maxLength(institutionController.description, MAX_LENGTH_DESCRIPTION, "description")
        )
    }

    companion object {
        private const val MAX_LENGTH_NAME = 250
        private const val MAX_LENGTH_DESCRIPTION = 2000
    }
}
