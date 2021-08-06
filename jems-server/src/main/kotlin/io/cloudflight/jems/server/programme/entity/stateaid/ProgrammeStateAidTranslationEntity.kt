package io.cloudflight.jems.server.programme.entity.stateaid

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "programme_state_aid_transl")
class ProgrammeStateAidTranslationEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProgrammeStateAidEntity>,
    val name: String? = null,
    val abbreviatedName: String? = null,
    val comments: String? = null
) : TranslationEntity()
