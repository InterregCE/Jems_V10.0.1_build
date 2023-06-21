package io.cloudflight.jems.server.project.entity.report.project.identification

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_identification_tg_transl")
class ProjectReportIdentificationTargetGroupTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProjectReportIdentificationTargetGroupEntity>,
    var description: String?,
) : TranslationEntity()
