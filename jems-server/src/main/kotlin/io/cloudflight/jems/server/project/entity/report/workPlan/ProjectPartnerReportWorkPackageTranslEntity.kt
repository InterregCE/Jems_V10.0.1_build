package io.cloudflight.jems.server.project.entity.report.workPlan

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_partner_wp_transl")
class ProjectPartnerReportWorkPackageTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerReportWorkPackageEntity>,
    var description: String?,
) : TranslationEntity()
