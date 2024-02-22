package io.cloudflight.jems.server.project.entity.report.project.closure

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_closure_project_prize_transl")
class ProjectReportProjectClosurePrizeTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectReportProjectClosurePrizeEntity>,

    var prize: String?

): TranslationEntity()
