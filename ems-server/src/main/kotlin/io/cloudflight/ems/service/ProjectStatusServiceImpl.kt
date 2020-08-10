package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProjectEligibilityAssessment
import io.cloudflight.ems.api.dto.InputProjectQualityAssessment
import io.cloudflight.ems.api.dto.InputProjectStatus
import io.cloudflight.ems.api.dto.InputRevertProjectStatus
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.OutputRevertProjectStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.APPROVED
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.APPROVED_WITH_CONDITIONS
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.Companion.isFundingStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.DRAFT
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.ELIGIBLE
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.INELIGIBLE
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.NOT_APPROVED
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.RETURNED_TO_APPLICANT
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.SUBMITTED
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.entity.ProjectEligibilityAssessment
import io.cloudflight.ems.entity.ProjectQualityAssessment
import io.cloudflight.ems.entity.ProjectStatus
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.ProjectRepository
import io.cloudflight.ems.repository.ProjectStatusRepository
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.security.service.SecurityService
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ProjectStatusServiceImpl(
    private val projectRepo: ProjectRepository,
    private val projectStatusRepo: ProjectStatusRepository,
    private val userRepository: UserRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
) : ProjectStatusService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    @Transactional
    override fun setProjectStatus(projectId: Long, statusChange: InputProjectStatus): OutputProject {
        val user = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()

        var project = projectRepo.findOneById(projectId) ?: throw ResourceNotFoundException("project")
        validateDecisionDateIfFunding(statusChange, project)
        val oldStatus = project.projectStatus.status

        val projectStatus =
            projectStatusRepo.save(
                getNewStatusEntity(project, statusChange, user)
            )
        project = projectRepo.save(updateProject(project, projectStatus))

        auditService.logEvent(
            Audit.projectStatusChanged(
                currentUser = securityService.currentUser,
                projectId = project.id.toString(),
                oldStatus = oldStatus,
                newStatus = projectStatus.status
            )
        )
        return project.toOutputProject()
    }

    private fun validateDecisionDateIfFunding(statusChange: InputProjectStatus, project: Project) {
        if (isFundingStatus(statusChange.status!!)
            && statusChange.date!!.isBefore(project.eligibilityDecision!!.decisionDate)) {
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "project.funding.decision.is.before.eligibility.decision"
            )
        }
    }

    @Transactional
    override fun setQualityAssessment(
        projectId: Long,
        qualityAssessmentData: InputProjectQualityAssessment
    ): OutputProject {
        val user = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()
        val project = projectRepo.findOneById(projectId) ?: throw ResourceNotFoundException("project")

        val qualityAssessment = ProjectQualityAssessment(
            id = projectId,
            project = project,
            result = qualityAssessmentData.result!!,
            user = user,
            note = qualityAssessmentData.note
        )
        val result = projectRepo.save(project.copy(qualityAssessment = qualityAssessment)).toOutputProject()

        auditService.logEvent(
            Audit.qualityAssessmentConcluded(
                currentUser = securityService.currentUser,
                projectId = result.id.toString(),
                result = result.qualityAssessment!!.result
            )
        )
        return result
    }

    @Transactional
    override fun setEligibilityAssessment(
        projectId: Long,
        eligibilityAssessmentData: InputProjectEligibilityAssessment
    ): OutputProject {
        val user = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()
        val project = projectRepo.findOneById(projectId) ?: throw ResourceNotFoundException("project")

        val eligibilityAssessment = ProjectEligibilityAssessment(
            id = projectId,
            project = project,
            result = eligibilityAssessmentData.result!!,
            user = user,
            note = eligibilityAssessmentData.note
        )
        val result = projectRepo.save(project.copy(eligibilityAssessment = eligibilityAssessment)).toOutputProject()

        auditService.logEvent(
            Audit.eligibilityAssessmentConcluded(
                currentUser = securityService.currentUser,
                projectId = result.id.toString(),
                result = result.eligibilityAssessment!!.result
            )
        )
        return result
    }

    @Transactional(readOnly = true)
    override fun findPossibleDecisionRevertStatusOutput(projectId: Long): OutputRevertProjectStatus? {
        val pairFromTo = findPossibleDecisionRevertStatus(projectId)
        return if (pairFromTo == null) null else
            OutputRevertProjectStatus(
                from = pairFromTo.first.toOutputProjectStatus(),
                to = pairFromTo.second.toOutputProjectStatus()
            )
    }

    private fun findPossibleDecisionRevertStatus(projectId: Long): Pair<ProjectStatus, ProjectStatus>? {
        val statuses = projectStatusRepo.findTop2ByProjectIdOrderByUpdatedDesc(projectId)
        if (statuses.size == 2 && possibleRevertTransitions.contains(Pair(statuses[0].status, statuses[1].status)))
            return Pair(statuses[0], statuses[1])

        log.warn("Recheck for decision-revert done for project(id=$projectId). Result: not possible due to conditions.")
        return null
    }

    @Transactional
    override fun revertLastDecision(projectId: Long, request: InputRevertProjectStatus): OutputProject {
        val possibleReversion = findPossibleDecisionRevertStatus(projectId) ?: throw revertDecisionNotPossible()

        val statusToBeRevoked = possibleReversion.first
        val statusToBeReestablished = possibleReversion.second

        validateDecisionReversion(from = statusToBeRevoked, to = statusToBeReestablished, request = request)
        val projectWithNewStatus = projectRepo.findOneById(projectId)?.copy(projectStatus = statusToBeReestablished)
            ?: throw ResourceNotFoundException("project")

        val project = projectRepo.save(
            when (statusToBeReestablished.status) {
                SUBMITTED -> projectWithNewStatus.copy(eligibilityDecision = null)
                ELIGIBLE -> projectWithNewStatus.copy(fundingDecision = null)
                APPROVED_WITH_CONDITIONS -> projectWithNewStatus.copy(fundingDecision = statusToBeReestablished)
                else -> throw UnsupportedOperationException()
            }
        )
        projectStatusRepo.delete(statusToBeRevoked)

        log.warn("Decision-reversion has been done for project(id=$projectId) status moved from ${statusToBeRevoked.status} to ${statusToBeReestablished.status}")
        auditService.logEvent(
            Audit.projectStatusChanged(
                currentUser = securityService.currentUser,
                projectId = projectId.toString(),
                oldStatus = statusToBeRevoked.status,
                newStatus = statusToBeReestablished.status
            )
        )
        return project.toOutputProject()
    }

    private fun validateDecisionReversion(from: ProjectStatus, to: ProjectStatus, request: InputRevertProjectStatus) {
        if (request.projectStatusFromId != from.id || request.projectStatusToId != to.id) {
            log.error("Decision-revert attempt for status has been done (possible from ${from.id} to ${to.id}), but wrong status IDs $request has been provided in request!")
            throw revertDecisionNotPossible()
        }
    }

    private fun revertDecisionNotPossible(): I18nValidationException {
        return I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = "project.decision.revert.not.possible"
        )
    }

    private fun updateProject(oldProject: Project, newStatus: ProjectStatus): Project {
        val oldStatus = oldProject.projectStatus.status
        return when {
            oldStatus == RETURNED_TO_APPLICANT -> {
                oldProject.copy(projectStatus = newStatus, lastResubmission = newStatus)
            }
            newStatus.status == ELIGIBLE || newStatus.status == INELIGIBLE -> {
                oldProject.copy(projectStatus = newStatus, eligibilityDecision = newStatus)
            }
            newStatus.status == SUBMITTED -> {
                oldProject.copy(projectStatus = newStatus, firstSubmission = newStatus)
            }
            isFirstFunding(oldStatus, newStatus.status) -> {
                oldProject.copy(projectStatus = newStatus, fundingDecision = newStatus)
            }
            else -> {
                oldProject.copy(projectStatus = newStatus)
            }
        }
    }

    private fun isFirstFunding(oldStatus: ProjectApplicationStatus, newStatus: ProjectApplicationStatus): Boolean {
        val oldPossibilities = setOf(ELIGIBLE)
        val newPossibilities = setOf(APPROVED, APPROVED_WITH_CONDITIONS, NOT_APPROVED)
        return oldPossibilities.contains(oldStatus) && newPossibilities.contains(newStatus) && oldStatus != newStatus
    }

    /**
     * Will create a new ProjectStatus entity to be linked to updated Project.
     *
     * In case of resubmission it will retrieve old status (before RETURNED_TO_APPLICANT) from history.
     */
    private fun getNewStatusEntity(
        project: Project,
        statusChange: InputProjectStatus,
        user: User
    ): ProjectStatus {
        var newStatus = statusChange.status!!
        val oldStatus = project.projectStatus.status

        // perform auto-fill with previous state
        if (oldStatus == RETURNED_TO_APPLICANT && newStatus == SUBMITTED) {
            newStatus = projectStatusRepo
                .findFirstByProjectIdAndStatusNotInOrderByUpdatedDesc(
                    projectId = project.id!!,
                    ignoreStatuses = setOf(RETURNED_TO_APPLICANT, DRAFT)
                )?.status ?: throw ResourceNotFoundException("project_status")
        }
        var decisionDate: LocalDate? = null
        if (decisionDateRequired(oldStatus, newStatus))
            decisionDate = statusChange.date
                ?: throw I18nValidationException(
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                    i18nKey = "project.decision.date.unknown"
                )

        return ProjectStatus(
            project = project,
            status = newStatus,
            user = user,
            decisionDate = decisionDate,
            note = statusChange.note
        )
    }

    private fun decisionDateRequired(
        oldStatus: ProjectApplicationStatus,
        newStatus: ProjectApplicationStatus
    ): Boolean {
        if (newStatus == RETURNED_TO_APPLICANT)
            return false
        if (oldStatus == RETURNED_TO_APPLICANT || oldStatus == DRAFT)
            return false
        return true
    }

}
