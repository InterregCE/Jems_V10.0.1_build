package io.cloudflight.jems.server.project.service.partner.get_project_partner

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.payments.authorization.CanRetrieveAdvancePayments
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartner
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartnerSummaries
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectBudgetPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerPaymentSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartner(
    private val persistence: PartnerPersistence,
    private val getProjectBudget: GetProjectBudget,
    private val institutionPersistence: ControllerInstitutionPersistence,
) : GetProjectPartnerInteractor {

    @CanRetrieveProjectForm
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnersByProjectIdException::class)
    override fun findAllByProjectId(projectId: Long, page: Pageable, version: String?): Page<ProjectBudgetPartnerSummary> {
        val partnersPage = persistence.findAllByProjectId(projectId, page, version)
        val totalPerPartner = getProjectBudget.getBudget(partnersPage.content, projectId, version)
            .associateBy({ it.partner.id!! }, { it.totalCosts })

        return partnersPage.map {
            ProjectBudgetPartnerSummary(it, totalBudget = totalPerPartner[it.id])
        }
    }

    @CanRetrieveProjectPartner
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerByIdException::class)
    override fun getById(partnerId: Long, version: String?): ProjectPartnerDetail =
        persistence.getById(partnerId, version)

    @CanRetrieveProjectPartnerSummaries
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerByProjectIdForDropdownException::class)
    override fun findAllByProjectIdForDropdown(
        projectId: Long, sort: Sort, version: String?
    ): List<ProjectPartnerSummary> {
        val partners = persistence.findAllByProjectIdForDropdown(projectId, sort, version)

        val institutionsByPartnerId = institutionPersistence
            .getControllerInstitutions(partnerIds = partners.mapNotNullTo(HashSet()) { it.id })

        return partners.fillInInstitutions(institutionsByPartnerId)
    }

    private fun List<ProjectPartnerSummary>.fillInInstitutions(institutions: Map<Long, ControllerInstitutionList>) =
        onEach { it.institutionName = institutions[it.id!!]?.name }

    @CanRetrieveAdvancePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerByProjectIdForPaymentsException::class)
    override fun findAllByProjectIdWithContributionsForDropdown(
        projectId: Long,
        version: String?
    ): List<ProjectPartnerPaymentSummary> =
        persistence.findAllByProjectIdWithContributionsForDropdown(projectId, version)

}
