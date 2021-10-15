package io.cloudflight.jems.api.project.budget

import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerFundsPerPeriodDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Funds")
@RequestMapping("/api/project/{projectId}/funds/")
interface ProjectFundsApi {
    @ApiOperation("Get project partner funds per period")
    @GetMapping("/perPeriod")
    fun getProjectPartnerFundsPerPeriod(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectPartnerFundsPerPeriodDTO>
}
