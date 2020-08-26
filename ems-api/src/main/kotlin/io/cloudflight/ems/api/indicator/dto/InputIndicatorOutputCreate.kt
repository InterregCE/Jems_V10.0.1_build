package io.cloudflight.ems.api.indicator.dto

import io.cloudflight.ems.api.indicator.validator.UniqueIndicatorOutputIdentifier
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import java.math.BigDecimal
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class InputIndicatorOutputCreate(

    @field:NotBlank(message = "indicator.identifier.should.not.be.empty")
    @field:Size(max = 5, message = "indicator.identifier.size.too.long")
    @UniqueIndicatorOutputIdentifier
    val identifier: String?,

    @field:Size(max = 6, message = "indicator.code.size.too.long")
    val code: String?,

    @field:NotBlank(message = "indicator.name.should.not.be.empty")
    @field:Size(max = 250, message = "indicator.name.size.too.long")
    val name: String?,

    val programmeObjectivePolicy: ProgrammeObjectivePolicy?,

    @field:Size(max = 255, message = "indicator.measurementUnit.size.too.long")
    val measurementUnit: String?,

    @field:DecimalMin(value = "0.0", message = "indicator.milestone.out.of.range")
    @field:Digits(integer = 9, fraction = 2, message = "indicator.milestone.out.of.range")
    val milestone: BigDecimal?,

    @field:DecimalMin(value = "0.0", message = "indicator.finalTarget.out.of.range")
    @field:Digits(integer = 9, fraction = 2, message = "indicator.finalTarget.out.of.range")
    val finalTarget: BigDecimal?

)
