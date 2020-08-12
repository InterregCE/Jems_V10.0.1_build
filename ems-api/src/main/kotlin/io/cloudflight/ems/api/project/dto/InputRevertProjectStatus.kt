package io.cloudflight.ems.api.project.dto

import javax.validation.constraints.NotNull

data class InputRevertProjectStatus(

    @field:NotNull(message = "project.revert.projectStatusFromId.should.not.be.empty")
    val projectStatusFromId: Long?,

    @field:NotNull(message = "project.revert.projectStatusToId.should.not.be.empty")
    val projectStatusToId: Long?

)
