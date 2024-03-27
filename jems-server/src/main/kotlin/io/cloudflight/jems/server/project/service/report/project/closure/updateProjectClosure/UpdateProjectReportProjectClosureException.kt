package io.cloudflight.jems.server.project.service.report.project.closure.updateProjectClosure

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_REPORT_PROJECT_CLOSURE_ERROR_CODE_PREFIX = "S-UPRPCP"
private const val UPDATE_PROJECT_REPORT_PROJECT_CLOSURE_ERROR_KEY_PREFIX = "use.case.update.project.report.project.closure"

class UpdateProjectReportProjectClosureException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_REPORT_PROJECT_CLOSURE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_PROJECT_CLOSURE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ProjectClosurePrizeLimitNumberExceededException : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_REPORT_PROJECT_CLOSURE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(i18nKey = "$UPDATE_PROJECT_REPORT_PROJECT_CLOSURE_ERROR_KEY_PREFIX.prize.limit.number.exceeded"),
)
