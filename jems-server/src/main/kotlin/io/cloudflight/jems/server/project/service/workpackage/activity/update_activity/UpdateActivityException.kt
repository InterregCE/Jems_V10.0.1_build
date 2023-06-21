package io.cloudflight.jems.server.project.service.workpackage.activity.update_activity

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.programme.service.fund.updateFunds.UPDATE_FUNDS_ERROR_CODE_PREFIX
import io.cloudflight.jems.server.programme.service.fund.updateFunds.UPDATE_FUNDS_ERROR_KEY_PREFIX

private const val UPDATE_ACTIVITY_ERROR_CODE_PREFIX = "S-UA"
private const val UPDATE_ACTIVITY_ERROR_KEY_PREFIX = "use.case.update.activity"

class UpdateActivityException(cause: Throwable) : ApplicationException(
    code = UPDATE_ACTIVITY_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_ACTIVITY_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class PartnersNotFound(partnerIds: Set<Long>) : ApplicationNotFoundException(
    code = "$UPDATE_ACTIVITY_ERROR_CODE_PREFIX-001",
    message = "PartnerIds: " + partnerIds.joinToString(", "),
    i18nMessage = I18nMessage("$UPDATE_ACTIVITY_ERROR_KEY_PREFIX.partners.not-found"),
)

class ActivityDeletionNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_ACTIVITY_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_ACTIVITY_ERROR_KEY_PREFIX.deletion.not.allowed.since.project.is.contracted"),
)

class ActivityDeactivationNotAllowedException : ApplicationUnprocessableException(
    code = "$UPDATE_ACTIVITY_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_ACTIVITY_ERROR_KEY_PREFIX.deactivation.not.allowed.since.project.is.not.contracted"),
)
