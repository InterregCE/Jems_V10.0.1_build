package io.cloudflight.jems.server.project.controller.report

import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecoveryDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportSummaryDTO
import io.cloudflight.jems.api.project.dto.report.ReportStatusDTO
import io.cloudflight.jems.api.project.dto.report.file.*
import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportIdentificationCoFinancingDTO
import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportPeriodDTO
import io.cloudflight.jems.server.programme.controller.fund.toDto
import io.cloudflight.jems.server.programme.controller.legalstatus.toDto
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileSearchRequest
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page
import org.springframework.web.multipart.MultipartFile
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

fun ProjectPartnerReportSummary.toDto() = ProjectPartnerReportSummaryDTO(
    id = id,
    reportNumber = reportNumber,
    status = ReportStatusDTO.valueOf(status.name),
    linkedFormVersion = version,
    firstSubmission = firstSubmission,
    createdAt = createdAt,
    startDate = startDate,
    endDate = endDate,
    periodDetail = periodDetail?.toDto(),
)

fun ProjectPartnerReportPeriod.toDto() = ProjectPartnerReportPeriodDTO(
    number = number,
    periodBudget = periodBudget,
    periodBudgetCumulative = periodBudgetCumulative,
    start = start,
    end = end,
)

fun Page<ProjectPartnerReportSummary>.toDto() = map { it.toDto() }

fun ProjectPartnerReport.toDto() = ProjectPartnerReportDTO(
    id = id,
    reportNumber = reportNumber,
    status = ReportStatusDTO.valueOf(status.name),
    linkedFormVersion = version,

    identification = identification.toDto()
)

fun ReportStatus.toDto() = ReportStatusDTO.valueOf(name)

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
    country = country,
    currency = currency,
    coFinancing = coFinancing.filter { it.fund != null }.map {
        PartnerReportIdentificationCoFinancingDTO(
            fund = it.fund!!.toDto(),
            percentage = it.percentage,
        )
    }
)

fun JemsFile.toDto() = ProjectReportFileDTO(
    id = id,
    name = name,
    type = ProjectPartnerReportFileTypeDTO.valueOf(type.name),
    uploaded = uploaded,
    author = partnerReportMapper.map(author),
    size = size,
    sizeString = size.sizeToString(),
    description = description,
)

private val sizeUnits = arrayOf("B", "kB", "MB", "GB", "TB")
private val sizeFormat = DecimalFormat("#,##0.#")
fun Long.sizeToString(): String {
    if (this <= 0)
        return "0"
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return sizeFormat.format(this / 1024.0.pow(digitGroups.toDouble())) + "\u0020" + sizeUnits[digitGroups]
}

fun ProjectReportFileSearchRequestDTO.toModel() = JemsFileSearchRequest(
    reportId = reportId,
    treeNode = JemsFileType.valueOf(treeNode.name),
    filterSubtypes = filterSubtypes.mapTo(HashSet()) { JemsFileType.valueOf(it.name) }
)

fun MultipartFile.toProjectFile() = ProjectFile(inputStream, originalFilename ?: name, size)

val partnerReportMapper = Mappers.getMapper(ProjectPartnerReportMapper::class.java)

fun JemsFileMetadata.toDto() = partnerReportMapper.map(this)

@Mapper
interface ProjectPartnerReportMapper {
    fun map(model: JemsFileMetadata): ProjectReportFileMetadataDTO
    fun map(model: UserSimple): UserSimpleDTO
}
