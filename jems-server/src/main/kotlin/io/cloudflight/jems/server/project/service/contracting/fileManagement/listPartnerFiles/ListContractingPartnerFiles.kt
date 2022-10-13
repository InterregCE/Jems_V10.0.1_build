package io.cloudflight.jems.server.project.service.contracting.fileManagement.listPartnerFiles

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingPartner
import io.cloudflight.jems.server.project.service.contracting.fileManagement.PARTNER_ALLOWED_FILE_TYPES
import io.cloudflight.jems.server.project.service.contracting.fileManagement.validateConfiguration
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListContractingPartnerFiles(
    private val partnerPersistence: PartnerPersistence,
    private val reportFilePersistence: ProjectReportFilePersistence
): ListContractingPartnerFilesInteractor {

    @CanRetrieveProjectContractingPartner
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListContractingPartnerFilesException::class)
    override fun listPartner(
        partnerId: Long,
        pageable: Pageable,
        searchRequest: ProjectContractingFileSearchRequest
    ): Page<ProjectReportFile> {

        validateConfiguration(searchRequest = searchRequest, partnerId, PARTNER_ALLOWED_FILE_TYPES)
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        val filePathPrefix = ProjectPartnerReportFileType.ContractPartnerDoc.generatePath(projectId, partnerId)

        return reportFilePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = filePathPrefix,
            filterSubtypes = searchRequest.filterSubtypes,
            filterUserIds = emptySet(),
        )
    }

}
