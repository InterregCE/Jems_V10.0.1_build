package io.cloudflight.jems.server.project.entity.report.project.workPlan

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "report_project_wp_transl")
class ProjectReportWorkPackageTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProjectReportWorkPackageEntity>,
    @field:NotNull
    val specificObjective: String,
    @field:NotNull
    var specificExplanation: String,
    @field:NotNull
    val communicationObjective: String,
    @field:NotNull
    var communicationExplanation: String,
    @field:NotNull
    var description: String,
) : TranslationEntity()
