package io.cloudflight.jems.server.payments.service.advance.getAdvancePayments

import io.cloudflight.jems.server.payments.authorization.CanRetrieveAdvancePayments
import io.cloudflight.jems.server.payments.model.advance.AdvancePayment
import io.cloudflight.jems.server.payments.model.advance.AdvancePaymentSearchRequest
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAdvancePayments(
    private val paymentPersistence: PaymentAdvancePersistence,
    private val projectVersionPersistence: ProjectVersionPersistence
): GetAdvancePaymentsInteractor {

    @CanRetrieveAdvancePayments
    @Transactional(readOnly = true)
    override fun list(pageable: Pageable, filters: AdvancePaymentSearchRequest): Page<AdvancePayment> {
        val advancePayments = paymentPersistence.list(pageable, filters)
        val latestApprovedVersionsPerProject = projectVersionPersistence
            .getAllVersionsByProjectIdIn(projectIds = advancePayments.projectIds())
            .getLatestApprovedPerProject()

        advancePayments.fillInLatestApprovedVersionPerProject(latestApprovedVersionsPerProject)
        return advancePayments
    }

    private fun Iterable<AdvancePayment>.fillInLatestApprovedVersionPerProject(latestApprovedVersionsPerProject: Map<Long, String?>) = onEach {
        it.lastApprovedProjectVersion = latestApprovedVersionsPerProject[it.projectId]
    }

    private fun Iterable<AdvancePayment>.projectIds() = mapTo(HashSet()) { it.projectId }

    private fun List<ProjectVersion>.getLatestApprovedPerProject() = groupBy { it.projectId }
        .mapValues { (_, versions) ->
            versions.filter { it.status.isApproved() }
                .sortedByDescending { it.createdAt }
                .firstOrNull()
                ?.version
        }
}
