package io.cloudflight.jems.server.exception

data class I18nFieldError(
    val i18nKey: String? = null,
    val i18nArguments: List<String>? = null
)
