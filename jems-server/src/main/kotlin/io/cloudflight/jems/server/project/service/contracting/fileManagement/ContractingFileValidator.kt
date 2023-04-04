package io.cloudflight.jems.server.project.service.contracting.fileManagement

import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.contracting.fileManagement.listContractingFiles.InvalidSearchConfiguration
import io.cloudflight.jems.server.project.service.contracting.fileManagement.listContractingFiles.InvalidSearchFilterConfiguration
import io.cloudflight.jems.server.project.service.contracting.fileManagement.listContractingFiles.InvalidSearchFilterPartnerWithoutId
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile.validateSearchConfiguration

val MONITORING_ALLOWED_FILE_TYPES = mapOf(
    JemsFileType.Contracting to setOf(
        JemsFileType.Contract,
        JemsFileType.ContractDoc,
        JemsFileType.ContractPartnerDoc,
        JemsFileType.ContractInternal
    ),
    JemsFileType.ContractSupport to setOf(
        JemsFileType.Contract,
        JemsFileType.ContractDoc
    ),
    JemsFileType.Contract to setOf(JemsFileType.Contract),
    JemsFileType.ContractDoc to setOf(JemsFileType.ContractDoc),
    JemsFileType.ContractPartner to setOf(JemsFileType.ContractPartnerDoc),
    JemsFileType.ContractPartnerDoc to setOf(JemsFileType.ContractPartnerDoc),
    JemsFileType.ContractInternal to setOf(JemsFileType.ContractInternal),
)

val CONTRACT_ALLOWED_FILE_TYPES = mapOf(
    JemsFileType.ContractSupport to setOf(
        JemsFileType.Contract,
        JemsFileType.ContractDoc
    ),
    JemsFileType.Contract to setOf(JemsFileType.Contract),
    JemsFileType.ContractDoc to setOf(JemsFileType.ContractDoc),
)

val PARTNER_ALLOWED_FILE_TYPES = mapOf(
    JemsFileType.ContractPartner to setOf(JemsFileType.ContractPartnerDoc),
    JemsFileType.ContractPartnerDoc to setOf(JemsFileType.ContractPartnerDoc),
)

fun validateInternalFile(fileType: JemsFileType?) {
    if (fileType == null || fileType != JemsFileType.ContractInternal) {
        throw FileNotFound()
    }
}

fun validateContractFile(fileType:  JemsFileType?) {
    if (fileType == null || fileType !in CONTRACT_ALLOWED_FILE_TYPES) {
        throw FileNotFound()
    }
}

fun validatePartnerFile(fileType: JemsFileType?) {
    if (fileType == null || fileType !in PARTNER_ALLOWED_FILE_TYPES) {
        throw FileNotFound()
    }
}

fun validateConfiguration(
    searchRequest: ProjectContractingFileSearchRequest,
    partnerId: Long?,
    allowedFilters: Map<JemsFileType, Set<JemsFileType>>
) {
    validateSearchConfiguration(
        treeNode = searchRequest.treeNode,
        filterSubtypes = searchRequest.filterSubtypes,
        allowedFilters = allowedFilters,
        { InvalidSearchConfiguration() },
        { invalidFilters -> InvalidSearchFilterConfiguration(invalidFilters) },
    )

    if (searchRequest.treeNode == JemsFileType.ContractPartnerDoc && partnerId == null)
        throw InvalidSearchFilterPartnerWithoutId()
}
