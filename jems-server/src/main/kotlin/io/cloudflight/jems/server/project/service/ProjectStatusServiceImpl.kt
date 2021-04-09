package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.InputProjectStatus
import io.cloudflight.jems.api.project.dto.InputRevertProjectStatus
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.status.OutputRevertProjectStatus
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.APPROVED
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.APPROVED_WITH_CONDITIONS
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.Companion.isFundingStatus
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.DRAFT
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.ELIGIBLE
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.INELIGIBLE
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.NOT_APPROVED
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.RETURNED_TO_APPLICANT
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.SUBMITTED
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectEligibilityAssessment
import io.cloudflight.jems.server.project.entity.ProjectQualityAssessment
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectStatusHistoryRepository
import io.cloudflight.jems.server.user.repository.UserRepository
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.audit.service.AuditService
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

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
