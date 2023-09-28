package io.cloudflight.jems.server.project.service.report.partner.base.getMyProjectPartnerReports

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.authorization.CanViewMyPartnerReports
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.base.removeEligibleAfterControlFromNotInControlOnes
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyProjectPartnerReports(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val projectCollaboratorPersistence: UserProjectCollaboratorPersistence,
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val securityService: SecurityService
) : GetMyProjectPartnerReportsInteractor {

    @CanViewMyPartnerReports
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetMyProjectPartnerReportsException::class)
    override fun findAllOfMine(pageable: Pageable): Page<ProjectPartnerReportSummary> {
        val partnerIds = getAllPartnerIdsByUserId(securityService.getUserIdOrThrow())
        return reportPersistence.listPartnerReports(partnerIds, ReportStatus.FINANCIALLY_CLOSED_STATUSES, pageable)
            .removeEligibleAfterControlFromNotInControlOnes()
    }

    private fun getAllPartnerIdsByUserId(userId: Long): Set<Long> {
        val partnerIdsFromCollaborators = partnerCollaboratorPersistence.findPartnersByUser(userId)
        val partnerIdsFromControllers = controllerInstitutionPersistence.getRelatedProjectAndPartnerIdsForUser(userId)
            .values.flatten().toSet()
        val assignedProjectIds = securityService.currentUser?.user?.assignedProjects ?: emptySet()
        val projectCollaboratorProjectIds = projectCollaboratorPersistence.getProjectIdsForUser(userId)
        return partnerIdsFromCollaborators union partnerIdsFromControllers union
            partnerPersistence.getPartnerIdsByProjectIds(assignedProjectIds union projectCollaboratorProjectIds)
    }
}
