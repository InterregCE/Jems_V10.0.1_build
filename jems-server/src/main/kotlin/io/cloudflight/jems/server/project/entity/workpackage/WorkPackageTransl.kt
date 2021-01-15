package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.server.project.entity.TranslationWorkPackageId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * WorkPackage lang table
 */
@Entity(name = "project_work_package_transl")
data class WorkPackageTransl(

    @EmbeddedId
    val translationId: TranslationWorkPackageId,

    @Column
    val name: String? = null,

    @Column(name = "specific_objective")
    val specificObjective: String? = null,

    @Column(name = "objective_and_audience")
    val objectiveAndAudience: String? = null

)
