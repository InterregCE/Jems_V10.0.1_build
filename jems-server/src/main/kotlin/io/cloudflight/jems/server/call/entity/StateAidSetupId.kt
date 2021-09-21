package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class StateAidSetupId (
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_call_id")
    @field:NotNull
    val call: CallEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_state_aid")
    @field:NotNull
    val stateAid: ProgrammeStateAidEntity
) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is StateAidSetupId && call == other.call && stateAid == other.stateAid

    override fun hashCode(): Int = Objects.hash(call, stateAid)

}
