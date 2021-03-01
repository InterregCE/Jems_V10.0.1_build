package io.cloudflight.jems.server.programme.entity.indicator

import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "programme_indicator_result_transl")
class ResultIndicatorTranslEntity(

    @EmbeddedId val translationId: TranslationId<ResultIndicatorEntity>,

    val name: String?,

    val measurementUnit: String? = null,

    val sourceOfData: String? = null

) {
    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ResultIndicatorTranslEntity &&
            translationId == other.translationId

    override fun hashCode() =
        translationId.hashCode()
}
