package io.cloudflight.jems.server.dataGenerator

import io.cloudflight.jems.api.project.dto.InputTranslation

const val PROGRAMME_DATA_INITIALIZER_ORDER = 1000
const val CALL_DATA_INITIALIZER_ORDER = 2000
const val PROJECT_DATA_INITIALIZER_ORDER = 3000

fun inputTranslation(postfix: String) =
    PROGRAMME_INPUT_LANGUAGES.map {
        InputTranslation(it.code, it.code.name.plus(" - ").plus(postfix))
    }.toSet()

