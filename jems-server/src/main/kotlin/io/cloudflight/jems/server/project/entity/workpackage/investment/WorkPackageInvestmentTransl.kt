package io.cloudflight.jems.server.project.entity.workpackage.investment

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_work_package_investment_transl")
data class WorkPackageInvestmentTransl(

    @EmbeddedId
    val investmentTranslation: WorkPackageInvestmentTranslation<WorkPackageInvestmentEntity>,

    val title: String? = null,

    @Column
    var justificationExplanation: String? = null,

    @Column
    var justificationTransactionalRelevance: String? = null,

    @Column
    var justificationBenefits: String? = null,

    @Column
    var justificationPilot: String? = null,

    @Column
    var risk: String? = null,

    @Column
    var documentation: String? = null,

    @Column
    var ownershipSiteLocation: String? = null,

    @Column
    var ownershipRetain: String? = null,

    @Column
    var ownershipMaintenance: String? = null
) {
    override fun equals(other: Any?) =
        this === other ||
                other !== null &&
                other is WorkPackageInvestmentTransl &&
                investmentTranslation == other.investmentTranslation

    override fun hashCode() =
        if (investmentTranslation.investment.id <= 0) super.hashCode()
        else investmentTranslation.investment.id.toInt().plus(investmentTranslation.language.translationKey.hashCode())
}