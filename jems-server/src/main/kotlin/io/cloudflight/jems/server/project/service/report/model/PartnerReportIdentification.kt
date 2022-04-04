package io.cloudflight.jems.server.project.service.report.model

import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery

data class PartnerReportIdentification(
    val projectIdentifier: String,
    val projectAcronym: String,
    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val partnerRole: ProjectPartnerRole,
    val nameInOriginalLanguage: String?,
    val nameInEnglish: String?,
    val legalStatus: ProgrammeLegalStatus?,
    val partnerType: ProjectTargetGroup?,
    val vatRecovery: ProjectPartnerVatRecovery?,
    val country: String?,
    val currency: String?,
    var coFinancing: List<ProjectPartnerCoFinancing>
)
