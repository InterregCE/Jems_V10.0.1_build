package io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.PartnerReport
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.WorkPlan
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.WorkPackage
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.Activity
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.Deliverable
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.Output
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.Expenditure
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.Procurement
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.Contribution
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.ProcurementAttachment
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileSearchRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListProjectPartnerReportFile(
    private val partnerPersistence: PartnerPersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
) : ListProjectPartnerReportFileInteractor {

    companion object {
        private val REMOVE_LAST_ID_REGEX = Regex("\\d+\\/\$")

        private val ALLOWED_FILTERS = mapOf(
            PartnerReport to setOf(WorkPackage, Activity, Deliverable, Output, Expenditure, ProcurementAttachment, Contribution),
            WorkPlan to setOf(WorkPackage, Activity, Deliverable, Output),
            Expenditure to setOf(Expenditure),
            Procurement to setOf(ProcurementAttachment),
            Contribution to setOf(Contribution),
        )
    }

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListProjectPartnerReportFileException::class)
    override fun list(
        partnerId: Long,
        pageable: Pageable,
        searchRequest: ProjectReportFileSearchRequest,
    ): Page<ProjectReportFile> {
        validateConfiguration(searchRequest = searchRequest)

        val filePathPrefix = generateSearchString(
            treeNode = searchRequest.treeNode,
            projectId = partnerPersistence.getProjectIdForPartnerId(partnerId),
            partnerId = partnerId,
            reportId = searchRequest.reportId,
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
        partnerId: Long,
        reportId: Long,
    ): String {
        return when (treeNode) {
            PartnerReport, WorkPlan ->
                treeNode.generatePath(projectId, partnerId, reportId)
            Expenditure, Procurement, Contribution ->
                treeNode.generatePath(projectId, partnerId, reportId, 0).replace(regex = REMOVE_LAST_ID_REGEX, "")
            else ->
                throw InvalidSearchConfiguration()
        }
    }

    private fun validateConfiguration(searchRequest: ProjectReportFileSearchRequest) {
        validateSearchConfiguration(
            treeNode = searchRequest.treeNode,
            filterSubtypes = searchRequest.filterSubtypes,
            allowedFilters = ALLOWED_FILTERS,
            { InvalidSearchConfiguration() },
            { invalidFilters -> InvalidSearchFilterConfiguration(invalidFilters) },
        )
    }

}
