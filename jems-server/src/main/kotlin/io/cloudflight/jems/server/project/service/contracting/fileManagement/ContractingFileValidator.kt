package io.cloudflight.jems.server.project.service.contracting.fileManagement

import io.cloudflight.jems.server.project.service.contracting.fileManagement.listContractingFiles.InvalidSearchConfiguration
import io.cloudflight.jems.server.project.service.contracting.fileManagement.listContractingFiles.InvalidSearchFilterConfiguration
import io.cloudflight.jems.server.project.service.contracting.fileManagement.listContractingFiles.InvalidSearchFilterPartnerWithoutId
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile.validateSearchConfiguration

val MONITORING_ALLOWED_FILE_TYPES = mapOf(
    ProjectPartnerReportFileType.Contracting to setOf(
        ProjectPartnerReportFileType.Contract,
        ProjectPartnerReportFileType.ContractDoc,
        ProjectPartnerReportFileType.ContractPartnerDoc,
        ProjectPartnerReportFileType.ContractInternal
    ),
    ProjectPartnerReportFileType.ContractSupport to setOf(
        ProjectPartnerReportFileType.Contract,
        ProjectPartnerReportFileType.ContractDoc
    ),
    ProjectPartnerReportFileType.Contract to setOf(ProjectPartnerReportFileType.Contract),
    ProjectPartnerReportFileType.ContractDoc to setOf(ProjectPartnerReportFileType.ContractDoc),
    ProjectPartnerReportFileType.ContractPartner to setOf(ProjectPartnerReportFileType.ContractPartnerDoc),
    ProjectPartnerReportFileType.ContractPartnerDoc to setOf(ProjectPartnerReportFileType.ContractPartnerDoc),
    ProjectPartnerReportFileType.ContractInternal to setOf(ProjectPartnerReportFileType.ContractInternal),
)

val CONTRACT_ALLOWED_FILE_TYPES = mapOf(
    ProjectPartnerReportFileType.ContractSupport to setOf(
        ProjectPartnerReportFileType.Contract,
        ProjectPartnerReportFileType.ContractDoc
    ),
    ProjectPartnerReportFileType.Contract to setOf(ProjectPartnerReportFileType.Contract),
    ProjectPartnerReportFileType.ContractDoc to setOf(ProjectPartnerReportFileType.ContractDoc),
)

fun validateInternalFile(fileType:  ProjectPartnerReportFileType?) {
    if (fileType == null || fileType != ProjectPartnerReportFileType.ContractInternal) {
        throw FileNotFound()
    }
}

fun validateContractFile(fileType:  ProjectPartnerReportFileType?) {
    if (fileType == null || fileType !in CONTRACT_ALLOWED_FILE_TYPES) {
        throw FileNotFound()
    }
}


fun validateConfiguration(
    searchRequest: ProjectContractingFileSearchRequest,
    partnerId: Long?,
    allowedFilers: Map<ProjectPartnerReportFileType, Set<ProjectPartnerReportFileType>>
) {
    validateSearchConfiguration(
        treeNode = searchRequest.treeNode,
        filterSubtypes = searchRequest.filterSubtypes,
        allowedFilters = allowedFilers,
        { InvalidSearchConfiguration() },
        { invalidFilters -> InvalidSearchFilterConfiguration(invalidFilters) },
    )

    if (searchRequest.treeNode == ProjectPartnerReportFileType.ContractPartnerDoc && partnerId == null)
        throw InvalidSearchFilterPartnerWithoutId()
}
