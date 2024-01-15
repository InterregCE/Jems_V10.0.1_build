package io.cloudflight.jems.server.project.service.report.project.base.reOpenVerificationProjectReport

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX = "S-RVPR"
private const val ERROR_KEY_PREFIX = "use.case.reopen.verification.project.report"

class ReOpenVerificationProjectReportException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class VerificationReportNotFinalized: ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.not.finalized"),
)

class VerificationReportHasPaymentInstallments: ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.has.payment.installments"),
)

class VerificationReportIncludedInPaymentToEc: ApplicationUnprocessableException(
    code = "$ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.included.in.payment.to.ec"),
)
