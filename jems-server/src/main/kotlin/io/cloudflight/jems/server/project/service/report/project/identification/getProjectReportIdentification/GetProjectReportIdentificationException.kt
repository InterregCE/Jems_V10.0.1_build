package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX = "S-GPRI"
private const val GET_PROJECT_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX = "use.case.get.project.report.identification"

class GetProjectReportIdentificationException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
