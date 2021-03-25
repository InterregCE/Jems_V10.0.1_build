package io.cloudflight.jems.server.programme.entity.fund

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "programme_fund_transl")
class ProgrammeFundTranslationEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProgrammeFundEntity>,
    val abbreviation: String? = null,
    val description: String? = null,
) : TranslationEntity()
