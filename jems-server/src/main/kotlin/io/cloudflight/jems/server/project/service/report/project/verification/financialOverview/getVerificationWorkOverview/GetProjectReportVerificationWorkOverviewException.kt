package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val ERROR_CODE_PREFIX = "S-GPRVWO"
private const val ERROR_KEY_PREFIX = "use.case.get.project.report.verification.work.overview"

class GetProjectReportVerificationWorkOverviewException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause
)
