package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.updateResultPrinciple

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_CODE_PREFIX = "S-UPRRP"
private const val UPDATE_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_KEY_PREFIX = "use.case.update.project.report.result.principle"

class UpdateProjectReportResultPrincipleException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_RESULT_PRINCIPLE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
