package io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.updateSpfContributionClaim

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val ERROR_CODE_PREFIX="S-USPFC"
private const val ERROR_KEY_PREFIX="use.case.update.project.report.spf.contribution"


class UpdateSpfContributionClaimException(cause: Throwable) : ApplicationException(
    code = ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class ReportStatusNotValidException : ApplicationUnprocessableException(
    code = "${ERROR_CODE_PREFIX}-001",
    i18nMessage = I18nMessage(i18nKey = "${ERROR_KEY_PREFIX}.report.already.closed.or.reOpen.mode.limited"),
)


class ContributionSourcesException : ApplicationUnprocessableException(
    code = "${ERROR_CODE_PREFIX}-002",
    i18nMessage = I18nMessage(i18nKey = "${ERROR_KEY_PREFIX}.sources.not.valid"),
)
