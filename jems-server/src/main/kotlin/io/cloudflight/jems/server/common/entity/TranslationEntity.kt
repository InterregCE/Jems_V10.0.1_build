package io.cloudflight.jems.server.common.entity

abstract class TranslationEntity {
    abstract val translationId: TranslationId<*>

    fun language() =
        translationId.language

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is TranslationEntity &&
            translationId == other.translationId

    override fun hashCode() =
        translationId.hashCode()
}
