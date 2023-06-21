package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableParkedExpenditureList

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val ERROR_CODE_PREFIX = "S-GAPEL"
private const val ERROR_KEY_PREFIX = "use.case.get.available.parked.expenditure.list"

class GetAvailableParkedExpenditureListException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
