package io.cloudflight.jems.server.project.entity.report.project.closure

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "report_project_closure_prize_transl")
class ProjectReportClosurePrizeTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectReportClosurePrizeEntity>,

    @field:NotNull
    var prize: String

): TranslationEntity()
