package io.cloudflight.jems.api.project.budget

import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerFundsPerPeriodDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Funds")
interface ProjectFundsApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_FUNDS = "/api/project/{projectId}/funds"
    }

    @ApiOperation("Get project partner funds per period")
    @GetMapping("$ENDPOINT_API_PROJECT_FUNDS/perPeriod")
    fun getProjectPartnerFundsPerPeriod(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectPartnerFundsPerPeriodDTO>
}
