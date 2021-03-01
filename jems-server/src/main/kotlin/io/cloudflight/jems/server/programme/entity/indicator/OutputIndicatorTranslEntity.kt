package io.cloudflight.jems.server.programme.entity.indicator

import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "programme_indicator_output_transl")
class OutputIndicatorTranslEntity(

    @EmbeddedId val translationId: TranslationId<OutputIndicatorEntity>,

    val name: String?,

    val measurementUnit: String? = null

)  {
    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is OutputIndicatorTranslEntity &&
            translationId == other.translationId

    override fun hashCode() =
        translationId.hashCode()
}
