package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.InputOrganization
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup

data class InputProjectPartnerCreate(

    val abbreviation: String?,

    val role: ProjectPartnerRole?,

    /**
     * Optional: if creating new LeadPartner when there is already one (then it is mandatory)
     */
    val oldLeadPartnerId: Long? = null,

    override val nameInOriginalLanguage: String? = null,

    override val nameInEnglish: String? = null,

    val department: Set<InputTranslation> = emptySet(),

    val partnerType: ProjectTargetGroup? = null,

    val legalStatusId: Long?,

    val vat: String? = null,

    val vatRecovery: ProjectPartnerVatRecovery? = null

): InputOrganization
