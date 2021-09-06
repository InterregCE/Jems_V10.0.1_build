package io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable

import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_work_package_activity_deliverable")
class WorkPackageActivityDeliverableEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val deliverableId: Long = 0,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    @field:NotNull
    val activity: WorkPackageActivityEntity,

    @Column(name = "deliverable_number")
    @field:NotNull
    val deliverableNumber: Int,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<WorkPackageActivityDeliverableTranslationEntity> = mutableSetOf(),

    val startPeriod: Int? = null,
) {
    override fun equals(other: Any?) =
        this === other ||
        (other is WorkPackageActivityDeliverableEntity)
        && deliverableId == other.deliverableId

    override fun hashCode() = deliverableId.hashCode()
}
