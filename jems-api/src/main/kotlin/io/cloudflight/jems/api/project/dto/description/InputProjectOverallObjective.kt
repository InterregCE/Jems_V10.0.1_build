package io.cloudflight.jems.api.project.dto.description

import javax.validation.constraints.Size

data class InputProjectOverallObjective(

    @field:Size(max = 500, message = "project.description.overallObjective.size.too.long")
    val overallObjective: String?

)
