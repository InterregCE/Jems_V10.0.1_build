package io.cloudflight.jems.api.project.budget

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetPerPeriodDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Budget")
@RequestMapping("/api/project/{projectId}/budget/")
interface ProjectBudgetApi {
    @ApiOperation("Get project partner budget per period")
    @GetMapping("/perPeriod")
    fun getProjectPartnerBudgetPerPeriod(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectPartnerBudgetPerPeriodDTO>

    @ApiOperation("Export budget data to csv file")
    @GetMapping("export")
    fun exportBudgetData(@PathVariable projectId: Long,
                         @RequestParam exportLanguage: SystemLanguage,
                         @RequestParam inputLanguage: SystemLanguage,
                         @RequestParam(required = false) version: String? = null): ResponseEntity<ByteArrayResource>

}
