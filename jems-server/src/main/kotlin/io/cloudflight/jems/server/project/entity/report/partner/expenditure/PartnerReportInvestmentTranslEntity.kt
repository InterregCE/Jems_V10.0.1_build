package io.cloudflight.jems.server.project.entity.report.partner.expenditure

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_investment_transl")
class PartnerReportInvestmentTranslEntity (

    @EmbeddedId
    override val translationId: TranslationId<PartnerReportInvestmentEntity>,

    @field:NotNull
    val title: String,

): TranslationEntity()
