package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.InputOrganization
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO

data class CreateProjectPartnerRequestDTO(

    val abbreviation: String?,

    val role: ProjectPartnerRoleDTO?,

    /**
     * Optional: if creating new LeadPartner when there is already one (then it is mandatory)
     */
    val oldLeadPartnerId: Long? = null,

    override val nameInOriginalLanguage: String? = null,

    override val nameInEnglish: String? = null,

    val department: Set<InputTranslation> = emptySet(),

    val partnerType: ProjectTargetGroupDTO? = null,

    val legalStatusId: Long?,

    val vat: String? = null,

    val vatRecovery: ProjectPartnerVatRecoveryDTO? = null

): InputOrganization
