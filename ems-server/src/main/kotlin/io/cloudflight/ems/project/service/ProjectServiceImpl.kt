package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.api.project.dto.InputProject
import io.cloudflight.ems.api.project.dto.OutputProject
import io.cloudflight.ems.api.project.dto.OutputProjectSimple
import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.ems.api.project.dto.InputProjectData
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.call.entity.Call
import io.cloudflight.ems.project.entity.ProjectStatus
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.call.repository.CallRepository
import io.cloudflight.ems.project.repository.ProjectRepository
import io.cloudflight.ems.project.repository.ProjectStatusRepository
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.security.APPLICANT_USER
import io.cloudflight.ems.security.PROGRAMME_USER
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.AuditService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class ProjectServiceImpl(
    private val projectRepo: ProjectRepository,
    private val projectStatusRepo: ProjectStatusRepository,
    private val callRepository: CallRepository,
    private val userRepository: UserRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
) : ProjectService {

    @Transactional(readOnly = true)
    override fun getById(id: Long): OutputProject {
        return projectRepo.findOneById(id)?.toOutputProject()
            ?: throw ResourceNotFoundException("project")
    }

    @Transactional(readOnly = true)
    override fun findAll(page: Pageable): Page<OutputProjectSimple> {
        val currentUser = securityService.currentUser!!
        if (currentUser.hasRole(ADMINISTRATOR)) {
            return projectRepo.findAll(page).map { it.toOutputProjectSimple() }
        }
        if (currentUser.hasRole(PROGRAMME_USER)) {
            return projectRepo.findAllByProjectStatusStatusNot(ProjectApplicationStatus.DRAFT, page)
                .map { it.toOutputProjectSimple() }
        }
        if (currentUser.hasRole(APPLICANT_USER)) {
            return projectRepo.findAllByApplicantId(currentUser.user.id!!, page).map { it.toOutputProjectSimple() }
        }
        return projectRepo.findAll(page).map { it.toOutputProjectSimple() }
    }

    @Transactional
    override fun createProject(project: InputProject): OutputProject {
        val applicant = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()

        val call = getCallIfOpen(project.projectCallId!!)

        val projectStatus = projectStatusRepo.save(projectStatusDraft(applicant))

        val createdProject = projectRepo.save(project.toEntity(
            call,
            applicant,
            projectStatus,
            projectPartners = emptySet()))
        projectStatusRepo.save(projectStatus.copy(project = createdProject))
        auditService.logEvent(
            Audit.projectStatusChanged(
                currentUser = securityService.currentUser,
                projectId = createdProject.id.toString(),
                newStatus = createdProject.projectStatus.status
            )
        )

        return createdProject.toOutputProject()
    }

    fun projectStatusDraft(user: User): ProjectStatus {
        return ProjectStatus(
            status = ProjectApplicationStatus.DRAFT,
            user = user,
            updated = ZonedDateTime.now()
        )
    }

    private fun getCallIfOpen(callId: Long): Call {
        val call = callRepository.findById(callId)
            .orElseThrow { ResourceNotFoundException("call") }
        if (call.status == CallStatus.PUBLISHED
            && ZonedDateTime.now().isBefore(call.endDate)
            && ZonedDateTime.now().isAfter(call.startDate)
        )
            return call

        auditService.logEvent(
            Audit.callAlreadyEnded(
                currentUser = securityService.currentUser!!,
                callId = call.id.toString()
            )
        )

        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nKey = "call.not.open"
        )
    }

    @Transactional
    override fun update(id: Long, projectData: InputProjectData): OutputProject {
        val project = projectRepo.findById(id).orElseThrow { ResourceNotFoundException("project") }
        return projectRepo.save(
            project.copy(
                acronym = projectData.acronym!!,
                projectData = projectData.toEntity(project)
            )
        ).toOutputProject()
    }

}
