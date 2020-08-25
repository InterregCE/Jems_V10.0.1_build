package io.cloudflight.ems.api.project.dto

import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import org.hibernate.validator.constraints.Range
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class InputProjectData(

    @field:NotBlank(message = "project.acronym.should.not.be.empty")
    @field:Size(max = 25, message = "project.acronym.size.too.long")
    val acronym: String?,

    val specificObjective: ProgrammeObjectivePolicy? = null,

    @field:Size(max = 250, message = "project.title.size.too.long")
    val title: String? = null,

    @field:Range(min = 1, max = 999, message = "programme.duration.invalid")
    val duration: Int? = null,

    @field:Size(max = 2000, message = "project.intro.size.too.long")
    val intro: String? = null,

    @field:Size(max = 2000, message = "project.introProgrammeLanguage.size.too.long")
    val introProgrammeLanguage: String? = null

)
