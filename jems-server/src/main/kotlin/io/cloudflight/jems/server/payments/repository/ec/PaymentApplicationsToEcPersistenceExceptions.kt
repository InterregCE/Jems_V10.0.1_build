package io.cloudflight.jems.server.payments.repository.ec

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationNotFoundException

private const val PAYMENT_APPLICATIONS_TO_EC_PERSISTENCE_ERROR_CODE = "R-PATEP"

class ProgrammeFundNotFound : ApplicationNotFoundException (
    code = "$PAYMENT_APPLICATIONS_TO_EC_PERSISTENCE_ERROR_CODE-001",
    i18nMessage = I18nMessage("programme.fund.not.found"),
)

class AccountingYearNotFound: ApplicationNotFoundException (
    code = "$PAYMENT_APPLICATIONS_TO_EC_PERSISTENCE_ERROR_CODE-002",
    i18nMessage = I18nMessage("accounting.year.not.found")
)
