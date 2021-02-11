package io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable

import javax.persistence.CascadeType
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity(name = "project_work_package_activity_deliverable")
data class WorkPackageActivityDeliverableEntity(

    @EmbeddedId
    val deliverableId: WorkPackageActivityDeliverableId,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.deliverableId", fetch = FetchType.EAGER)
    val translatedValues: Set<WorkPackageActivityDeliverableTranslationEntity> = emptySet(),

    val startPeriod: Int? = null,
)
