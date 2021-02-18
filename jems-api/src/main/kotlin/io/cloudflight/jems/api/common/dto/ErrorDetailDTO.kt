package io.cloudflight.jems.api.common.dto

data class ErrorDetailDTO(
    val code: String,
    val i18nMessage: I18nMessage,
)
