package io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_work_package_activity_deliverable_transl")
data class WorkPackageActivityDeliverableTranslationEntity(

    @EmbeddedId
    override val translationId: TranslationId<WorkPackageActivityDeliverableEntity>,

    var description: String? = null,

    ) : TranslationEntity()
