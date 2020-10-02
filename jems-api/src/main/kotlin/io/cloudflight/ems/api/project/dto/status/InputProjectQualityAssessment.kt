package io.cloudflight.ems.api.project.dto.status

import io.cloudflight.ems.api.project.dto.status.ProjectQualityAssessmentResult
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class InputProjectQualityAssessment(

    @field:NotNull(message = "project.status.qualityassessment.should.not.be.empty")
    val result: ProjectQualityAssessmentResult?,

    @field:Size(max = 1000, message = "project.qualityassessment.note.size.too.long")
    val note: String? = null

)
