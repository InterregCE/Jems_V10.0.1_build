package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.ProjectDecisionEntity
import io.cloudflight.jems.server.project.entity.ProjectEligibilityAssessment
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectQualityAssessment
import io.cloudflight.jems.server.project.repository.ProjectDecisionRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectStatusServiceImpl(
    private val projectRepo: ProjectRepository,
    private val userRepository: UserRepository,
    private val projectDecisionRepository: ProjectDecisionRepository,
    private val auditPublisher: ApplicationEventPublisher,
    private val securityService: SecurityService
) : ProjectStatusService {

    @Transactional
    override fun setQualityAssessment(
        projectId: Long,
        qualityAssessmentData: InputProjectQualityAssessment
    ): ProjectDetailDTO {
        val user = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()
        val project = projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }
        saveQualityAssessment(project, qualityAssessmentData, user)

        val result = projectRepo.save(project).toOutputProject()

        auditPublisher.publishEvent(
            if (result.step2Active)
                qualityAssessmentStep2Concluded(this, project.toOutputProject())
            else
                qualityAssessmentStep1Concluded(this, project.toOutputProject())
        )
        return result
    }

    @Transactional
    override fun setEligibilityAssessment(
        projectId: Long,
        eligibilityAssessmentData: InputProjectEligibilityAssessment
    ): ProjectDetailDTO {
        val user = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()
        val project = projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }
        saveEligibilityAssessment(project, eligibilityAssessmentData, user)

        val result = projectRepo.save(project).toOutputProject()

        auditPublisher.publishEvent(
            if (result.step2Active)
                eligibilityAssessmentStep2Concluded(this, project.toOutputProject())
            else
                eligibilityAssessmentStep1Concluded(this, project.toOutputProject())
        )
        return result
    }

    private fun saveQualityAssessment(
        project: ProjectEntity,
        qualityAssessmentData: InputProjectQualityAssessment,
        user: UserEntity
    ) {
        val decisionEntity = project.getDecision()
        decisionEntity.qualityAssessment = ProjectQualityAssessment(
            id = decisionEntity.id,
            projectDecision = decisionEntity,
            result = qualityAssessmentData.result!!,
            user = user,
            note = qualityAssessmentData.note
        )

        projectDecisionRepository.save(decisionEntity)
    }

    private fun saveEligibilityAssessment(
        project: ProjectEntity,
        eligibilityAssessmentData: InputProjectEligibilityAssessment,
        user: UserEntity
    ) {
        val decisionEntity = project.getDecision()
        decisionEntity.eligibilityAssessment = ProjectEligibilityAssessment(
            id = decisionEntity.id,
            projectDecision = decisionEntity,
            result = eligibilityAssessmentData.result!!,
            user = user,
            note = eligibilityAssessmentData.note
        )

        projectDecisionRepository.save(decisionEntity)
    }

    private fun ProjectEntity.getDecision(): ProjectDecisionEntity {
        if (currentStatus.status.isInStep2()) {
            if (secondStepDecision == null)
                secondStepDecision = ProjectDecisionEntity(project = this)
            return secondStepDecision!!
        } else {
            if (firstStepDecision == null)
                firstStepDecision = ProjectDecisionEntity(project = this)
            return firstStepDecision!!
        }
    }
}
