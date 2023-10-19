package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.createPaymentApplicationToEc

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException

private const val CREATE_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX = "S-CPATED"
private const val CREATE_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX = "use.case.create.payment.applications.to.ec.detail"
class CreatePaymentApplicationToEcException(cause: Throwable) : ApplicationException (
    code = CREATE_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$CREATE_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX.failed"),
    cause = cause
)


class EcPaymentApplicationSameFundAccountingYearExistsException :  ApplicationUnprocessableException(
    code = "$CREATE_PAYMENT_APPLICATIONS_TO_EC_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$CREATE_PAYMENT_APPLICATIONS_TO_EC_ERROR_KEY_PREFIX.other.application.fund.accounting.year.already.exists\""),
)
