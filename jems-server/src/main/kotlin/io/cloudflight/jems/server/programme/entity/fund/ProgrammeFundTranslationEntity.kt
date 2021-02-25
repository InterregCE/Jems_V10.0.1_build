package io.cloudflight.jems.server.programme.entity.fund

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "programme_fund_transl")
data class ProgrammeFundTranslationEntity(
    @EmbeddedId
    val translationId: ProgrammeFundTranslationId,
    val abbreviation: String? = null,
    val description: String? = null,
) : Serializable
