package io.cloudflight.jems.api.common.dto

data class APIErrorDTO(
    val id: String,
    val code: String,
    val i18nMessage: I18nMessage,
    val details: List<ErrorDetailDTO>,
    val formErrors: Map<String, I18nMessage>,
    val message: String = ""
)
