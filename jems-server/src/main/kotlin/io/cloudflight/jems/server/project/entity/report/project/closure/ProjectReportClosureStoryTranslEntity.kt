package io.cloudflight.jems.server.project.entity.report.project.closure

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "report_project_closure_story_transl")
class ProjectReportClosureStoryTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectReportEntity>,

    @Column
    @field:NotNull
    var story: String,

): TranslationEntity()
