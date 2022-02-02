package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.ProjectExportApi
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.project.service.export.export_application_form.ExportApplicationFormInteractor
import io.cloudflight.jems.server.project.service.export.export_budget.ExportBudgetInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectExportController(
    private val exportBudget: ExportBudgetInteractor,
    private val exportApplicationForm: ExportApplicationFormInteractor
) : ProjectExportApi {

    override fun exportBudget(
        projectId: Long, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage, version: String?
    ): ResponseEntity<ByteArrayResource> =
        exportBudget.exportDataToCsv(projectId, exportLanguage, inputLanguage, version).toResponseEntity()

    override fun exportApplicationForm(
        projectId: Long, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage, version: String?
    ): ResponseEntity<ByteArrayResource> =
        exportApplicationForm.export(projectId, exportLanguage, inputLanguage, version).toResponseEntity()
}
