package io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_work_package_activity_deliverable_transl")
data class WorkPackageActivityDeliverableTranslationEntity(

    @EmbeddedId
    val translationId: WorkPackageActivityDeliverableTranslationId,

    val description: String? = null,
)
