package io.cloudflight.ems.api.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class InputProjectStatus(

    @field:NotNull(message = "project.status.status.should.not.be.empty")
    val status: ProjectApplicationStatus?,

    @field:Size(max = 255, message = "project.status.note.size.too.long")
    val note: String?

)
