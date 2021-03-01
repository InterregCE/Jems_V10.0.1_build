package io.cloudflight.jems.server.common.entity

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class TranslationId<T>(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_entity_id")
    @field:NotNull
    val sourceEntity: T,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

) : Serializable {
    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is TranslationId<*> &&
            sourceEntity == other.sourceEntity &&
            language == other.language

    override fun hashCode() =
        sourceEntity.hashCode().plus(language.translationKey.hashCode())

}
