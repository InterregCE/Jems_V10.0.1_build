package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.OutputProjectSimple
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.ProjectStatus
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.ProjectRepository
import io.cloudflight.ems.repository.ProjectStatusRepository
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.security.APPLICANT_USER
import io.cloudflight.ems.security.PROGRAMME_USER
import io.cloudflight.ems.security.service.SecurityService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class ProjectServiceImpl(
    private val projectRepo: ProjectRepository,
    private val projectStatusRepo: ProjectStatusRepository,
    private val userRepository: UserRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
) : ProjectService {

    @Transactional(readOnly = true)
    override fun getById(id: Long): OutputProject {
        return projectRepo.findOneById(id)?.toOutputProject()
            ?: throw ResourceNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun findAll(page: Pageable): Page<OutputProjectSimple> {
        val currentUser = securityService.currentUser!!
        if (currentUser.hasRole(ADMINISTRATOR)) {
            return projectRepo.findAll(page).map { it.toOutputProjectSimple() }
        }
        if (currentUser.hasRole(PROGRAMME_USER)) {
            return projectRepo.findAllWithStatuses(
                listOf(
                    ProjectApplicationStatus.SUBMITTED,
                    ProjectApplicationStatus.RETURNED_TO_APPLICANT
                ), page
            ).map { it.toOutputProjectSimple() }
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

        val projectStatus = projectStatusRepo.save(projectStatusDraft(applicant))

        val createdProject = projectRepo.save(project.toEntity(applicant, projectStatus))
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

}
