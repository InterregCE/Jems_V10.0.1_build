package io.cloudflight.jems.server.programme.entity.costoption

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "programme_unit_cost_transl")
data class ProgrammeUnitCostTranslEntity(

    @EmbeddedId
    val translationId: ProgrammeUnitCostTranslId,

    @Column
    val name: String? = null,

    @Column
    val description: String? = null,

    @Column
    val type: String? = null

)
