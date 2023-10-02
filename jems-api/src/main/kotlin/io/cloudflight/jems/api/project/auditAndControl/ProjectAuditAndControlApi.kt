package io.cloudflight.jems.api.project.auditAndControl

import io.cloudflight.jems.api.project.dto.auditAndControl.AuditControlDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ProjectAuditControlUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Audit and Control")
interface ProjectAuditAndControlApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_AUDIT_CONTROL = "/api/project/{projectId}/audit"
    }

    @ApiOperation("Create new project audit/control")
    @PostMapping(ENDPOINT_API_PROJECT_AUDIT_CONTROL, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProjectAudit(
        @PathVariable projectId: Long,
        @RequestBody auditData: ProjectAuditControlUpdateDTO
    ): AuditControlDTO


    @ApiOperation("Updates the details of audit/control")
    @PutMapping("$ENDPOINT_API_PROJECT_AUDIT_CONTROL/{auditControlId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectAudit(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @RequestBody auditData: ProjectAuditControlUpdateDTO
    ): AuditControlDTO

    @ApiOperation("Retrieve all project financial audits")
    @GetMapping(ENDPOINT_API_PROJECT_AUDIT_CONTROL)
    fun listAuditsForProject(@PathVariable projectId: Long): List<AuditControlDTO>


    @ApiOperation("Retrieve project financial audit details")
    @GetMapping("$ENDPOINT_API_PROJECT_AUDIT_CONTROL/{auditControlId}")
    fun getAuditDetail(@PathVariable projectId: Long, @PathVariable auditControlId: Long): AuditControlDTO

}