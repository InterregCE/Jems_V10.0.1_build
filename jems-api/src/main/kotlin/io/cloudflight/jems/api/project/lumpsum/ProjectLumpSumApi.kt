package io.cloudflight.jems.api.project.lumpsum

import io.cloudflight.jems.api.project.dto.lumpsum.ProjectLumpSumDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal

@Api("Project Lump Sum")
interface ProjectLumpSumApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_LUMP_SUM = "/api/project/{projectId}/lumpSum"
    }

    @ApiOperation("Returns all project lump sums")
    @GetMapping(ENDPOINT_API_PROJECT_LUMP_SUM)
    fun getProjectLumpSums(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectLumpSumDTO>

    @ApiOperation("Update project partner")
    @PutMapping(ENDPOINT_API_PROJECT_LUMP_SUM, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectLumpSums(
        @PathVariable projectId: Long,
        @RequestBody lumpSums: List<ProjectLumpSumDTO>
    ): List<ProjectLumpSumDTO>

    @ApiOperation("Returns project lump sums total for partner")
    @GetMapping("$ENDPOINT_API_PROJECT_LUMP_SUM/{partnerId}")
    fun getProjectLumpSumsTotalForPartner(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @RequestParam(required = false) version: String? = null
    ): BigDecimal

}
