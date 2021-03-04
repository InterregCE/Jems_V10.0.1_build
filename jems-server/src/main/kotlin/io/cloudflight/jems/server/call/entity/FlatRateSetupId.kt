package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class FlatRateSetupId(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_id")
    @field:NotNull
    val call: CallEntity,

    @Column
    @field:NotNull
    val type: FlatRateType

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is FlatRateSetupId && call == other.call && type == other.type

    override fun hashCode(): Int = Objects.hash(call, type)

}
