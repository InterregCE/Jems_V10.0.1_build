package io.cloudflight.jems.server.project.repository.contracting.monitoring

import io.cloudflight.jems.server.project.entity.contracting.ContractingMonitoringAddDateId
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingMonitoringAddDateEntity
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringAddDate

fun List<ProjectContractingMonitoringAddDateEntity>.toModels() =  map { it.toModel() }
fun ProjectContractingMonitoringAddDateEntity.toModel() = ProjectContractingMonitoringAddDate(
    projectId = addDateId.projectId,
    number = addDateId.number,
    entryIntoForceDate = entryIntoForceDate,
    comment = comment
)
fun ProjectContractingMonitoringEntity.toModel() = ProjectContractingMonitoring(
    projectId = projectId,
    startDate = startDate,
    endDate = endDate,
    typologyProv94 = typologyProv94,
    typologyProv94Comment = typologyProv94Comment,
    typologyProv95 = typologyProv95,
    typologyProv95Comment = typologyProv95Comment,
    typologyStrategic = typologyStrategic,
    typologyStrategicComment = typologyStrategicComment,
    typologyPartnership = typologyPartnership,
    typologyPartnershipComment = typologyPartnershipComment,
    addDates = addDates.toModels()
)

fun List<ProjectContractingMonitoringAddDate>.toEntities(projectId: Long) =
    mapIndexed { index, it -> it.toEntity(projectId, index.plus(1)) }

fun ProjectContractingMonitoringAddDate.toEntity(id: Long, sortNumber: Int) = ProjectContractingMonitoringAddDateEntity(
    ContractingMonitoringAddDateId(id, sortNumber),
    entryIntoForceDate = entryIntoForceDate,
    comment = comment
)

fun ProjectContractingMonitoring.toEntity() = ProjectContractingMonitoringEntity(
    projectId = projectId,
    startDate = startDate,
    endDate = endDate,
    typologyProv94 = typologyProv94,
    typologyProv94Comment = typologyProv94Comment,
    typologyProv95 = typologyProv95,
    typologyProv95Comment = typologyProv95Comment,
    typologyStrategic = typologyStrategic,
    typologyStrategicComment = typologyStrategicComment,
    typologyPartnership = typologyPartnership,
    typologyPartnershipComment = typologyPartnershipComment,
    addDates = addDates.toEntities(projectId)
)
