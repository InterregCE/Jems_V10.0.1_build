package io.cloudflight.jems.server.project.repository.report.partner.procurement.beneficial

import io.cloudflight.jems.server.project.entity.report.partner.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialEntity
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChange
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner

fun List<ProjectPartnerReportProcurementBeneficialEntity>.toModel() = map {
    ProjectPartnerReportProcurementBeneficialOwner(
        id = it.id,
        reportId = it.createdInReportId,
        firstName = it.firstName,
        lastName = it.lastName,
        birth = it.birth,
        vatNumber = it.vatNumber,
    )
}

fun ProjectPartnerReportProcurementBeneficialChange.toEntity(procurement: ProjectPartnerReportProcurementEntity, reportId: Long) =
    ProjectPartnerReportProcurementBeneficialEntity(
        procurement = procurement,
        createdInReportId = reportId,
        firstName = firstName,
        lastName = lastName,
        birth = birth,
        vatNumber = vatNumber,
    )
