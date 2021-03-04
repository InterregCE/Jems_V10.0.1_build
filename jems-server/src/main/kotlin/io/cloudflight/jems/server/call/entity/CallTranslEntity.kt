package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_call_transl")
class CallTranslEntity(

    @EmbeddedId
    val translationId: TranslationId<CallEntity>,

    val description: String? = null

): TranslationEntity {

    override fun language() = translationId.language

    override fun equals(other: Any?) = this === other ||
        (other is CallTranslEntity && translationId == other.translationId)

    override fun hashCode() = translationId.hashCode()
}
