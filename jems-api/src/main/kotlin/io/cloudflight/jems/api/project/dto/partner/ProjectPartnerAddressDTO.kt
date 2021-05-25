package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.InputAddress
import javax.validation.constraints.NotNull

data class ProjectPartnerAddressDTO(

    @field:NotNull(message = "project.partner.type.should.not.be.empty")
    val type: ProjectPartnerAddressType,

    override val country: String? = null,
    override val nutsRegion2: String? = null,
    override val nutsRegion3: String? = null,
    override val street: String? = null,
    override val houseNumber: String? = null,
    override val postalCode: String? = null,
    override val city: String? = null,

    val homepage: String? = null

) : InputAddress
