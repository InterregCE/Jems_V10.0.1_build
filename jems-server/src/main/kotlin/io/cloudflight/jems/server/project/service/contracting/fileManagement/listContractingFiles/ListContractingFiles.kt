package io.cloudflight.jems.server.project.service.contracting.fileManagement.listContractingFiles

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.ProjectContractInfoAuthorization
import io.cloudflight.jems.server.project.authorization.ProjectMonitoringAuthorization
import io.cloudflight.jems.server.project.service.contracting.fileManagement.CONTRACT_ALLOWED_FILE_TYPES
import io.cloudflight.jems.server.project.service.contracting.fileManagement.MONITORING_ALLOWED_FILE_TYPES
import io.cloudflight.jems.server.project.service.contracting.fileManagement.validateConfiguration
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.*
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListContractingFiles(
    private val partnerPersistence: PartnerPersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val contractInfoAuth: ProjectContractInfoAuthorization,
    private val projectMonitoringAuthorization: ProjectMonitoringAuthorization
): ListContractingFilesInteractor {


    @Transactional(readOnly = true)
    @ExceptionWrapper(ListContractingFilesException::class)
    override fun list(
        projectId: Long,
        partnerId: Long?,
        pageable: Pageable,
        searchRequest: ProjectContractingFileSearchRequest,
    ): Page<ProjectReportFile> {

        if (projectMonitoringAuthorization.canViewProjectMonitoring(projectId)) {
            validateConfiguration(searchRequest = searchRequest, partnerId, MONITORING_ALLOWED_FILE_TYPES)
            val filePathPrefix = generateSearchString(
                treeNode = searchRequest.treeNode,
                projectId = projectId,
                partnerId = partnerId?.let { if (partnerPersistence.getProjectIdForPartnerId(it) == projectId) it else null },
            )
            return reportFilePersistence.listAttachments(
                pageable = pageable,
                indexPrefix = filePathPrefix,
                filterSubtypes = getFileSubTypesBasedOnContractInfoPermission(searchRequest.filterSubtypes, projectId),
                filterUserIds = emptySet(),
            )
        } else if (contractInfoAuth.canViewContractInfo(projectId)) {
            validateConfiguration(searchRequest = searchRequest, partnerId, CONTRACT_ALLOWED_FILE_TYPES)
           return reportFilePersistence.listAttachments(
                pageable = pageable,
                indexPrefix = searchRequest.treeNode.generatePath(projectId),
                filterSubtypes = searchRequest.filterSubtypes,
                filterUserIds = emptySet(),
            )
        }

        throw InvalidSearchConfiguration()
    }

    private fun generateSearchString(
        treeNode: ProjectPartnerReportFileType,
        projectId: Long,
        partnerId: Long?,
    ): String {
        return when (treeNode) {
            Contracting, ContractSupport, Contract, ContractDoc, ContractPartner, ContractInternal ->
                treeNode.generatePath(projectId)
            ContractPartnerDoc ->
                treeNode.generatePath(projectId, partnerId!!)
            else ->
                throw InvalidSearchConfiguration()
        }
    }

    private fun getFileSubTypesBasedOnContractInfoPermission(
        filterSubtypes: Set<ProjectPartnerReportFileType>,
        projectId: Long
    ): Set<ProjectPartnerReportFileType> {
        return if (contractInfoAuth.canViewContractInfo(projectId) || contractInfoAuth.canEditContractInfo(projectId)) {
            filterSubtypes
        } else {
            setOf(ContractInternal)
        }
    }
}
