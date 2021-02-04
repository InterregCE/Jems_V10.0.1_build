package io.cloudflight.jems.server.project.repository.partner.cofinancing

import io.cloudflight.jems.server.programme.entity.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.toModel
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingFundId
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerContributionEntity
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing

// region Finances

fun Collection<UpdateProjectPartnerCoFinancing>.toCoFinancingEntity(partnerId: Long, availableFunds: Map<Long, ProgrammeFundEntity>) = mapTo(HashSet()) {
    ProjectPartnerCoFinancingEntity(
        coFinancingFundId = ProjectPartnerCoFinancingFundId(partnerId, it.fundType),
        percentage = it.percentage!!,
        programmeFund = if (it.fundId != null) availableFunds[it.fundId] else null
    )
}

fun ProjectPartnerCoFinancingEntity.toModel() = ProjectPartnerCoFinancing(
    fundType = coFinancingFundId.type,
    fund = programmeFund?.toModel(),
    percentage = percentage
)

fun Collection<ProjectPartnerCoFinancingEntity>.toCoFinancingModel() = map { it.toModel() }
// endregion Finances


// region Contributions
fun Collection<ProjectPartnerContribution>.toContributionEntity(partnerId: Long) = map {
    ProjectPartnerContributionEntity(
        id = it.id ?: 0,
        partnerId = partnerId,
        name = if (it.isPartner) null else it.name,
        status = it.status,
        amount = it.amount!!
    )
}

fun ProjectPartnerContributionEntity.toModel() = ProjectPartnerContribution(
    id = id,
    name = name,
    status = status,
    amount = amount,
    isPartner = name == null
)

fun Collection<ProjectPartnerContributionEntity>.toContributionModel() = map { it.toModel() }
// endregion Contributions
