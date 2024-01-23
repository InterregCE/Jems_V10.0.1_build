package io.cloudflight.jems.server.project.repository.contracting.monitoring

import io.cloudflight.jems.server.project.entity.contracting.ContractingDimensionCodeEntity
import io.cloudflight.jems.server.project.entity.contracting.ContractingMonitoringAddDateId
import io.cloudflight.jems.server.project.entity.contracting.ContractingPartnerPaymentDateEntity
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingMonitoringAddDateEntity
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingMonitoringEntity
import io.cloudflight.jems.server.project.service.contracting.model.ContractingDimensionCode
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringPartnerLastPayment
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringAddDate

fun List<ProjectContractingMonitoringAddDateEntity>.toModels() =  map { it.toModel() }
fun ProjectContractingMonitoringAddDateEntity.toModel() = ProjectContractingMonitoringAddDate(
    projectId = addDateId.projectId,
    number = addDateId.number,
    entryIntoForceDate = entryIntoForceDate,
    comment = comment
)

fun List<ContractingDimensionCodeEntity>.toDimensionCodeModels() = map { it.toModel() }

fun List<ContractingPartnerPaymentDateEntity>.toModels() = map { it.toModel() }

fun ContractingPartnerPaymentDateEntity.toModel() = ContractingMonitoringPartnerLastPayment(
    id= id,
    partnerId = partnerId,
    projectId = projectId,
    lastPaymentDate = lastPaymentDate
)

fun ContractingDimensionCodeEntity.toModel() = ContractingDimensionCode(
    id = id,
    projectId = projectId,
    programmeObjectiveDimension = programmeObjectiveDimension,
    dimensionCode = dimensionCode,
    projectBudgetAmountShare = projectBudgetAmountShare
)
fun ProjectContractingMonitoringEntity.toModel() = ProjectContractingMonitoring(
    projectId = projectId,
    startDate = startDate,
    closureDate = closureDate,
    typologyProv94 = typologyProv94,
    typologyProv94Comment = typologyProv94Comment,
    typologyProv95 = typologyProv95,
    typologyProv95Comment = typologyProv95Comment,
    typologyStrategic = typologyStrategic,
    typologyStrategicComment = typologyStrategicComment,
    typologyPartnership = typologyPartnership,
    typologyPartnershipComment = typologyPartnershipComment,
    addDates = addDates.toModels(),
    dimensionCodes = dimensionCodes.toDimensionCodeModels(),
    partnerPaymentDates = partnerPaymentDates.toModels()
)

fun List<ProjectContractingMonitoringAddDate>.toEntities(projectId: Long) =
    mapIndexed { index, it -> it.toEntity(projectId, index.plus(1)) }

fun ProjectContractingMonitoringAddDate.toEntity(id: Long, sortNumber: Int) = ProjectContractingMonitoringAddDateEntity(
    ContractingMonitoringAddDateId(id, sortNumber),
    entryIntoForceDate = entryIntoForceDate,
    comment = comment
)

fun List<ContractingDimensionCode>.toDimensionCodeEntities(projectId: Long) =
    map{ it.toEntity(projectId)}

fun List<ContractingMonitoringPartnerLastPayment>.toEntities() =
    map{ it.toEntity() }

fun ContractingMonitoringPartnerLastPayment.toEntity() = ContractingPartnerPaymentDateEntity(
    id = id,
    projectId = projectId,
    partnerId = partnerId,
    lastPaymentDate = lastPaymentDate
)

fun ContractingDimensionCode.toEntity(projectId: Long) =
    ContractingDimensionCodeEntity(
        id = id,
        projectId = projectId,
        programmeObjectiveDimension = programmeObjectiveDimension,
        dimensionCode = dimensionCode,
        projectBudgetAmountShare = projectBudgetAmountShare
    )
fun ProjectContractingMonitoring.toEntity() = ProjectContractingMonitoringEntity(
    projectId = projectId,
    startDate = startDate,
    closureDate = closureDate,
    typologyProv94 = typologyProv94,
    typologyProv94Comment = typologyProv94Comment,
    typologyProv95 = typologyProv95,
    typologyProv95Comment = typologyProv95Comment,
    typologyStrategic = typologyStrategic,
    typologyStrategicComment = typologyStrategicComment,
    typologyPartnership = typologyPartnership,
    typologyPartnershipComment = typologyPartnershipComment,
    addDates = addDates.toEntities(projectId),
    dimensionCodes = dimensionCodes.toDimensionCodeEntities(projectId),
    partnerPaymentDates = partnerPaymentDates.toEntities()
)
