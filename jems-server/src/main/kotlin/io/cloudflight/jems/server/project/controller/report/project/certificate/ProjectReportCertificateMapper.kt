package io.cloudflight.jems.server.project.controller.report.project.certificate

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.report.project.certificate.PartnerReportCertificateDTO
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate

fun PartnerReportCertificate.toDto(currentProjectReportId: Long) = PartnerReportCertificateDTO(
    partnerReportId = partnerReportId,
    partnerReportNumber = partnerReportNumber,
    partnerId = partnerId,
    partnerRole = ProjectPartnerRoleDTO.valueOf(partnerRole.name),
    partnerNumber = partnerNumber,
    controlEnd = controlEnd,
    totalEligibleAfterControl = totalEligibleAfterControl,
    projectReportId = projectReportId,
    projectReportNumber = projectReportNumber,
    disabled = projectReportId != null && projectReportId != currentProjectReportId,
    checked = projectReportId == currentProjectReportId,
)
