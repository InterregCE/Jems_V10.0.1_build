package io.cloudflight.jems.server.project.entity.checklist

import com.vladmihalcea.hibernate.type.json.JsonType
import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistComponentEntity
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.validation.constraints.NotNull

@Entity(name = "checklist_component_instance")
@TypeDefs(TypeDef(name = "json", typeClass = JsonType::class))
class ChecklistComponentInstanceEntity(

    @EmbeddedId
    val checklistComponentId: ChecklistComponentInstanceId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId(value = "programmeComponentId")
    @JoinColumn(name = "programme_component_id", referencedColumnName = "id")
    @field:NotNull
    var programmeChecklistComponentEntity: ProgrammeChecklistComponentEntity,

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    var metadata: String?
)
