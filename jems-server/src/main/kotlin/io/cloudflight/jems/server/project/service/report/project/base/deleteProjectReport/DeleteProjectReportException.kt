package io.cloudflight.jems.server.project.service.report.project.base.deleteProjectReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel

private const val DELETE_PROJECT_REPORT_ERROR_CODE_PREFIX = "S-DEPR"
private const val DELETE_PROJECT_REPORT_ERROR_KEY_PREFIX = "use.case.delete.project.report"

class DeleteProjectReportException(cause: Throwable) : ApplicationException(
    code = DELETE_PROJECT_REPORT_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$DELETE_PROJECT_REPORT_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class ThereIsNoAnyReportForProject : ApplicationNotFoundException(
    code = "$DELETE_PROJECT_REPORT_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$DELETE_PROJECT_REPORT_ERROR_KEY_PREFIX.no.report.for.project"),
)

class OnlyLastOpenReportCanBeDeleted(lastOpenReport: ProjectReportModel) : ApplicationUnprocessableException(
    code = "$DELETE_PROJECT_REPORT_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage(
        i18nKey = "$DELETE_PROJECT_REPORT_ERROR_KEY_PREFIX.only.last.open.report.can.be.deleted",
        i18nArguments = mapOf("lastReport" to "R.${lastOpenReport.reportNumber}"),
    ),
)
