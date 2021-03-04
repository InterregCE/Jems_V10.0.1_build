package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class InputProjectPartnerUpdate(

    val id: Long,

    @field:NotBlank(message = "project.partner.name.should.not.be.empty")
    @field:Size(max = 15, message = "project.partner.name.size.too.long")
    val abbreviation: String?,

    @field:NotNull(message = "project.partner.role.should.not.be.empty")
    val role: ProjectPartnerRole?,

    /**
     * Optional: if creating new LeadPartner when there is already one (then it is mandatory)
     */
    val oldLeadPartnerId: Long? = null,

    @field:Size(max = 100, message = "project.organization.original.name.size.too.long")
    val nameInOriginalLanguage: String? = null,

    @field:Size(max = 100, message = "project.organization.english.name.size.too.long")
    val nameInEnglish: String? = null,

    val department: Set<InputTranslation> = emptySet(),

    val partnerType: ProjectTargetGroup? = null,

    @field:NotNull(message = "project.partner.legalstatus.should.not.be.empty")
    val legalStatusId: Long?,

    @field:Size(max = 50, message = "project.partner.vat.size.too.long")
    val vat: String? = null,

    val vatRecovery: ProjectPartnerVatRecovery? = null

)
