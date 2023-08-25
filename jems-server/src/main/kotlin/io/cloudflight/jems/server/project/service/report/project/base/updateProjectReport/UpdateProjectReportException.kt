package io.cloudflight.jems.server.project.service.report.project.base.updateProjectReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-UPR"
private const val UPDATE_PROJECT_REPORT_ERROR_KEY_PREFIX = "use.case.update.project.report"

class UpdateProjectReportException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class StartDateIsAfterEndDate : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_ERROR_KEY_PREFIX.start.date.must.be.before.end.date"),
)

class LinkToDeadlineProvidedWithManualDataOverride : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_REPORT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_ERROR_KEY_PREFIX.data.is.taken.from.deadline.do.not.provide.any.more.details"),
)

class LinkToDeadlineNotProvidedAndDataMissing : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_REPORT_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_ERROR_KEY_PREFIX.all.deadline.data.needs.to.be.specified.explicitly"),
)

class PeriodNumberInvalid(periodNumber: Int) : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_REPORT_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_ERROR_KEY_PREFIX.specified.period.is.not.available"),
    message = "period number $periodNumber is not available",
)

class TypeChangeIsForbiddenWhenReportIsReOpened : ApplicationUnprocessableException(
    code = "$UPDATE_PROJECT_REPORT_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_ERROR_KEY_PREFIX.type.change.forbidden"),
)
