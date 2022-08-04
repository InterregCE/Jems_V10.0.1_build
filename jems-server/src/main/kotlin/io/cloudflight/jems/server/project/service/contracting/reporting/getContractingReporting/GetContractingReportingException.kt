package io.cloudflight.jems.server.project.service.contracting.reporting.getContractingReporting

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_CONTRACTING_REPORTING_ERROR_CODE_PREFIX = "S-GPCR"
private const val GET_CONTRACTING_REPORTING_ERROR_KEY_PREFIX = "use.case.get.project.contracting.reporting"

class GetContractingReportingException(cause: Throwable) : ApplicationException(
    code = GET_CONTRACTING_REPORTING_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_CONTRACTING_REPORTING_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
