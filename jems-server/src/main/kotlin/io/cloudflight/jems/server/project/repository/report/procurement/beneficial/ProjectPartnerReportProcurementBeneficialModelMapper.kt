package io.cloudflight.jems.server.project.repository.report.procurement.beneficial

import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.beneficial.ProjectPartnerReportProcurementBeneficialEntity
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChange
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner

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
