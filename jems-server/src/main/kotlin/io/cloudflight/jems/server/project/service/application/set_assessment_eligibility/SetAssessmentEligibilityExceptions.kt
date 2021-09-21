package io.cloudflight.jems.server.project.service.application.set_assessment_eligibility

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.project.service.application.ApplicationStatus

private const val SET_ASSESSMENT_ELIGIBILITY_ERROR_CODE_PREFIX = "S-EA"
private const val SET_ASSESSMENT_ELIGIBILITY_ERROR_KEY_PREFIX = "use.case.set.assessment.eligibility"

class SetAssessmentEligibilityException(cause: Throwable) : ApplicationException(
    code = SET_ASSESSMENT_ELIGIBILITY_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$SET_ASSESSMENT_ELIGIBILITY_ERROR_KEY_PREFIX.failed"), cause = cause
)

class AssessmentStep1AlreadyConcluded : ApplicationUnprocessableException(
    code = "$SET_ASSESSMENT_ELIGIBILITY_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$SET_ASSESSMENT_ELIGIBILITY_ERROR_KEY_PREFIX.step1.already.concluded"),
)

class AssessmentStep2AlreadyConcluded : ApplicationUnprocessableException(
    code = "$SET_ASSESSMENT_ELIGIBILITY_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$SET_ASSESSMENT_ELIGIBILITY_ERROR_KEY_PREFIX.step2.already.concluded"),
)

class AssessmentStep1CannotBeConcludedInThisStatus(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$SET_ASSESSMENT_ELIGIBILITY_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$SET_ASSESSMENT_ELIGIBILITY_ERROR_KEY_PREFIX.wrong.status"),
    message = "current status is ${status.name}"
)

class AssessmentStep2CannotBeConcludedInThisStatus(status: ApplicationStatus) : ApplicationUnprocessableException(
    code = "$SET_ASSESSMENT_ELIGIBILITY_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$SET_ASSESSMENT_ELIGIBILITY_ERROR_KEY_PREFIX.wrong.status"),
    message = "current status is ${status.name}"
)
