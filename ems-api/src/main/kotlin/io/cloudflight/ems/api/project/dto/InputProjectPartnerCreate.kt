package io.cloudflight.ems.api.project.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class InputProjectPartnerCreate(

    @field:NotBlank(message = "project.partner.name.should.not.be.empty")
    @field:Size(max = 15, message = "project.partner.name.size.too.long")
    val name: String?,

    @field:NotNull(message = "project.partner.role.should.not.be.empty")
    val role: ProjectPartnerRole?,

    /**
     * Optional: if creating new LeadPartner when there is already one
     */
    val oldLeadPartnerId: Long? = null,

    val organization: InputProjectPartnerOrganization? = null

)
