package io.cloudflight.jems.server.project.service.auditAndControl.correction.finance.updateProjectCorrectionFinancialDescription

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val UPDATE_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_CODE_PREFIX = "S-UPCCFD"
private const val UPDATE_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_KEY_PREFIX = "use.case.update.project.correction.financial.description"

class UpdateCorrectionFinancialDescriptionException(cause: Throwable): ApplicationException(
    code = UPDATE_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$UPDATE_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)

class AuditControlIsInStatusClosedException: ApplicationUnprocessableException(
    code =  "$UPDATE_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_KEY_PREFIX.control.status.is.closed"),
)

class CorrectionIsInStatusClosedException: ApplicationUnprocessableException(
    code =  "$UPDATE_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$UPDATE_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_KEY_PREFIX.correction.status.is.closed"),
)

