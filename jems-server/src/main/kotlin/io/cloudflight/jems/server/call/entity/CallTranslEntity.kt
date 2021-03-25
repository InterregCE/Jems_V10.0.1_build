package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_call_transl")
class CallTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<CallEntity>,
    val description: String? = null
) : TranslationEntity()
