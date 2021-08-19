package io.cloudflight.jems.server.call.entity

import java.io.Serializable
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class AllowRealCostsEntity(

    @field:NotNull
    var allowRealStaffCosts: Boolean = true,

    @field:NotNull
    var allowRealTravelAndAccommodationCosts: Boolean = true,

    @field:NotNull
    var allowRealExternalExpertiseAndServicesCosts: Boolean = true,

    @field:NotNull
    var allowRealEquipmentCosts: Boolean = true,

    @field:NotNull
    var allowRealInfrastructureCosts: Boolean = true,

    ) : Serializable
