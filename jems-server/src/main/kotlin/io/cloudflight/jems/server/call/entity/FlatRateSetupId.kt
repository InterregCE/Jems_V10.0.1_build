package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
data class FlatRateSetupId(

    @field:NotNull
    val callId: Long,

    @Column
    @field:NotNull
    val type: FlatRateType

) : Serializable
