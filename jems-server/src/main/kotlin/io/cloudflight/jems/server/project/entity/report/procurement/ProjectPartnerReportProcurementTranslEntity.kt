package io.cloudflight.jems.server.project.entity.report.procurement

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_partner_procurement_transl")
class ProjectPartnerReportProcurementTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerReportProcurementEntity>,
    var comment: String?,
    var contractType: String?,
) : TranslationEntity()
