package io.cloudflight.jems.server.call.entity

import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class CallSelectedChecklistId(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_call_id")
    @field:NotNull
    val call: CallEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_checklist_id")
    @field:NotNull
    val programmeChecklist: ProgrammeChecklistEntity
) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
            other is CallSelectedChecklistId && call == other.call && programmeChecklist.id == other.programmeChecklist.id

    override fun hashCode(): Int = Objects.hash(call, programmeChecklist)
}
