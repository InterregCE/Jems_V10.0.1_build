package io.cloudflight.jems.server.project.entity.report.project.financialOverview

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "report_project_certificate_investment_transl")
class ReportProjectCertificateInvestmentTranslEntity (

    @EmbeddedId
    override val translationId: TranslationId<ReportProjectCertificateInvestmentEntity>,

    @field:NotNull
    val title: String,

): TranslationEntity()
