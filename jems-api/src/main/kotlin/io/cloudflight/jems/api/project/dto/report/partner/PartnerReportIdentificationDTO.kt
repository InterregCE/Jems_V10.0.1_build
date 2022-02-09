package io.cloudflight.jems.api.project.dto.report.partner

import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusDTO
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecoveryDTO

data class PartnerReportIdentificationDTO(
    val projectIdentifier: String,
    val projectAcronym: String,
    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val partnerRole: ProjectPartnerRoleDTO,
    val nameInOriginalLanguage: String?,
    val nameInEnglish: String?,
    val legalStatus: ProgrammeLegalStatusDTO?,
    val partnerType: ProjectTargetGroupDTO?,
    val vatRecovery: ProjectPartnerVatRecoveryDTO?,
    val coFinancing: List<PartnerReportIdentificationCoFinancingDTO>,
)
