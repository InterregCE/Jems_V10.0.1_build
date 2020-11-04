package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class FlatRateSetupId(

    @Column(name = "call_id", nullable = false)
    val callId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: FlatRateType

) : Serializable
