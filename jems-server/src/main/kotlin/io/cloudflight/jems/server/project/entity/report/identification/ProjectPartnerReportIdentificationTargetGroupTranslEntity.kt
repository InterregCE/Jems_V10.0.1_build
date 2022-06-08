package io.cloudflight.jems.server.project.entity.report.identification

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_partner_identification_tg_transl")
class ProjectPartnerReportIdentificationTargetGroupTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerReportIdentificationTargetGroupEntity>,
    var specification: String?,
    var description: String?,
) : TranslationEntity()
