package io.cloudflight.jems.server.project.entity.report.project.workPlan

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "report_project_wp_activity_deliverable_transl")
class ProjectReportWorkPackageActivityDeliverableTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProjectReportWorkPackageActivityDeliverableEntity>,
    @field:NotNull
    val title: String,
    @field:NotNull
    var progress: String,
) : TranslationEntity()
