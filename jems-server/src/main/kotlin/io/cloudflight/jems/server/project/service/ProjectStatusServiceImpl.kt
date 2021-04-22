package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.ProjectDecisionEntity
import io.cloudflight.jems.server.project.entity.ProjectEligibilityAssessment
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectQualityAssessment
import io.cloudflight.jems.server.project.repository.ProjectDecisionRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectStatusServiceImpl(
    private val projectRepo: ProjectRepository,
    private val userRepository: UserRepository,
    private val projectDecisionRepository: ProjectDecisionRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
) : ProjectStatusService {

    @Transactional
    override fun setQualityAssessment(
        projectId: Long,
        qualityAssessmentData: InputProjectQualityAssessment
    ): ProjectDetailDTO {
        val user = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()
        var project = projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }
        saveQualityAssessment(project, qualityAssessmentData, user)

        val result = project.toOutputProject()

        auditService.logEvent(qualityAssessmentConcluded(projectDetailDTO = result))
        return result
    }

    @Transactional
    override fun setEligibilityAssessment(
        projectId: Long,
        eligibilityAssessmentData: InputProjectEligibilityAssessment
    ): ProjectDetailDTO {
        val user = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()
        var project = projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }
        saveEligibilityAssessment(project, eligibilityAssessmentData, user)

        val result = projectRepo.save(project).toOutputProject()

        auditService.logEvent(
            eligibilityAssessmentConcluded(projectDetailDTO = result)
        )
        return result
    }

    private fun saveQualityAssessment(project: ProjectEntity, qualityAssessmentData: InputProjectQualityAssessment, user: User) {
        var projectDecisionEntity = ProjectDecisionEntity(project = project)

        if (project.step2Active) {
            if (project.secondStepDecision != null) {
                projectDecisionEntity = project.secondStepDecision!!
            } else
                project.secondStepDecision = projectDecisionEntity
        } else {
            if (project.firstStepDecision != null) {
                projectDecisionEntity = project.firstStepDecision!!
            } else {
                project.firstStepDecision  = projectDecisionEntity
            }
        }

        val qualityAssessment = ProjectQualityAssessment(
            id = projectDecisionEntity.id,
            projectDecision = projectDecisionEntity,
            result = qualityAssessmentData.result!!,
            user = user,
            note = qualityAssessmentData.note
        )

        projectDecisionEntity.qualityAssessment = qualityAssessment
        projectDecisionRepository.save(projectDecisionEntity)
    }

    private fun saveEligibilityAssessment(project: ProjectEntity, eligibilityAssessmentData: InputProjectEligibilityAssessment, user: User) {
        var projectDecisionEntity = ProjectDecisionEntity(project = project)

        if (project.step2Active) {
            if (project.secondStepDecision != null) {
                projectDecisionEntity = project.secondStepDecision!!
            } else
                project.secondStepDecision = projectDecisionEntity
        } else {
            if (project.firstStepDecision != null) {
                projectDecisionEntity = project.firstStepDecision!!
            } else {
                project.firstStepDecision  = projectDecisionEntity
            }
        }

        val eligibilityAssessment = ProjectEligibilityAssessment(
            id = projectDecisionEntity.id,
            projectDecision = projectDecisionEntity,
            result = eligibilityAssessmentData.result!!,
            user = user,
            note = eligibilityAssessmentData.note
        )

        projectDecisionEntity.eligibilityAssessment = eligibilityAssessment
        projectDecisionRepository.save(projectDecisionEntity)
    }
}
