package io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.getProjectCorrectionFinancialDescription

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException

private const val GET_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_CODE_PREFIX = "S-PCCFD"
private const val GET_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_KEY_PREFIX = "use.case.get.project.correction.financial.description"

class GetProjectCorrectionFinancialDescriptionException(cause: Throwable): ApplicationException(
    code = GET_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$GET_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION_ERROR_KEY_PREFIX.failed"),
    cause = cause
)
