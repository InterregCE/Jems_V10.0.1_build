package io.cloudflight.jems.server.project.entity.assessment

import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.ZonedDateTime
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_assessment_quality")
class ProjectAssessmentQualityEntity(

    @EmbeddedId
    val id: ProjectAssessmentId,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val result: ProjectAssessmentQualityResult,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @field:NotNull
    val user: UserEntity,

    @field:NotNull
    val updated: ZonedDateTime = ZonedDateTime.now(),

    val note: String? = null

)
