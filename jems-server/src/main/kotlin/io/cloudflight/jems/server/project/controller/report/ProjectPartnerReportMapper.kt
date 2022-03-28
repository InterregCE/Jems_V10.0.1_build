package io.cloudflight.jems.server.project.controller.report

import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecoveryDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportSummaryDTO
import io.cloudflight.jems.api.project.dto.report.ReportStatusDTO
import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportIdentificationCoFinancingDTO
import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.server.programme.controller.fund.toDto
import io.cloudflight.jems.server.programme.controller.legalstatus.toDto
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

fun ProjectPartnerReportSummary.toDto() = ProjectPartnerReportSummaryDTO(
    id = id,
    reportNumber = reportNumber,
    status = ReportStatusDTO.valueOf(status.name),
    linkedFormVersion = version,
    firstSubmission = firstSubmission,
    createdAt = createdAt,
)

fun Page<ProjectPartnerReportSummary>.toDto() = map { it.toDto() }

fun ProjectPartnerReport.toDto() = ProjectPartnerReportDTO(
    id = id,
    reportNumber = reportNumber,
    status = ReportStatusDTO.valueOf(status.name),
    linkedFormVersion = version,

    identification = identification.toDto()
)

fun PartnerReportIdentification.toDto() = PartnerReportIdentificationDTO(
    projectIdentifier = projectIdentifier,
    projectAcronym = projectAcronym,
    partnerNumber = partnerNumber,
    partnerAbbreviation = partnerAbbreviation,
    partnerRole = ProjectPartnerRoleDTO.valueOf(partnerRole.name),
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    legalStatus = legalStatus?.toDto(),
    partnerType = partnerType?.let { ProjectTargetGroupDTO.valueOf(it.name) },
    vatRecovery = vatRecovery?.let { ProjectPartnerVatRecoveryDTO.valueOf(it.name) },
    coFinancing = coFinancing.filter { it.fund != null }.map {
        PartnerReportIdentificationCoFinancingDTO(
            fund = it.fund!!.toDto(),
            percentage = it.percentage,
        )
    }
)

private val mapper = Mappers.getMapper(ProjectPartnerReportMapper::class.java)

fun ProjectReportFileMetadata.toDto() = mapper.map(this)

@Mapper
interface ProjectPartnerReportMapper {
    fun map(model: ProjectReportFileMetadata): ProjectReportFileMetadataDTO
}
