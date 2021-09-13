package io.cloudflight.jems.server.project.entity.workpackage.activity

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_work_package_activity_transl")
class WorkPackageActivityTranslationEntity(

    @EmbeddedId
    override val translationId: TranslationId<WorkPackageActivityEntity>,

    var title: String? = null,

    var description: String? = null,

) : TranslationEntity()
