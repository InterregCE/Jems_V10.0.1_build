package io.cloudflight.jems.server.programme.entity.checklist

import com.vladmihalcea.hibernate.type.json.JsonType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "programme_checklist_component")
@TypeDefs(TypeDef(name = "json", typeClass = JsonType::class))
class ProgrammeChecklistComponentEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: ProgrammeChecklistComponentType,

    val positionOnTable: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_checklist_id")
    var checklist: ProgrammeChecklistEntity? = null,

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    var metadata: String?
)
