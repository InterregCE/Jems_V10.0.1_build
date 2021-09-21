package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.server.project.entity.AddressEntity
import javax.persistence.Embedded
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_address")
data class ProjectPartnerAddressEntity(

    @EmbeddedId
    val addressId: ProjectPartnerAddressId,

    @Embedded
    val address: AddressEntity

)
