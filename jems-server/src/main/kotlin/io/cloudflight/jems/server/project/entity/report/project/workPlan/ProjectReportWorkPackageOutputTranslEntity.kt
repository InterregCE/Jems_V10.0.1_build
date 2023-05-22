package io.cloudflight.jems.server.project.entity.report.project.workPlan

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "report_project_wp_output_transl")
class ProjectReportWorkPackageOutputTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProjectReportWorkPackageOutputEntity>,
    @field:NotNull
    val title: String,
    @field:NotNull
    var progress: String,
    @field:NotNull
    var previousProgress: String,
) : TranslationEntity()
