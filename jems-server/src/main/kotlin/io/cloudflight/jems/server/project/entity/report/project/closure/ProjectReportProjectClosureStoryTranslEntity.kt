package io.cloudflight.jems.server.project.entity.report.project.closure

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_closure_project_story_transl")
class ProjectReportProjectClosureStoryTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectReportProjectClosureStoryEntity>,

    @Column
    var story: String?,

): TranslationEntity()
