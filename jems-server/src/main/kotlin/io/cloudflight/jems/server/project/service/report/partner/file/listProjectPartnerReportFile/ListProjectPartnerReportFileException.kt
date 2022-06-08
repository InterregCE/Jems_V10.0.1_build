package io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType

private const val LIST_PROJECT_PARTNER_REPORT_FILE_ERROR_CODE_PREFIX = "S-LPPRF"
private const val LIST_PROJECT_PARTNER_REPORT_FILE_ERROR_KEY_PREFIX = "use.case.list.project.partner.report.file"

class ListProjectPartnerReportFileException(cause: Throwable) : ApplicationException(
    code = LIST_PROJECT_PARTNER_REPORT_FILE_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_PROJECT_PARTNER_REPORT_FILE_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class InvalidSearchConfiguration : ApplicationUnprocessableException(
    code = "$LIST_PROJECT_PARTNER_REPORT_FILE_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$LIST_PROJECT_PARTNER_REPORT_FILE_ERROR_CODE_PREFIX.invalid.search.configuration"),
)

class InvalidSearchFilterConfiguration(invalidFilters: Set<ProjectPartnerReportFileType>) : ApplicationUnprocessableException(
    code = "$LIST_PROJECT_PARTNER_REPORT_FILE_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$LIST_PROJECT_PARTNER_REPORT_FILE_ERROR_CODE_PREFIX.invalid.search.filter.configuration"),
    message = "Following filters cannot be applied: $invalidFilters",
)
