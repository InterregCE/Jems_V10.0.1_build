package io.cloudflight.jems.server.project.controller.contracting.monitoring

import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingMonitoringDTO
import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingMonitoringStartDateDTO
import io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate.ContractingClosureDTO
import io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate.ContractingClosureUpdateDTO
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringStartDate
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosure
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureLastPaymentDateUpdate
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureUpdate
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val contractingMonitoringMapper = Mappers.getMapper(ContractingMonitoringDTOMapper::class.java)

fun ProjectContractingMonitoring.toDTO() = contractingMonitoringMapper.map(this)

fun ProjectContractingMonitoringDTO.toModel() = contractingMonitoringMapper.map(this)

fun List<ProjectPeriod>.toDTO() = map { contractingMonitoringMapper.map(it) }

fun ProjectContractingMonitoringStartDate.toDTO() = ProjectContractingMonitoringStartDateDTO(startDate)

fun ContractingClosure.toDto() = contractingMonitoringMapper.map(this)
fun ContractingClosureUpdateDTO.toModel() = ContractingClosureUpdate(
    closureDate = closureDate,
    lastPaymentDates = lastPaymentDates.filter { it.lastPaymentDate != null }
        .map { ContractingClosureLastPaymentDateUpdate(it.partnerId, it.lastPaymentDate!!) }
)

@Mapper
interface ContractingMonitoringDTOMapper {

    fun map(model: ProjectContractingMonitoring): ProjectContractingMonitoringDTO

    fun map(dto: ProjectContractingMonitoringDTO): ProjectContractingMonitoring

    fun map(model: ProjectPeriod): ProjectPeriodDTO

    fun map(model: ContractingClosure): ContractingClosureDTO

}
