package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.server.project.entity.TranslationWorkPackageOutputId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_work_package_output_transl")
data class WorkPackageOutputTransl(
    @EmbeddedId
    val translationId: TranslationWorkPackageOutputId,

    @Column
    val title: String? = null,

    @Column
    val description: String? = null
)
