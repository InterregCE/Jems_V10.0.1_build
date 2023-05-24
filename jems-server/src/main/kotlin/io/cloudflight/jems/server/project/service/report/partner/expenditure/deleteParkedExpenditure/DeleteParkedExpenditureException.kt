package io.cloudflight.jems.server.project.service.report.partner.expenditure.deleteParkedExpenditure

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-DPE"
private const val ERROR_KEY_PREFIX = "use.case.delete.parked.expenditure"

class DeleteParkedExpenditureException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ParkedExpenditureNotFound(expenditureId: Long) : ApplicationNotFoundException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(i18nKey = "$ERROR_KEY_PREFIX.expenditure.not.found"),
    message = "expenditureId: $expenditureId",
)

class DeletingParkedForbiddenIfReOpenedReportIsNotLast : ApplicationUnprocessableException(
    code = "${ERROR_CODE_PREFIX}-002",
    i18nMessage = I18nMessage("${ERROR_KEY_PREFIX}.reopened.report.not.last"),
)
