package io.cloudflight.jems.server.programme.entity.legalstatus

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "programme_legal_status_transl")
class ProgrammeLegalStatusTranslationEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProgrammeLegalStatusEntity>,
    val description: String? = null,
) : TranslationEntity()
