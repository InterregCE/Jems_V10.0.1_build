package io.cloudflight.ems.exception

import org.springframework.http.HttpStatus

data class I18nValidationError(
    val i18nKey: String? = null,
    val i8nArguments: List<String>? = null,
    val httpStatus: HttpStatus,
    val i18nFieldErrors: Map<String, I18nFieldError>? = null,
    val additionalInfo: String? = null
) : Exception()
