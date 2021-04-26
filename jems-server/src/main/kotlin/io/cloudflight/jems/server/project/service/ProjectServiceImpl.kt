package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputProject
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.model.ADMINISTRATOR
import io.cloudflight.jems.server.authentication.model.APPLICANT_USER
import io.cloudflight.jems.server.authentication.model.PROGRAMME_USER
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.project.dto.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectStatusHistoryRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.repository.UserRepository
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.ceil
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectServiceImpl(
    private val projectRepo: ProjectRepository,
    private val projectStatusHistoryRepo: ProjectStatusHistoryRepository,
    private val callRepository: CallRepository,
    private val userRepository: UserRepository,
    private val auditService: AuditService,
    private val auditPublisher: ApplicationEventPublisher,
    private val securityService: SecurityService
) : ProjectService {

    @Transactional(readOnly = true)
    override fun getById(id: Long): ProjectDetailDTO {
        return projectRepo.findById(id).map { it.toOutputProject() }
            .orElseThrow { ResourceNotFoundException("project") }
    }

    @Transactional(readOnly = true)
    override fun getApplicantAndStatusById(id: Long): ProjectApplicantAndStatus {
        return projectRepo.findById(id).map { it.toApplicantAndStatus() }
            .orElseThrow { ResourceNotFoundException("project") }
    }

    @Transactional(readOnly = true)
    override fun findAll(page: Pageable): Page<OutputProjectSimple> {
        val currentUser = securityService.currentUser!!
        if (currentUser.hasRole(ADMINISTRATOR)) {
            return projectRepo.findAll(page).map { it.toOutputProjectSimple() }
        }
        if (currentUser.hasRole(PROGRAMME_USER)) {
            return projectRepo.findAllByCurrentStatusStatusNot(ApplicationStatus.DRAFT, page)
                .map { it.toOutputProjectSimple() }
        }
        if (currentUser.hasRole(APPLICANT_USER)) {
            return projectRepo.findAllByApplicantId(currentUser.user.id!!, page).map { it.toOutputProjectSimple() }
        }
        return projectRepo.findAll(page).map { it.toOutputProjectSimple() }
    }


    @Transactional
    override fun createProject(project: InputProject): ProjectDetailDTO {
        val applicant = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()

        val call = getCallIfOpen(project.projectCallId!!)
        val step2Active = call.endDateStep1 != null

        val projectStatus = projectStatusHistoryRepo.save(projectStatusDraft(applicant, step2Active))

        val createdProject = projectRepo.save(
            project.toEntity(
                call,
                applicant,
                projectStatus
            )
        )
        projectStatusHistoryRepo.save(projectStatus.copy(project = createdProject))

        projectApplicationCreated(createdProject.id, createdProject.acronym, createdProject.currentStatus.status).logWith(auditService)
        auditPublisher.publishEvent(projectVersionRecorded(this,
            projectSummary = ProjectSummary(createdProject.id,createdProject.acronym,createdProject.currentStatus.status),
            userEmail = applicant.email,
            version = 1,
            createdAt = ZonedDateTime.now(ZoneOffset.UTC)
        ))

        return createdProject.toOutputProject()
    }

    fun projectStatusDraft(user: UserEntity): ProjectStatusHistoryEntity {
        return if (step2Active) {
            ProjectStatusHistoryEntity(
                status =  ApplicationStatus.STEP1_DRAFT,
                user = user,
                updated = ZonedDateTime.now()
            )
        } else {
            ProjectStatusHistoryEntity(
                status =  ApplicationStatus.DRAFT,
                user = user,
                updated = ZonedDateTime.now()
            )
        }
    }

    private fun getCallIfOpen(callId: Long): CallEntity {
        val call = callRepository.findById(callId)
            .orElseThrow { ResourceNotFoundException("call") }
        if (call.status == CallStatus.PUBLISHED
            && ZonedDateTime.now().isBefore(call.endDate)
            && ZonedDateTime.now().isAfter(call.startDate)
        )
            return call

        auditService.logEvent(callAlreadyEnded(callId = callId))

        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = "call.not.open"
        )
    }

    @Transactional
    override fun update(id: Long, projectData: InputProjectData): ProjectDetailDTO {
        val project = projectRepo.findById(id).orElseThrow { ResourceNotFoundException("project") }
        val periods =
            if (project.projectData?.duration == projectData.duration) project.periods
            else calculatePeriods(id, project.call.lengthOfPeriod, projectData.duration)

        return projectRepo.save(
            project.copy(
                acronym = projectData.acronym!!,
                projectData = projectData.toEntity(project.id),
                priorityPolicy = policyToEntity(projectData.specificObjective, project.call.prioritySpecificObjectives),
                periods = periods
            )
        ).toOutputProject()
    }

    /**
     * Calculate all necessary project periods with the given periodLength and duration.
     */
    private fun calculatePeriods(
        projectId: Long,
        periodLength: Int,
        duration: Int?
    ): List<ProjectPeriodEntity> {
        if (duration == null || duration < 1)
            return emptyList()

        val count = ceil(duration.toDouble() / periodLength).toInt()

        return (1 .. count).mapIndexed { index, period ->
            ProjectPeriodEntity(
                id = ProjectPeriodId(projectId = projectId, number = period),
                start = periodLength * index + 1,
                end = if (period == count) duration else periodLength * period
            )
        }
    }

    /**
     * Take policy only if available for this particular Call.
     */
    private fun policyToEntity(
            policy: ProgrammeObjectivePolicy?,
            availablePoliciesForCall: Set<ProgrammeSpecificObjectiveEntity>
    ): ProgrammeSpecificObjectiveEntity? {
        if (policy == null)
            return null
        return availablePoliciesForCall
            .find { it.programmeObjectivePolicy == policy }
            ?: throw ResourceNotFoundException("programmeSpecificObjective")
    }

}
