package io.cloudflight.jems.server.project.entity.workpackage.output

import io.cloudflight.jems.server.common.entity.TranslationEntity
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_work_package_output_transl")
class WorkPackageOutputTranslEntity(

    @EmbeddedId
    override val translationId: WorkPackageOutputTranslationId,

    var title: String? = null,
    var description: String? = null,

) : TranslationEntity()
