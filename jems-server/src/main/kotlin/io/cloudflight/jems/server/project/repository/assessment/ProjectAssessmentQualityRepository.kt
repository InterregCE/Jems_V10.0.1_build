package io.cloudflight.jems.server.project.repository.assessment

import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentId
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentQualityEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectAssessmentQualityRepository: JpaRepository<ProjectAssessmentQualityEntity, ProjectAssessmentId> {

    fun existsByIdProjectIdAndIdStep(projectId: Long, step: Int): Boolean

}
