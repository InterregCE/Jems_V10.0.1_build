package io.cloudflight.jems.server.project.service.result.update_project_results

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_RESULT_ERROR_CODE_PREFIX = "S-UPR"
private const val CREATE_PROJECT_ERROR_KEY_PREFIX = "use.case.update.project.results"

class CreateProjectResultExceptions(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_RESULT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_PROJECT_ERROR_KEY_PREFIX.failed"), cause = cause
)

class MaxNumberOrResultPerProjectException(maxNumber: Int) : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_RESULT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage( "$CREATE_PROJECT_ERROR_KEY_PREFIX.max.allowed.reached", mapOf("maxNumber" to maxNumber.toString())),
)

class PeriodNotFoundException : ApplicationNotFoundException(
    code = "$UPDATE_PROJECT_RESULT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage( "$CREATE_PROJECT_ERROR_KEY_PREFIX.period.not.found"),
)

class ProjectResultDeletionNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_RESULT_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$CREATE_PROJECT_ERROR_KEY_PREFIX.deletion.not.allowed.since.project.is.contracted"),
)

class ProjectResultDeactivationNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_RESULT_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$CREATE_PROJECT_ERROR_KEY_PREFIX.deactivation.not.allowed.since.project.is.not.contracted"),
)

class ProjectResultActivationNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_RESULT_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage("$CREATE_PROJECT_ERROR_KEY_PREFIX.activation.not.allowed"),
)
