package io.cloudflight.jems.server.call.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_call_flat_rate")
data class FlatRateSetup(

    @EmbeddedId
    val setupId: FlatRateSetupId,

    @field:NotNull
    var rate: Int,

    @field:NotNull
    var isAdjustable: Boolean

)
