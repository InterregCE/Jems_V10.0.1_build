package io.cloudflight.jems.server.project.entity.report.project.workPlan

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "report_project_wp_activity_transl")
class ProjectReportWorkPackageActivityTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProjectReportWorkPackageActivityEntity>,
    @field:NotNull
    val title: String,
    @field:NotNull
    var progress: String,
) : TranslationEntity()
