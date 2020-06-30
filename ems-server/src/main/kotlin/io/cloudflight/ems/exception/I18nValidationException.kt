package io.cloudflight.ems.exception

import org.springframework.http.HttpStatus

class I18nValidationException(
    val i18nKey: String? = null,
    val i8nArguments: List<String>? = null,
    val httpStatus: HttpStatus,
    val i18nFieldErrors: Map<String, I18nFieldError>? = null,
    val additionalInfo: String? = null
) : Exception() {

    fun getData(): I18nValidationError {
        return I18nValidationError(
            i18nKey = i18nKey,
            i8nArguments = i8nArguments,
            httpStatus = httpStatus,
            i18nFieldErrors = i18nFieldErrors,
            additionalInfo = additionalInfo
        )
    }

}
