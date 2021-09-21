package io.cloudflight.jems.server.common.entity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.time.ZoneId
import java.time.ZonedDateTime

inline fun <T : TranslationEntity> Set<T>.extractField(extractFunction: (T) -> String?) =
    map { InputTranslation(it.language(), extractFunction.invoke(it)) }
        .filterTo(HashSet()) { !it.translation.isNullOrBlank() }

inline fun <T : TranslationView> List<T>.extractField(extractFunction: (T) -> String?) =
    filter { it.language !=null }.toHashSet().map { InputTranslation(it.language!!, extractFunction.invoke(it)) }
        .filterTo(HashSet()) { !it.translation.isNullOrBlank() }

fun <T : TranslationEntity> MutableSet<T>.resetTranslations(newTranslations: Set<T>, updater: (T, T) -> Unit) {
    this.removeIf { translation ->
        !newTranslations.map { it.language() }.contains(translation.language())
    }
    this.forEach { currentTranslation ->
        val newTranslation = newTranslations.first { it.language() == currentTranslation.language() }
        updater.invoke(currentTranslation, newTranslation)
    }
    val newLanguages = newTranslations.map { it.language() }.subtract(this.map { it.language() })
    this.addAll(newTranslations.filter { newLanguages.contains(it.language()) })

}

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


fun Int?.toYear(): ZonedDateTime? {
    if (this == null)
        return null
    return ZonedDateTime.of(this, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC").normalized())
}
