package io.cloudflight.ems.api.project.dto.description

import javax.validation.constraints.Size

data class InputProjectPartnership(

    @field:Size(max = 5000, message = "project.description.partnership.size.too.long")
    val partnership: String?

)
