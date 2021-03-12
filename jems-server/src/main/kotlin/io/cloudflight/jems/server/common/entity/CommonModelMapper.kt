package io.cloudflight.jems.server.common.entity

import io.cloudflight.jems.api.project.dto.InputTranslation

inline fun <T : TranslationEntity> Set<T>.extractField(extractFunction: (T) -> String?) =
    map { InputTranslation(it.language(), extractFunction.invoke(it)) }
        .filterTo(HashSet()) { !it.translation.isNullOrBlank() }
