package io.cloudflight.jems.server.project.service.report.getProjectReportPartnerList

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_PARTNER_REPORTING_ERROR_CODE_PREFIX = "S-GPRPL"
private const val GET_PROJECT_PARTNER_REPORTING_ERROR_KEY_PREFIX = "use.case.get.project.report.partner.list"

class GetProjectReportPartnerListException(cause: Throwable) : ApplicationException(
    code = GET_PROJECT_PARTNER_REPORTING_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_PARTNER_REPORTING_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
