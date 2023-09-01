package io.cloudflight.jems.server.project.service.report.project.certificate.selectCertificate

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-SC"
private const val ERROR_KEY_PREFIX = "use.case.select.certificate"

class SelectCertificateException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class CertificateIsNotFinalized : ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.project.report.is.not.finalized"),
)

class CertificateNotFound : ApplicationNotFoundException(
    code = "$ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.not.found"),
)

class ProjectReportDoesNotIncludeFinance : ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.project.report.does.not.include.finance"),
)
