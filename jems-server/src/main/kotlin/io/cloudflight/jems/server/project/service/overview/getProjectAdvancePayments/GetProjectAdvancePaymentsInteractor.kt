package io.cloudflight.jems.server.project.service.overview.getProjectAdvancePayments

import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetProjectAdvancePaymentsInteractor {
    fun list(projectId: Long, pageable: Pageable): Page<AdvancePayment>
}
