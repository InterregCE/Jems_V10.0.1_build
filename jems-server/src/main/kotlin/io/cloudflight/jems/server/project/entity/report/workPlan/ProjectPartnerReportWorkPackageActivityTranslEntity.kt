package io.cloudflight.jems.server.project.entity.report.workPlan

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_partner_wp_activity_transl")
class ProjectPartnerReportWorkPackageActivityTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerReportWorkPackageActivityEntity>,
    val title: String?,
    var description: String?,
) : TranslationEntity()
