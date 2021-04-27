package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.server.project.entity.ProjectEligibilityAssessment
import io.cloudflight.jems.server.project.entity.ProjectQualityAssessment
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.audit.service.AuditService
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
        val project = projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

        val qualityAssessment = ProjectQualityAssessment(
            id = projectId,
            project = project,
            result = qualityAssessmentData.result!!,
            user = user,
            note = qualityAssessmentData.note
        )
        val result = projectRepo.save(project.copy(qualityAssessment = qualityAssessment)).toOutputProject()

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
        val project = projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

        val eligibilityAssessment = ProjectEligibilityAssessment(
            id = projectId,
            project = project,
            result = eligibilityAssessmentData.result!!,
            user = user,
            note = eligibilityAssessmentData.note
        )
        val result = projectRepo.save(project.copy(eligibilityAssessment = eligibilityAssessment)).toOutputProject()

        auditService.logEvent(
            eligibilityAssessmentConcluded(projectDetailDTO = result)
        )
        return result
    }
}
