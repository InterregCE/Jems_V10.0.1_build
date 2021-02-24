package io.cloudflight.jems.server.programme.entity.costoption

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "programme_lump_sum_transl")
data class ProgrammeLumpSumTranslEntity(

    @EmbeddedId
    val translationId: ProgrammeLumpSumTranslId,

    @Column
    val name: String? = null,

    @Column
    val description: String? = null
)
