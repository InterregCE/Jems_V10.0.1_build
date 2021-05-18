package io.cloudflight.jems.server.common.exception

import io.cloudflight.jems.api.common.dto.APIErrorDTO
import io.cloudflight.jems.api.common.dto.ErrorDetailDTO
import io.cloudflight.jems.api.common.dto.I18nMessage
import java.util.UUID

const val DEFAULT_ERROR_CODE = "ERR"
val DEFAULT_ERROR_MESSAGE = I18nMessage(i18nKey = "unknown.error.message")

fun createAPIErrorDTO(
    exception: Throwable?,
    defaultErrorCode: String = DEFAULT_ERROR_CODE,
    defaultI18nMessage: I18nMessage = DEFAULT_ERROR_MESSAGE,
    defaultMessage: String = ""
): APIErrorDTO {
    val errorId = UUID.randomUUID().toString()
    val errorCode = if (exception is ApplicationException) exception.code else defaultErrorCode
    val errorI18nMessage = if (exception is ApplicationException) exception.i18nMessage else defaultI18nMessage
    var formErrors = if (exception is ApplicationException) exception.formErrors else hashMapOf()
    var message = if (exception is ApplicationException) exception.message else defaultMessage

    val errorDetail = mutableListOf<ErrorDetailDTO>()
    var cause = exception?.cause

    while (cause != null) {
        if (cause is ApplicationException) {
            errorDetail.add(ErrorDetailDTO(cause.code, cause.i18nMessage))
            formErrors = formErrors.plus(cause.formErrors)
            if (!cause.message.isNullOrBlank()) message = cause.message
        }
        cause = cause.cause
    }
    return APIErrorDTO(errorId, errorCode, errorI18nMessage, errorDetail, formErrors, message)
}

fun APIErrorDTO.toMap(): MutableMap<String, Any> {
    return hashMapOf(
        "id" to this.id,
        "code" to this.code,
        "i18nMessage" to this.i18nMessage,
        "detail" to this.details,
        "formErrors" to this.formErrors,
        "message" to this.message
    )
}
