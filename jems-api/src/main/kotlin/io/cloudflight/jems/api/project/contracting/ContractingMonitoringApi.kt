package io.cloudflight.jems.api.project.contracting

import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingMonitoringDTO
import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingMonitoringStartDateDTO
import io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate.ContractingClosureDTO
import io.cloudflight.jems.api.project.dto.contracting.lastPaymentDate.ContractingClosureUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal

@Api("Project Contracting Monitoring")
interface ContractingMonitoringApi {

    companion object {
        private const val ENDPOINT_API_CONTRACTING_MONITORING = "/api/project/{projectId}/contracting/monitoring"
    }

    @ApiOperation("Get Project Contracting Monitoring")
    @GetMapping(ENDPOINT_API_CONTRACTING_MONITORING)
    fun getContractingMonitoring(@PathVariable projectId: Long): ProjectContractingMonitoringDTO

    @ApiOperation("Update Project Contracting Monitoring")
    @PutMapping(ENDPOINT_API_CONTRACTING_MONITORING, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateContractingMonitoring(
        @PathVariable projectId: Long,
        @RequestBody contractingMonitoring: ProjectContractingMonitoringDTO
    ): ProjectContractingMonitoringDTO

    @ApiOperation("Update Project Contracting Monitoring")
    @PutMapping("$ENDPOINT_API_CONTRACTING_MONITORING/partnerPaymentDate", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateContractingPartnerPaymentDate(
        @PathVariable projectId: Long,
        @RequestBody contractingClosure: ContractingClosureUpdateDTO,
    ): ContractingClosureDTO

    @ApiOperation("Get Available Periods for Contracting Monitoring (Last Approved)")
    @GetMapping("$ENDPOINT_API_CONTRACTING_MONITORING/periods")
    fun getContractingMonitoringPeriods(@PathVariable projectId: Long): List<ProjectPeriodDTO>

    @ApiOperation("Get Project Contracting Monitoring Start Date")
    @GetMapping("$ENDPOINT_API_CONTRACTING_MONITORING/startDate")
    fun getContractingMonitoringStartDate(@PathVariable projectId: Long): ProjectContractingMonitoringStartDateDTO

    @ApiOperation("Get Contracting Monitoring Project Budget")
    @GetMapping("$ENDPOINT_API_CONTRACTING_MONITORING/projectBudget")
    fun getContractingMonitoringProjectBudget(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): BigDecimal

}
