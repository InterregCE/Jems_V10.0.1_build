package io.cloudflight.jems.server.project.service.contracting.management.file.listContractingFiles

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectManagement
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.*
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile.validateSearchConfiguration
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListContractingFiles(
    private val partnerPersistence: PartnerPersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
): ListContractingFilesInteractor {

    companion object {
        private val ALLOWED_FILTERS = mapOf(
            Contracting to setOf(Contract, ContractDoc, ContractPartnerDoc, ContractInternal),
            ContractSupport to setOf(Contract, ContractDoc),
            Contract to setOf(Contract),
            ContractDoc to setOf(ContractDoc),
            ContractPartner to setOf(ContractPartnerDoc),
            ContractPartnerDoc to setOf(ContractPartnerDoc),
            ContractInternal to setOf(ContractInternal),
        )
    }

    @CanViewProjectManagement
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListContractingFilesException::class)
    override fun list(
        projectId: Long,
        partnerId: Long?,
        pageable: Pageable,
        searchRequest: ProjectContractingFileSearchRequest,
    ): Page<ProjectReportFile> {
        validateConfiguration(searchRequest = searchRequest, partnerId)

        val filePathPrefix = generateSearchString(
            treeNode = searchRequest.treeNode,
            projectId = projectId,
            partnerId = partnerId?.let { if (partnerPersistence.getProjectIdForPartnerId(it) == projectId) it else null },
        )

        return reportFilePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = filePathPrefix,
            filterSubtypes = searchRequest.filterSubtypes,
            filterUserIds = emptySet(),
        )
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

    private fun validateConfiguration(searchRequest: ProjectContractingFileSearchRequest, partnerId: Long?) {
        validateSearchConfiguration(
            treeNode = searchRequest.treeNode,
            filterSubtypes = searchRequest.filterSubtypes,
            allowedFilters = ALLOWED_FILTERS,
            { InvalidSearchConfiguration() },
            { invalidFilters -> InvalidSearchFilterConfiguration(invalidFilters) },
        )

        if (searchRequest.treeNode == ContractPartnerDoc && partnerId == null)
            throw InvalidSearchFilterPartnerWithoutId()
    }

}
