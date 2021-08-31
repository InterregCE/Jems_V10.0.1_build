package io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable

import javax.persistence.CascadeType
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity(name = "project_work_package_activity_deliverable")
class WorkPackageActivityDeliverableEntity(

    @EmbeddedId
    val deliverableId: WorkPackageActivityDeliverableId,

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
