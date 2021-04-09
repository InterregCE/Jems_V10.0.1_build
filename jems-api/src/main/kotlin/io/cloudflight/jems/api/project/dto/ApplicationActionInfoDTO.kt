package io.cloudflight.jems.api.project.dto

import io.swagger.annotations.ApiModel
import java.time.LocalDate
import javax.validation.constraints.PastOrPresent
import javax.validation.constraints.Size

@ApiModel(value = "ApplicationActionInfoDTO")
data class ApplicationActionInfoDTO(

    @field:Size(max = 10000, message = "project.status.note.size.too.long")
    val note: String?,

    @field:PastOrPresent(message = "project.status.date.should.be.in.past.or.present")
    val date: LocalDate?
)
