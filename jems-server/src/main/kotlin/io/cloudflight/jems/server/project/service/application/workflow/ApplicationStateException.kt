package io.cloudflight.jems.server.project.service.application.workflow

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.project.service.application.ApplicationStatus

const val APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX = "S-PA-PAA"
const val APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX = "application.action"

class SubmitIsNotAllowedException(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        "$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.submit.is.not.allowed",
        mapOf("status" to status.name)
    )
)

class SetAsEligibleIsNotAllowedException(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        "$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.set.as.eligible.is.not.allowed",
        mapOf("status" to status.name)
    )
)

class SetAsIneligibleIsNotAllowedException(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage(
        "$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.set.as.ineligible.is.not.allowed",
        mapOf("status" to status.name)
    )
)

class ApproveIsNotAllowedException(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage(
        "$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.approve.is.not.allowed",
        mapOf("status" to status.name)
    )
)

class ApproveWithConditionsIsNotAllowedException(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage(
        "$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.approve.with.condition.is.not.allowed",
        mapOf("status" to status.name)
    )
)

class ReturnToApplicantIsNotAllowedException(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-006",
    i18nMessage = I18nMessage(
        "$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.return.to.applicant.is.not.allowed",
        mapOf("status" to status.name)
    )
)

class RefuseIsNotAllowedException(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-007",
    i18nMessage = I18nMessage(
        "$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.refuse.is.not.allowed",
        mapOf("status" to status.name)
    )
)

class RevertLastActionOnApplicationIsNotAllowedException(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-008",
    i18nMessage = I18nMessage(
        "$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.revert.last.action.on.application.is.not.allowed",
        mapOf("status" to status.name)
    )
)

class CallIsNotOpenException : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-009",
    i18nMessage = I18nMessage("$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.call.is.not.open")
)

class DecisionReversionIsNotPossibleException(fromStatus: ApplicationStatus, toStatus:ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-010",
    i18nMessage = I18nMessage("$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.reversion.of.decision.is.not.possible",
        mapOf("fromStatus" to fromStatus.name, "toStatus" to toStatus.name )
    )
)

class FundingDecisionIsBeforeEligibilityDecisionException : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-011",
    i18nMessage = I18nMessage("$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.funding.decision.is.before.eligibility.decision")
)

class ReturnToApplicantIsNotPossibleException(fromStatus: ApplicationStatus, toStatus:ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-012",
    i18nMessage = I18nMessage("$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.reversion.of.decision.is.not.possible",
        mapOf("fromStatus" to fromStatus.name, "toStatus" to toStatus.name )
    )
)

class StartSecondStepIsNotAllowedException(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-013",
    i18nMessage = I18nMessage(
        "$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.start.second.step.is.not.allowed",
        mapOf("status" to status.name)
    )
)

class HandBackToApplicantIsNotAllowedException(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$APPLICATION_STATE_ACTIONS_ERROR_CODE_PREFIX-014",
    i18nMessage = I18nMessage(
        "$APPLICATION_STATE_ACTIONS_ERROR_KEY_PREFIX.hand.back.to.applicant.is.not.allowed",
        mapOf("status" to status.name)
    )
)
