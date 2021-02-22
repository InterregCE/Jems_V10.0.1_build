package io.cloudflight.jems.api.common.dto

data class I18nMessage(
    val i18nKey: String? = null,
    val i18nArguments: Map<String, String> = hashMapOf()
)
