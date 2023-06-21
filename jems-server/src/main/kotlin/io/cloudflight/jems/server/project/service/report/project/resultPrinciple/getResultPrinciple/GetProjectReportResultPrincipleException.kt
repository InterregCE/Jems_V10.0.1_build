package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.getResultPrinciple

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_CODE_PREFIX = "S-GPRRP"
private const val GET_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_KEY_PREFIX = "use.case.get.project.report.result.principle"

class GetProjectReportResultPrincipleException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
