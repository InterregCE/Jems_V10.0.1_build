package io.cloudflight.jems.server.project.service.report.partner.expenditure.reincludeParkedExpenditure

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-RIPE"
private const val ERROR_KEY_PREFIX = "use.case.re.include.parked.expenditure"

class ReIncludeParkedExpenditureException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ReIncludingForbiddenIfReOpenedReportIsNotLast : ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.reopened.report.not.last"),
)
