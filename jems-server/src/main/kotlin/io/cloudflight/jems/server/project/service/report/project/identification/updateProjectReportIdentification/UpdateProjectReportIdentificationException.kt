package io.cloudflight.jems.server.project.service.report.project.identification.updateProjectReportIdentification

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val UPDATE_PROJECT_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX = "S-UPRI"
private const val UPDATE_PROJECT_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX = "use.case.update.project.report.identification"

class UpdateProjectReportIdentificationException(cause: Throwable) : ApplicationException(
    code = UPDATE_PROJECT_REPORT_IDENTIFICATION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_REPORT_IDENTIFICATION_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)
