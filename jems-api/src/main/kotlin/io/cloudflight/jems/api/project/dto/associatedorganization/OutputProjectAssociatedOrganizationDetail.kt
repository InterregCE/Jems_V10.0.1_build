package io.cloudflight.jems.api.project.dto.associatedorganization

import io.cloudflight.jems.api.project.dto.InputOrganization
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerContactDTO

data class OutputProjectAssociatedOrganizationDetail (

    val id: Long,
    val active: Boolean,
    val partner: ProjectPartnerSummaryDTO,
    override val nameInOriginalLanguage: String? = null,
    override val nameInEnglish: String? = null,
    val sortNumber: Int? = null,
    val address: OutputProjectAssociatedOrganizationAddress? = null,
    val contacts: List<ProjectPartnerContactDTO> = emptyList(),
    val roleDescription: Set<InputTranslation> = emptySet()

): InputOrganization
