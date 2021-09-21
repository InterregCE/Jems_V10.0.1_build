package io.cloudflight.jems.server.programme.service.translation.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage

enum class TranslationFileType {
    System,
    Application;

    fun getFileNameFor(language: SystemLanguage) =
        this.name.plus("_").plus(language.name.toLowerCase()).plus(".properties")

}
