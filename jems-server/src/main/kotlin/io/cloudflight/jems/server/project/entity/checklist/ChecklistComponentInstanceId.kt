package io.cloudflight.jems.server.project.entity.checklist

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ChecklistComponentInstanceId(

    @Column
    @field:NotNull
    val programmeComponentId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_instance_id")
    var checklist: ChecklistInstanceEntity? = null,

) : Serializable  {
    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ChecklistComponentInstanceId &&
            programmeComponentId == other.programmeComponentId &&
            checklist == other.checklist

    override fun hashCode() =
        programmeComponentId.hashCode().plus(checklist.hashCode())

}
