package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-CPR"
private const val CREATE_PROJECT_REPORT_ERROR_KEY_PREFIX = "use.case.create.project.report"

class CreateProjectReportException(cause: Throwable) : ApplicationException(
    code = CREATE_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_PROJECT_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class MaxAmountOfReportsReachedException : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_PROJECT_REPORT_ERROR_KEY_PREFIX.max.allowed.amount.reached"),
)

class ReportCanBeCreatedOnlyWhenContractedException : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_REPORT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$CREATE_PROJECT_REPORT_ERROR_KEY_PREFIX.wrong.status"),
)

class StartDateIsAfterEndDate : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_REPORT_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$CREATE_PROJECT_REPORT_ERROR_KEY_PREFIX.start.date.must.be.before.end.date"),
)

class LinkToDeadlineProvidedWithManualDataOverride : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_REPORT_ERROR_CODE_PREFIX-004",
    i18nMessage = I18nMessage("$CREATE_PROJECT_REPORT_ERROR_KEY_PREFIX.data.is.taken.from.deadline.do.not.provide.any.more.details"),
)

class LinkToDeadlineNotProvidedAndDataMissing : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_REPORT_ERROR_CODE_PREFIX-005",
    i18nMessage = I18nMessage("$CREATE_PROJECT_REPORT_ERROR_KEY_PREFIX.all.deadline.data.needs.to.be.specified.explicitly"),
)

class PeriodNumberInvalid(periodNumber: Int) : ApplicationUnprocessableException(
    code = "$CREATE_PROJECT_REPORT_ERROR_CODE_PREFIX-006",
    i18nMessage = I18nMessage("$CREATE_PROJECT_REPORT_ERROR_KEY_PREFIX.specified.period.is.not.available"),
    message = "period number $periodNumber is not available",
)
