package io.cloudflight.jems.server.project.entity.report.project.resultPrinciple

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_result_transl")
class ProjectReportProjectResultTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectReportProjectResultEntity>,

    var description: String?
): TranslationEntity()

