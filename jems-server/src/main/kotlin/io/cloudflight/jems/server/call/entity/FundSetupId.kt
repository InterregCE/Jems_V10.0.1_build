package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class FundSetupId(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_id")
    @field:NotNull
    val call: CallEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_fund")
    @field:NotNull
    val programmeFund: ProgrammeFundEntity

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is FundSetupId && call == other.call && programmeFund == other.programmeFund

    override fun hashCode(): Int = Objects.hash(call, programmeFund)

}
