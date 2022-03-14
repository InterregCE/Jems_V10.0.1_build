package io.cloudflight.jems.server.project.service.report.partner.contribution.updateProjectPartnerReportContribution

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UDPATE_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_CODE_PREFIX = "S-UPPRC"
private const val UPDATE_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_KEY_PREFIX = "use.case.update.project.partner.report.contribution"

class UpdateProjectPartnerReportContributionException(cause: Throwable) : ApplicationException(
    code = UDPATE_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class MaxAmountOfContributionsReachedException(maxAmount: Int) : ApplicationUnprocessableException(
    code = "$UDPATE_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage(
        "$UPDATE_PROJECT_PARTNER_REPORT_CONTRIBUTION_ERROR_KEY_PREFIX.max.allowed.amount.reached",
        mapOf("maxSize" to maxAmount.toString())
    ),
    message = "max allowed: $maxAmount",
)
