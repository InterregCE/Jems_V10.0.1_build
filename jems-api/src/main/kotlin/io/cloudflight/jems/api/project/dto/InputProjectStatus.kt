package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import java.time.LocalDate
import javax.validation.constraints.NotNull
import javax.validation.constraints.PastOrPresent
import javax.validation.constraints.Size

data class InputProjectStatus(

    @field:NotNull(message = "project.status.status.should.not.be.empty")
    val status: ProjectApplicationStatus?,

    @field:Size(max = 10000, message = "project.status.note.size.too.long")
    val note: String?,

    @field:PastOrPresent(message = "project.status.date.should.be.in.past.or.present")
    val date: LocalDate?
)
