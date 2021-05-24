package io.cloudflight.jems.server.project.repository.partner.cofinancing

import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerContributionRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerFinancingRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingFundId
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerContributionEntity
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
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

fun Collection<PartnerFinancingRow>.toProjectPartnerFinancingHistoricalData() = this.groupBy { it.type }.map { groupedRows -> ProjectPartnerCoFinancing(
    fundType = groupedRows.value.first().type,
    fund = ProgrammeFund(
        id = groupedRows.value.firstOrNull()?.fundId ?: 0,
        selected = groupedRows.value.first().selected ?: false,
        type = (if (groupedRows.value.firstOrNull()?.fundType != null) ProgrammeFundType.from(groupedRows.value.first().fundType!!) else ProgrammeFundType.OTHER)!!,
        abbreviation = groupedRows.value.extractField { it.abbreviation },
        description = groupedRows.value.extractField { it.description }
    ),
    percentage = groupedRows.value.first().percentage
)}

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

fun PartnerContributionRow.toModel() = ProjectPartnerContribution(
    id = id,
    name = name,
    status = status,
    amount = amount,
    isPartner = name == null
)

fun Collection<ProjectPartnerContributionEntity>.toContributionModel() = map { it.toModel() }

fun Collection<PartnerContributionRow>.toProjectPartnerContributionHistoricalData() = map { it.toModel() }
// endregion Contributions

fun ProjectPartnerEntity.extractCoFinancingAndContribution() =
    ProjectPartnerCoFinancingAndContribution(
        finances = financing.toCoFinancingModel(),
        partnerContributions = partnerContributions.toContributionModel(),
        partnerAbbreviation = abbreviation
    )
