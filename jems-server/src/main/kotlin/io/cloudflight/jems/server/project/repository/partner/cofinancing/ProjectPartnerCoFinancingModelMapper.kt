package io.cloudflight.jems.server.project.repository.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.MainFund
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerContributionRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PartnerFinancingRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.PerPartnerFinancingRow
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingEntity
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancingFundId
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerContributionEntity
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import java.util.TreeMap
import kotlin.collections.HashSet

// region Finances

fun List<UpdateProjectPartnerCoFinancing>.toCoFinancingEntity(partnerId: Long, availableFunds: Map<Long, ProgrammeFundEntity>) =
    mapIndexedTo(HashSet()) { index, coFinancing ->
        ProjectPartnerCoFinancingEntity(
            coFinancingFundId = ProjectPartnerCoFinancingFundId(partnerId, orderNr = index + 1),
            percentage = coFinancing.percentage!!,
            programmeFund = if (coFinancing.fundId != null) availableFunds[coFinancing.fundId] else null
        )
    }

fun ProjectPartnerCoFinancingEntity.toModel() = ProjectPartnerCoFinancing(
    fundType = getFundType(),
    fund = programmeFund?.toModel(),
    percentage = percentage
)

fun Collection<PartnerFinancingRow>.toProjectPartnerFinancingHistoricalData() = this.groupBy { it.orderNr }
    .toSortedMap()
    .map { groupedRows ->
        ProjectPartnerCoFinancing(
            fundType = if (groupedRows.value.firstOrNull()?.fundId == null) PartnerContribution else MainFund,
            fund = if (groupedRows.value.firstOrNull()?.fundType != null) ProgrammeFund(
                id = groupedRows.value.firstOrNull()?.fundId ?: 0,
                selected = groupedRows.value.first().selected ?: false,
                type = ProgrammeFundType.from(groupedRows.value.first().fundType!!)!!,
                abbreviation = groupedRows.value.extractField { it.abbreviation },
                description = groupedRows.value.extractField { it.description }
            ) else null,
            percentage = groupedRows.value.first().percentage
        )
    }

fun Collection<PerPartnerFinancingRow>.toPerPartnerFinancing() = groupBy { it.partnerId }
    .mapValues { it.value.groupByTo(TreeMap<Int, MutableList<PerPartnerFinancingRow>>()) { values -> values.orderNr }
        .map { groupedRows -> groupedRows.value }
        .map { value ->
            ProjectPartnerCoFinancing(
                fundType = if (value.firstOrNull()?.fundId == null) PartnerContribution else MainFund,
                fund = if (value.firstOrNull()?.fundType != null) ProgrammeFund(
                    id = value.firstOrNull()?.fundId ?: 0,
                    selected = value.first().selected ?: false,
                    type = ProgrammeFundType.from(value.first().fundType!!)!!,
                    abbreviation = value.extractField { fund -> fund.abbreviation },
                    description = value.extractField { fund -> fund.description }
                ) else null,
                percentage = value.first().percentage
            )
        }
    }

fun Collection<ProjectPartnerCoFinancingEntity>.toCoFinancingModel() =
    sortedBy { it.coFinancingFundId.orderNr }.map { it.toModel() }
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

fun ProjectPartnerEntity.extractCoFinancingAndContribution(
    finances: Collection<ProjectPartnerCoFinancingEntity>
) =
    ProjectPartnerCoFinancingAndContribution(
        finances = finances.toCoFinancingModel(),
        partnerContributions = partnerContributions.toContributionModel(),
        partnerAbbreviation = abbreviation
    )
