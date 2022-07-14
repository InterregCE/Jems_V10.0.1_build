package io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile

import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType

fun validateSearchConfiguration(
    treeNode: ProjectPartnerReportFileType,
    filterSubtypes: Set<ProjectPartnerReportFileType>,
    allowedFilters: Map<ProjectPartnerReportFileType, Set<ProjectPartnerReportFileType>>,
    invalidSearchExceptionResolver: () -> Exception,
    invalidFilterExceptionResolver: (Set<ProjectPartnerReportFileType>) -> Exception,
) {
    if (treeNode !in allowedFilters.keys)
        throw invalidSearchExceptionResolver.invoke()

    val allowedFileTypes = allowedFilters[treeNode]!!
    val invalidFileTypeFilters = filterSubtypes.minus(allowedFileTypes)

    if (invalidFileTypeFilters.isNotEmpty())
        throw invalidFilterExceptionResolver.invoke(invalidFileTypeFilters)
}
