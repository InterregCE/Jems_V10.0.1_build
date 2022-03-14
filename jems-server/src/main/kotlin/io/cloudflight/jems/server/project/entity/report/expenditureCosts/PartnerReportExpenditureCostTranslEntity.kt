package io.cloudflight.jems.server.project.entity.report.expenditureCosts

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "partner_report_expenditure_cost_transl")
class PartnerReportExpenditureCostTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<PartnerReportExpenditureCostEntity>,

    var comment: String? = null,
    var description: String? = null

) : TranslationEntity()
