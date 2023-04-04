package io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile

import io.cloudflight.jems.server.common.file.service.model.JemsFileType

fun validateSearchConfiguration(
    treeNode: JemsFileType,
    filterSubtypes: Set<JemsFileType>,
    allowedFilters: Map<JemsFileType, Set<JemsFileType>>,
    invalidSearchExceptionResolver: () -> Exception,
    invalidFilterExceptionResolver: (Set<JemsFileType>) -> Exception,
) {
    if (treeNode !in allowedFilters.keys)
        throw invalidSearchExceptionResolver.invoke()

    val allowedFileTypes = allowedFilters[treeNode]!!
    val invalidFileTypeFilters = filterSubtypes.minus(allowedFileTypes)

    if (invalidFileTypeFilters.isNotEmpty())
        throw invalidFilterExceptionResolver.invoke(invalidFileTypeFilters)
}
