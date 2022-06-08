package io.cloudflight.jems.server.project.entity.report.identification

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_partner_identification_transl")
class ProjectPartnerReportIdentificationTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerReportIdentificationEntity>,
    var summary: String?,
    var problemsAndDeviations: String?,
    var spendingDeviations: String?,
) : TranslationEntity()
