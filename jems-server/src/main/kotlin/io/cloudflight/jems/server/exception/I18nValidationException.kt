package io.cloudflight.jems.server.exception

import org.springframework.http.HttpStatus
import java.util.Objects

class I18nValidationException(
    val i18nKey: String? = null,
    val i18nArguments: List<String>? = null,
    val httpStatus: HttpStatus,
    val i18nFieldErrors: Map<String, I18nFieldError>? = null,
    val additionalInfo: String? = null
) : Exception() {

    fun getData(): I18nValidationError {
        return I18nValidationError(
            i18nKey = i18nKey,
            i18nArguments = i18nArguments,
            httpStatus = httpStatus,
            i18nFieldErrors = i18nFieldErrors,
            additionalInfo = additionalInfo
        )
    }

    override fun equals(other: Any?): Boolean
        = (other is I18nValidationException)
        && i18nKey == other.i18nKey
        && Objects.equals(i18nArguments, other.i18nArguments)
        && httpStatus == other.httpStatus
        && Objects.equals(i18nFieldErrors, other.i18nFieldErrors)
        && additionalInfo == other.additionalInfo

    override fun hashCode(): Int {
        return Objects.hash(i18nKey, i18nArguments, httpStatus, i18nFieldErrors, additionalInfo)
    }

}
