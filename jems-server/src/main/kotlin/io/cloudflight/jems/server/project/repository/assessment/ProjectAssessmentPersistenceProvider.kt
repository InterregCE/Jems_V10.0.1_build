package io.cloudflight.jems.server.project.repository.assessment

import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.ProjectAssessmentPersistence
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectAssessmentPersistenceProvider(
    private val eligibilityRepository: ProjectAssessmentEligibilityRepository,
    private val qualityRepository: ProjectAssessmentQualityRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
) : ProjectAssessmentPersistence {

    @Transactional(readOnly = true)
    override fun eligibilityForStepExists(projectId: Long, step: Int): Boolean =
        eligibilityRepository.existsByIdProjectIdAndIdStep(projectId, step)

    @Transactional
    override fun setEligibility(userId: Long, data: ProjectAssessmentEligibility) {
        eligibilityRepository.save(
            data.toEntity(
                project = projectRepository.getById(data.projectId),
                user = userRepository.getById(userId),
            )
        )
    }

    @Transactional(readOnly = true)
    override fun qualityForStepExists(projectId: Long, step: Int): Boolean =
        qualityRepository.existsByIdProjectIdAndIdStep(projectId, step)

    @Transactional
    override fun setQuality(userId: Long, data: ProjectAssessmentQuality) {
        qualityRepository.save(
            data.toEntity(
                project = projectRepository.getById(data.projectId),
                user = userRepository.getById(userId),
            )
        )
    }

}
