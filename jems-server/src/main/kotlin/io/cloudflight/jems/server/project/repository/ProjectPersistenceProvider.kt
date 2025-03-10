package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.call.repository.ApplicationFormFieldConfigurationRepository
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.call.repository.ProjectCallStateAidRepository
import io.cloudflight.jems.server.call.repository.notifications.project.ProjectNotificationConfigurationRepository
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.repository.priority.ProgrammePriorityRepository
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentId
import io.cloudflight.jems.server.project.repository.assessment.ProjectAssessmentEligibilityRepository
import io.cloudflight.jems.server.project.repository.assessment.ProjectAssessmentQualityRepository
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectSearchRequest
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.toApplicantAndStatus
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel.EDIT
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel.MANAGE
import io.cloudflight.jems.server.project.entity.projectuser.ProjectCollaboratorLevel.VIEW
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.LocalDate

@Repository
class ProjectPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectRepository: ProjectRepository,
    private val projectCollaboratorRepository: UserProjectCollaboratorRepository,
    private val partnerCollaboratorRepository: UserPartnerCollaboratorRepository,
    private val projectAssessmentQualityRepository: ProjectAssessmentQualityRepository,
    private val projectAssessmentEligibilityRepository: ProjectAssessmentEligibilityRepository,
    private val projectStatusHistoryRepo: ProjectStatusHistoryRepository,
    private val userRepository: UserRepository,
    private val callRepository: CallRepository,
    private val stateAidRepository: ProjectCallStateAidRepository,
    private val applicationFormFieldConfigurationRepository: ApplicationFormFieldConfigurationRepository,
    private val programmePriorityRepository: ProgrammePriorityRepository,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val notificationConfigurationRepository: ProjectNotificationConfigurationRepository,
) : ProjectPersistence {

    @Transactional(readOnly = true)
    override fun getProject(projectId: Long, version: String?): ProjectFull {
        val project = getProjectOrThrow(projectId)

        val assessmentStep1 = ProjectAssessmentEntity(
            assessmentQuality = projectAssessmentQualityRepository.findById(project.idInStep(1)).orElse(null),
            assessmentEligibility = projectAssessmentEligibilityRepository.findById(project.idInStep(1)).orElse(null),
            eligibilityDecision = project.decisionEligibilityStep1,
            fundingDecision = project.decisionFundingStep1,
        )
        val assessmentStep2 = ProjectAssessmentEntity(
            assessmentQuality = projectAssessmentQualityRepository.findById(project.idInStep(2)).orElse(null),
            assessmentEligibility = projectAssessmentEligibilityRepository.findById(project.idInStep(2)).orElse(null),
            eligibilityDecision = project.decisionEligibilityStep2,
            preFundingDecision = project.decisionPreFundingStep2,
            fundingDecision = project.decisionFundingStep2,
        )
        val jsNotifiable = isJsNotifiable(project.call.id)

        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                project.toModel(
                    assessmentStep1 = assessmentStep1,
                    assessmentStep2 = assessmentStep2,
                    stateAidRepository.findAllByIdCallId(project.call.id),
                    applicationFormFieldConfigurationRepository.findAllByCallId(project.call.id),
                    jsNotifiable,
                )
            },
            // grouped this will be only one result
            previousVersionFetcher = { timestamp ->
                getProjectHistoricalData(projectId, timestamp, project, assessmentStep1, assessmentStep2, jsNotifiable)
            }
        ) ?: throw ApplicationVersionNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun throwIfNotExists(projectId: Long, version: String?) {
        if (projectVersionUtils.fetch(version, projectId,
                { projectRepository.existsById(projectId) },
                { projectRepository.existsByIsAsOfTimestamp(projectId) }
            ) != true
        ) throw ProjectNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun getApplicantAndStatusById(id: Long): ProjectApplicantAndStatus {
        val partnerCollaborators = partnerCollaboratorRepository.findAllByProjectId(id).mapTo(HashSet()) { it.id.userId }
        val partnerControllers = controllerInstitutionPersistence.getRelatedUserIdsForProject(projectId = id)
        val collaboratorsByLevel = projectCollaboratorRepository.findAllByIdProjectId(id)
            .groupBy { it.level }
            .mapValues { it.value.map { collaborator -> collaborator.id.userId }.toSet() }
        return projectRepository.getReferenceById(id).toApplicantAndStatus(
            collaboratorViewIds = (collaboratorsByLevel[VIEW] ?: emptySet()) union partnerCollaborators union partnerControllers,
            collaboratorEditIds = collaboratorsByLevel[EDIT] ?: emptySet(),
            collaboratorManageIds = collaboratorsByLevel[MANAGE] ?: emptySet(),
        )
    }

    @Transactional(readOnly = true)
    override fun getProjectSummary(projectId: Long): ProjectSummary =
        projectRepository.getReferenceById(projectId).toSummaryModel()

    @Transactional(readOnly = true)
    override fun getProjectCallSettings(projectId: Long): ProjectCallSettings =
        getProjectOrThrow(projectId).let {
            it.call.toSettingsModel(
                stateAidRepository.findAllByIdCallId(it.call.id),
                applicationFormFieldConfigurationRepository.findAllByCallId(it.call.id),
                jsNotifiable = isJsNotifiable(it.call.id),
            )
        }

    @Transactional(readOnly = true)
    override fun getCallIdOfProject(projectId: Long): Long =
        projectRepository.findCallIdFor(projectId).orElseThrow { ProjectNotFoundException() }

    @Transactional(readOnly = true)
    override fun getProjects(pageable: Pageable, searchRequest: ProjectSearchRequest?): Page<ProjectSummary> =
        projectRepository.findAll(
            ProjectRepository.buildSearchPredicate(searchRequest = searchRequest),
            pageable
        ).toModel()

    @Transactional(readOnly = true)
    override fun getAssignedProjects(pageable: Pageable, searchRequest: ProjectSearchRequest?): Page<ProjectSummary> =
        projectRepository.findAll(
            ProjectRepository.buildAssignedSearchPredicate(searchRequest = searchRequest),
            pageable
        ).toModel()

    @Transactional(readOnly = true)
    override fun getProjectsOfUserPlusExtra(pageable: Pageable, extraProjectIds: Collection<Long>): Page<ProjectSummary> =
        projectRepository.findAllByIdIn(
            projectIds = extraProjectIds,
            pageable = pageable,
        ).toModel()

    @Transactional(readOnly = true)
    override fun getProjectPeriods(projectId: Long, version: String?): List<ProjectPeriod>  {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                getProjectOrThrow(projectId).periods.toProjectPeriods()
            },
            previousVersionFetcher = { timestamp ->
                projectRepository.findPeriodsByProjectIdAsOfTimestamp(projectId, timestamp).toProjectPeriodHistoricalData()
            }
        ) ?: throw ApplicationVersionNotFoundException()
    }

    @Transactional
    override fun createProjectWithStatus(acronym: String, status: ApplicationStatus, userId: Long, callId: Long): ProjectDetail {
        val user = userRepository.findById(userId).orElseThrow { ResourceNotFoundException("user") }
        val projectStatus = projectStatusHistoryRepo.save(
            ProjectStatusHistoryEntity(
                status = status,
                user = user,
            )
        )

        val createdProject = projectRepository.save(
            ProjectEntity(
                acronym = acronym,
                applicant = user,
                call = callRepository.findById(callId).orElseThrow { ResourceNotFoundException("call") },
                currentStatus = projectStatus,
            )
        )
        projectStatus.project = createdProject

        return createdProject.toDetailModel(
            assessmentStep1 = null,
            assessmentStep2 = null,
            stateAidRepository.findAllByIdCallId(callId),
            applicationFormFieldConfigurationRepository.findAllByCallId(callId),
            jsNotifiable = isJsNotifiable(callId),
        )
    }

    @Transactional
    override fun updateProjectCustomIdentifier(projectId: Long, customIdentification: String) {
        getProjectOrThrow(projectId).customIdentifier = customIdentification
    }

    @Transactional
    override fun updateProjectContractedOnDates(projectId: Long, contractedOnDate: LocalDate?) {
        getProjectOrThrow(projectId).contractedOnDate = contractedOnDate
    }

    private fun getProjectOrThrow(projectId: Long) =
        projectRepository.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

    private fun getProjectHistoricalData(
        projectId: Long,
        timestamp: Timestamp,
        project: ProjectEntity,
        assessmentStep1: ProjectAssessmentEntity,
        assessmentStep2: ProjectAssessmentEntity,
        jsNotifiable: Boolean,
    ): ProjectFull {
        val periods =
            projectRepository.findPeriodsByProjectIdAsOfTimestamp(projectId, timestamp).toProjectPeriodHistoricalData()
        val projectRows = projectRepository.findByIdAsOfTimestamp(projectId, timestamp)
        return projectRows.toProjectEntryWithDetailData(
            project,
            periods,
            assessmentStep1,
            assessmentStep2,
            stateAidRepository.findAllByIdCallId(project.call.id),
            applicationFormFieldConfigurationRepository.findAllByCallId(project.call.id),
            priority = getPriority(priorityId = projectRows.firstOrNull()?.programmePriorityId),
            jsNotifiable,
        )
    }

    private fun isJsNotifiable(callId: Long) = notificationConfigurationRepository
        .findByActiveTrueAndIdCallEntityIdAndIdId(callId, NotificationType.ProjectReportVerificationDoneNotificationSent) != null

    private fun getPriority(priorityId: Long?): ProgrammePriorityEntity? =
        priorityId?.let { programmePriorityRepository.findById(it).orElse(null) }

    private fun ProjectEntity.idInStep(step: Int) = ProjectAssessmentId(this, step)

}
