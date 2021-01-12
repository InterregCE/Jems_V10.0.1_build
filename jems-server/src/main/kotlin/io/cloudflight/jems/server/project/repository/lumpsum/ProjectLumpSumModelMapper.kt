package io.cloudflight.jems.server.project.repository.lumpsum

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectPartnerLumpSumId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import java.util.UUID
import kotlin.collections.HashSet

fun Set<ProjectLumpSumEntity>.toModel() = map {
    ProjectLumpSum(
        id = it.id,
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
    projectId: Long,
    getProgrammeLumpSum: (Long) -> ProgrammeLumpSumEntity,
    getProjectPartner: (Long) -> ProjectPartnerEntity,
): ProjectLumpSumEntity {
    val assignedId = id ?: UUID.randomUUID()
    return ProjectLumpSumEntity(
        id = assignedId,
        projectId = projectId,
        programmeLumpSum = getProgrammeLumpSum.invoke(this.programmeLumpSumId),
        endPeriod = period,
        lumpSumContributions = lumpSumContributions.toPartnerLumpSumEntity(assignedId, getProjectPartner),
    )
}

fun List<ProjectLumpSum>.toEntity(
    projectId: Long,
    getProgrammeLumpSum: (Long) -> ProgrammeLumpSumEntity,
    getProjectPartner: (Long) -> ProjectPartnerEntity,
) = mapTo(HashSet()) {
    it.toEntity(projectId, getProgrammeLumpSum, getProjectPartner)
}

fun List<ProjectPartnerLumpSum>.toPartnerLumpSumEntity(
    projectLumpSumId: UUID,
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
