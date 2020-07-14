package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProjectEligibilityAssessment
import io.cloudflight.ems.api.dto.InputProjectQualityAssessment
import io.cloudflight.ems.api.dto.InputProjectStatus
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.entity.ProjectEligibilityAssessment
import io.cloudflight.ems.entity.ProjectQualityAssessment
import io.cloudflight.ems.entity.ProjectStatus
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.repository.ProjectRepository
import io.cloudflight.ems.repository.ProjectStatusRepository
import io.cloudflight.ems.security.service.SecurityService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class ProjectStatusServiceImpl(
    private val projectRepo: ProjectRepository,
    private val projectStatusRepo: ProjectStatusRepository,
    private val userRepository: UserRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
) : ProjectStatusService {

    @Transactional
    override fun setProjectStatus(projectId: Long, statusChange: InputProjectStatus): OutputProject {
        val user = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()

        var project = projectRepo.findOneById(projectId) ?: throw ResourceNotFoundException()
        val oldStatus = project.projectStatus.status

        val projectStatus = projectStatusRepo.save(getStatusEntity(project, statusChange.status!!, user, statusChange.note))
        project = projectRepo.save(updateProject(project, projectStatus))

        auditService.logEvent(Audit.projectStatusChanged(
            currentUser = securityService.currentUser,
            projectId = project.id.toString(),
            oldStatus = oldStatus,
            newStatus = projectStatus.status
        ))
        return project.toOutputProject()
    }

    @Transactional
    override fun setQualityAssessment(
        projectId: Long,
        qualityAssessmentData: InputProjectQualityAssessment
    ): OutputProject {
        val user = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()
        val project = projectRepo.findOneById(projectId) ?: throw ResourceNotFoundException()

        val qualityAssessment = ProjectQualityAssessment(
            id = projectId,
            project = project,
            result = qualityAssessmentData.result!!,
            user = user,
            note = qualityAssessmentData.note
        )
        val result = projectRepo.save(project.copy(qualityAssessment = qualityAssessment)).toOutputProject()

        auditService.logEvent(Audit.qualityAssessmentConcluded(
            currentUser = securityService.currentUser,
            projectId = project.id.toString(),
            result = project.qualityAssessment!!.result
        ))
        return result
    }

    @Transactional
    override fun setEligibilityAssessment(
        projectId: Long,
        eligibilityAssessmentData: InputProjectEligibilityAssessment
    ): OutputProject {
        val user = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()
        val project = projectRepo.findOneById(projectId) ?: throw ResourceNotFoundException()

        val eligibilityAssessment = ProjectEligibilityAssessment(
            id = projectId,
            project = project,
            result = eligibilityAssessmentData.result!!,
            user = user,
            note = eligibilityAssessmentData.note
        )
        val result = projectRepo.save(project.copy(eligibilityAssessment = eligibilityAssessment)).toOutputProject()

        auditService.logEvent(Audit.eligibilityAssessmentConcluded(
            currentUser = securityService.currentUser,
            projectId = project.id.toString(),
            result = project.eligibilityAssessment!!.result
        ))
        return result
    }

    private fun updateProject(oldProject: Project, newStatus: ProjectStatus): Project {
        if (newStatus.status == ProjectApplicationStatus.SUBMITTED) {
            return oldProject.copy(projectStatus = newStatus, submissionDate = ZonedDateTime.now())
        } else {
            return oldProject.copy(projectStatus = newStatus)
        }
    }

    private fun getStatusEntity(project: Project, status: ProjectApplicationStatus, user: User, note: String?): ProjectStatus {
        return ProjectStatus(
            project = project,
            status = status,
            user = user,
            updated = ZonedDateTime.now(),
            note = note
        )
    }

}
