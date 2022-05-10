package io.cloudflight.jems.server.project.entity.checklist

import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "checklist_instance")
class ChecklistInstanceEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var status: ChecklistInstanceStatus,

    var finishedDate: LocalDate?,

    val relatedToId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_checklist_id")
    @field:NotNull
    val programmeChecklist: ProgrammeChecklistEntity,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @field:NotNull
    val creator: UserEntity,

    @OneToMany(mappedBy = "checklistComponentId.checklist", cascade = [CascadeType.ALL], orphanRemoval = true)
    var components: MutableSet<ChecklistComponentInstanceEntity>? = mutableSetOf(),

    @field:NotNull
    var consolidated: Boolean = false
)
