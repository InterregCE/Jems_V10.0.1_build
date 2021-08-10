package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup

data class UpdateProjectPartnerRequestDTO(

    val id: Long,

    val abbreviation: String?,

    val role: ProjectPartnerRoleDTO?,

    /**
     * Optional: if creating new LeadPartner when there is already one (then it is mandatory)
     */
    val oldLeadPartnerId: Long? = null,

    val nameInOriginalLanguage: String? = null,

    val nameInEnglish: String? = null,

    val department: Set<InputTranslation> = emptySet(),

    val partnerType: ProjectTargetGroup? = null,

    val legalStatusId: Long?,

    val vat: String? = null,

    val vatRecovery: ProjectPartnerVatRecoveryDTO? = null

)
