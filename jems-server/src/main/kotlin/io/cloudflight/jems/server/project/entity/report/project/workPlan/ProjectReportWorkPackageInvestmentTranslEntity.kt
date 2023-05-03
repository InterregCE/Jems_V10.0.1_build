package io.cloudflight.jems.server.project.entity.report.project.workPlan

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "report_project_wp_investment_transl")
class ProjectReportWorkPackageInvestmentTranslEntity(
    @EmbeddedId
    override val translationId: TranslationId<ProjectReportWorkPackageInvestmentEntity>,
    @field:NotNull
    val title: String,
    @field:NotNull
    val justificationExplanation: String,
    @field:NotNull
    val justificationTransactionalRelevance: String,
    @field:NotNull
    val justificationBenefits: String,
    @field:NotNull
    val justificationPilot: String,
    @field:NotNull
    val risk: String,
    @field:NotNull
    val documentation: String,
    @field:NotNull
    val documentationExpectedImpacts: String,
    @field:NotNull
    val ownershipSiteLocation: String,
    @field:NotNull
    val ownershipRetain: String,
    @field:NotNull
    val ownershipMaintenance: String,
    @field:NotNull
    var progress: String,
) : TranslationEntity() {
    constructor(translationId: TranslationId<ProjectReportWorkPackageInvestmentEntity>) : this(
            translationId,
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
            )
}
