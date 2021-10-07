package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.budget.ProjectBudgetApi
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetPerPeriodDTO
import io.cloudflight.jems.server.project.service.budget.export.ExportBudgetInteractor
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectBudgetController(
    private val getPartnerBudgetPerPeriodInteractor: GetPartnerBudgetPerPeriodInteractor,
    private val exportBudgetInteractor: ExportBudgetInteractor
) : ProjectBudgetApi {

    override fun getProjectPartnerBudgetPerPeriod(projectId: Long, version: String?): List<ProjectPartnerBudgetPerPeriodDTO> =
        this.getPartnerBudgetPerPeriodInteractor.getPartnerBudgetPerPeriod(projectId, version).map { it.toDto() }

    override fun exportBudgetData(projectId: Long, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage, version: String?): ResponseEntity<ByteArrayResource> =
        with(exportBudgetInteractor.exportDataToCsv(projectId, exportLanguage, inputLanguage, version)) {
            ResponseEntity.ok()
                .contentLength(this.content.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.fileName}\"")
                .body(ByteArrayResource(this.content))
    }

}
