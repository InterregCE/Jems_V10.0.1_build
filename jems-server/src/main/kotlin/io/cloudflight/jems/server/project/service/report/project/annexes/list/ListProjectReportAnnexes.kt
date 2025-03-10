package io.cloudflight.jems.server.project.service.report.project.annexes.list

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileSearchRequest
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile.InvalidSearchConfiguration
import io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile.InvalidSearchFilterConfiguration
import io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile.validateSearchConfiguration
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListProjectReportAnnexes(
    private val filePersistence: JemsFilePersistence
) : ListProjectReportAnnexesInteractor {

    companion object {
        private val REMOVE_LAST_ID_REGEX = Regex("\\d+\\/\$")

        private val ALLOWED_FILTERS = mapOf(
            JemsFileType.ProjectReport to setOf(
                JemsFileType.ProjectReport,
                JemsFileType.ActivityProjectReport,
                JemsFileType.DeliverableProjectReport,
                JemsFileType.OutputProjectReport,
                JemsFileType.ProjectResult
            ),
            JemsFileType.WorkPlanProjectReport to setOf(
                JemsFileType.ActivityProjectReport,
                JemsFileType.DeliverableProjectReport,
                JemsFileType.OutputProjectReport
            ),
            JemsFileType.ProjectResult to setOf(JemsFileType.ProjectResult)
        )
    }

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListProjectReportAnnexesException::class)
    override fun list(
        projectId: Long,
        reportId: Long,
        pageable: Pageable,
        searchRequest: JemsFileSearchRequest
    ): Page<JemsFile> {
        validateConfiguration(searchRequest = searchRequest)

        val filePathPrefix = generateSearchString(
            treeNode = searchRequest.treeNode,
            projectId = projectId,
            reportId = searchRequest.reportId,
        )

        val filterSubtypes = searchRequest.filterSubtypes.ifEmpty { ALLOWED_FILTERS.values.flatten().toSet() }

        return filePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = filePathPrefix,
            filterSubtypes = filterSubtypes,
            filterUserIds = emptySet()
        )
    }

    private fun generateSearchString(
        treeNode: JemsFileType,
        projectId: Long,
        reportId: Long,
    ): String {
        return when (treeNode) {
            JemsFileType.ProjectReport, JemsFileType.WorkPlanProjectReport ->
                treeNode.generatePath(projectId, reportId)

            JemsFileType.ProjectResult ->
                treeNode.generatePath(projectId, reportId, 0).replace(regex = REMOVE_LAST_ID_REGEX, "")

            else ->
                throw InvalidSearchConfiguration()
        }
    }

    private fun validateConfiguration(searchRequest: JemsFileSearchRequest) {
        validateSearchConfiguration(
            treeNode = searchRequest.treeNode,
            filterSubtypes = searchRequest.filterSubtypes,
            allowedFilters = ALLOWED_FILTERS,
            { InvalidSearchConfiguration() },
            { invalidFilters -> InvalidSearchFilterConfiguration(invalidFilters) },
        )
    }
}
