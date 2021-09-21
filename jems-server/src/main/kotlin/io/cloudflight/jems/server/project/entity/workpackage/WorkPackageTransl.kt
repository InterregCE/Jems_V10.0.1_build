package io.cloudflight.jems.server.project.entity.workpackage

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * WorkPackage lang table
 */
@Entity(name = "project_work_package_transl")
class WorkPackageTransl(

    @EmbeddedId
    override val translationId: TranslationId<WorkPackageEntity>,

    @Column
    var name: String? = null,

    @Column(name = "specific_objective")
    var specificObjective: String? = null,

    @Column(name = "objective_and_audience")
    var objectiveAndAudience: String? = null

): TranslationEntity()
