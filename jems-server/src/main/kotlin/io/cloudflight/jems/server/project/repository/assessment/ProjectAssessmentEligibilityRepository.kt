package io.cloudflight.jems.server.project.repository.assessment

import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentEligibilityEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectAssessmentEligibilityRepository: JpaRepository<ProjectAssessmentEligibilityEntity, ProjectAssessmentId> {

    fun existsByIdProjectIdAndIdStep(projectId: Long, step: Int): Boolean

}
