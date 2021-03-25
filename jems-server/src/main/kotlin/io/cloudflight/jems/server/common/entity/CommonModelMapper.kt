package io.cloudflight.jems.server.common.entity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation

inline fun <T : TranslationEntity> Set<T>.extractField(extractFunction: (T) -> String?) =
    map { InputTranslation(it.language(), extractFunction.invoke(it)) }
        .filterTo(HashSet()) { !it.translation.isNullOrBlank() }

fun <T : TranslationEntity> MutableSet<T>.addTranslationEntities(
    entitySupplier: (SystemLanguage) -> T,
    translatableFields: Array<Set<InputTranslation>>
) {
    addAll(
        translatableFields.map { arg -> arg.filter { !it.translation.isNullOrBlank() }.map { it.language } }.flatten()
            .toSet()
            .map { language -> entitySupplier.invoke(language) }
    )
}

fun Set<InputTranslation>.extractTranslation(language: SystemLanguage) =
    firstOrNull { it.language == language }?.translation ?: ""
