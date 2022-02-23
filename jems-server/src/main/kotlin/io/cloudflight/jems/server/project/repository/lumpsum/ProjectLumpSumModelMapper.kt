package io.cloudflight.jems.server.project.repository.lumpsum

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumId
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumRow
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import kotlin.collections.HashSet

fun List<ProjectLumpSumEntity>.toModel() = sortedBy { it.id.orderNr }.map {
    ProjectLumpSum(
        period = it.endPeriod,
        programmeLumpSumId = it.programmeLumpSum.id,
        lumpSumContributions = it.lumpSumContributions.toModel(),
    )
}

fun Iterable<ProjectPartnerLumpSumEntity>.toModel() = sortedBy { it.id.projectPartner.sortNumber }
    .map {
        ProjectPartnerLumpSum(
            partnerId = it.id.projectPartner.id,
            amount = it.amount,
        )
    }

fun ProjectLumpSum.toEntity(
    projectLumpSumId: ProjectLumpSumId,
    getProgrammeLumpSum: (Long) -> ProgrammeLumpSumEntity,
    getProjectPartner: (Long) -> ProjectPartnerEntity,
): ProjectLumpSumEntity {
    return ProjectLumpSumEntity(
        id = projectLumpSumId,
        programmeLumpSum = getProgrammeLumpSum.invoke(this.programmeLumpSumId),
        endPeriod = period,
        lumpSumContributions = lumpSumContributions.toPartnerLumpSumEntity(projectLumpSumId, getProjectPartner),
    )
}

fun List<ProjectLumpSum>.toEntity(
    projectId: Long,
    getProgrammeLumpSum: (Long) -> ProgrammeLumpSumEntity,
    getProjectPartner: (Long) -> ProjectPartnerEntity,
) = mapIndexed { index, model ->
    model.toEntity(ProjectLumpSumId(projectId, index.plus(1)), getProgrammeLumpSum, getProjectPartner)
}

fun List<ProjectPartnerLumpSum>.toPartnerLumpSumEntity(
    projectLumpSumId: ProjectLumpSumId,
    getProjectPartner: (Long) -> ProjectPartnerEntity
) = mapTo(HashSet()) {
    ProjectPartnerLumpSumEntity(
        id = ProjectPartnerLumpSumId(
            projectLumpSumId = projectLumpSumId,
            projectPartner = getProjectPartner.invoke(it.partnerId),
        ),
        amount = it.amount,
    )
}

fun List<ProjectLumpSumRow>.toProjectLumpSumHistoricalData() =
    this.groupBy { it.orderNr }.map { groupedRows ->
        ProjectLumpSum(
            programmeLumpSumId = groupedRows.value.first().programmeLumpSumId,
            period = groupedRows.value.first().endPeriod,
            lumpSumContributions = groupedRows.value
                .filter { it.projectPartnerId != null }
                .map {
                    ProjectPartnerLumpSum(
                        partnerId = it.projectPartnerId!!,
                        amount = it.amount
                    )
                }.toList()
        )
    }
