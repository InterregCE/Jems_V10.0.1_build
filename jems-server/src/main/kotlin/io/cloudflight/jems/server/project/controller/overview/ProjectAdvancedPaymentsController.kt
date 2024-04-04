package io.cloudflight.jems.server.project.controller.overview

import io.cloudflight.jems.api.project.overview.ProjectAdvancedPaymentsApi
import io.cloudflight.jems.server.payments.service.toDTO
import io.cloudflight.jems.server.project.service.overview.getProjectAdvancePayments.GetProjectAdvancePaymentsInteractor
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectAdvancedPaymentsController(
    private val getAdvancePayments: GetProjectAdvancePaymentsInteractor,
): ProjectAdvancedPaymentsApi {
    override fun getAdvancePayments(
        projectId: Long,
        pageable: Pageable
    ) = getAdvancePayments.list(projectId, pageable).map { it.toDTO() }
}
