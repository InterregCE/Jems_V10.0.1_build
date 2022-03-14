package io.cloudflight.jems.server.project.entity.report.expenditure

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "report_project_partner_expenditure_transl")
class PartnerReportExpenditureCostTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<PartnerReportExpenditureCostEntity>,

    var comment: String? = null,
    var description: String? = null

) : TranslationEntity()
