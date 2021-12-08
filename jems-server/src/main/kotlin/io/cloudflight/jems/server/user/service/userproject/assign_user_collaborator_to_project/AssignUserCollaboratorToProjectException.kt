package io.cloudflight.jems.server.user.service.userproject.assign_user_collaborator_to_project

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ASSIGN_USER_COLLABORATOR_TO_PROJECT_ERROR_CODE_PREFIX = "S-AUCTP"
private const val ASSIGN_USER_COLLABORATOR_TO_PROJECT_ERROR_KEY_PREFIX = "use.case.assign.user.collaborator.to.project"

class AssignUserCollaboratorToProjectException(cause: Throwable) : ApplicationException(
    code = ASSIGN_USER_COLLABORATOR_TO_PROJECT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ASSIGN_USER_COLLABORATOR_TO_PROJECT_ERROR_KEY_PREFIX.failed"),
    cause = cause
)


class UsersAreNotValid(emails: Collection<String>) : ApplicationUnprocessableException(
    code = "$ASSIGN_USER_COLLABORATOR_TO_PROJECT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ASSIGN_USER_COLLABORATOR_TO_PROJECT_ERROR_KEY_PREFIX.users.are.not.valid"),
    formErrors = emails.associateBy({ it }, { I18nMessage("user.does.not.exist.or.he.cannot.be.assigned", mapOf("email" to it)) }),
)

class MinOneManagingCollaboratorRequiredException : ApplicationUnprocessableException(
    code = "$ASSIGN_USER_COLLABORATOR_TO_PROJECT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$ASSIGN_USER_COLLABORATOR_TO_PROJECT_ERROR_KEY_PREFIX.min.one.managing.collaborator"),
)
