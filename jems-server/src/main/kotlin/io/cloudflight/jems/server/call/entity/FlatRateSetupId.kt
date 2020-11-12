package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Embeddable
data class FlatRateSetupId(

    @field:NotNull
    val callId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: FlatRateType

) : Serializable
