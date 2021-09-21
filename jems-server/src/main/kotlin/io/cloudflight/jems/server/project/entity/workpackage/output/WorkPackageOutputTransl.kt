package io.cloudflight.jems.server.project.entity.workpackage.output

import io.cloudflight.jems.server.common.entity.TranslationEntity
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_work_package_output_transl")
class WorkPackageOutputTransl(
    @EmbeddedId
    override val translationId: WorkPackageOutputTranslationId,

    @Column
    val title: String? = null,

    @Column
    val description: String? = null
) : TranslationEntity()
