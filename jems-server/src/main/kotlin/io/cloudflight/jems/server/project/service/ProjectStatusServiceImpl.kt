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
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectStatusServiceImpl(
    private val projectRepo: ProjectRepository,
    private val userRepository: UserRepository,
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

        val qualityAssessment = ProjectQualityAssessment(
            id = projectId,
            projectDecision = getDecisionBasedOnStep(projectId),
            result = qualityAssessmentData.result!!,
            user = user,
            note = qualityAssessmentData.note
        )
        project = if (project.step2Active) {
            project.copy( secondStepDecision = project.secondStepDecision?.copy(qualityAssessment = qualityAssessment))
        } else {
            project.copy( firstStepDecision = project.firstStepDecision?.copy(qualityAssessment = qualityAssessment))
        }

        val result = projectRepo.save(project).toOutputProject()

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

        val eligibilityAssessment = ProjectEligibilityAssessment(
            id = projectId,
            projectDecision = getDecisionBasedOnStep(projectId),
            result = eligibilityAssessmentData.result!!,
            user = user,
            note = eligibilityAssessmentData.note
        )

        project = if (project.step2Active) {
            project.copy( secondStepDecision = project.secondStepDecision?.copy(eligibilityAssessment = eligibilityAssessment))
        } else {
            project.copy( firstStepDecision = project.firstStepDecision?.copy(eligibilityAssessment = eligibilityAssessment))
        }

        val result = projectRepo.save(project).toOutputProject()

        auditService.logEvent(
            eligibilityAssessmentConcluded(projectDetailDTO = result)
        )
        return result
    }

    private fun getProjectOrThrow(projectId: Long): ProjectEntity =
        projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }


    private fun getDecisionBasedOnStep(projectId: Long): ProjectDecisionEntity {
        val projectEntity = getProjectOrThrow(projectId)

        return if (projectEntity.step2Active)
            projectEntity.secondStepDecision!!
        else
            projectEntity.firstStepDecision!!
    }
}
