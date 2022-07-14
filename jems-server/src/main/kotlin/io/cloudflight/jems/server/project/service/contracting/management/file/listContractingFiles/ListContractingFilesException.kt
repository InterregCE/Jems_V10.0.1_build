package io.cloudflight.jems.server.project.service.contracting.management.file.listContractingFiles

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.common.exception.ApplicationException
import io.cloudflight.jems.server.common.exception.ApplicationUnprocessableException
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType

private const val LIST_CONTRACTING_FILES_ERROR_CODE_PREFIX = "S-LCF"
private const val LIST_CONTRACTING_FILES_ERROR_KEY_PREFIX = "use.case.list.contracting.files"

class ListContractingFilesException(cause: Throwable) : ApplicationException(
    code = LIST_CONTRACTING_FILES_ERROR_CODE_PREFIX,
    i18nMessage = I18nMessage("$LIST_CONTRACTING_FILES_ERROR_KEY_PREFIX.failed"),
    cause = cause,
)

class InvalidSearchConfiguration : ApplicationUnprocessableException(
    code = "$LIST_CONTRACTING_FILES_ERROR_CODE_PREFIX-001",
    i18nMessage = I18nMessage("$LIST_CONTRACTING_FILES_ERROR_KEY_PREFIX.invalid.search.configuration"),
)

class InvalidSearchFilterConfiguration(invalidFilters: Set<ProjectPartnerReportFileType>) : ApplicationUnprocessableException(
    code = "$LIST_CONTRACTING_FILES_ERROR_CODE_PREFIX-002",
    i18nMessage = I18nMessage("$LIST_CONTRACTING_FILES_ERROR_KEY_PREFIX.invalid.search.filter.configuration"),
    message = "Following filters cannot be applied: $invalidFilters",
)

class InvalidSearchFilterPartnerWithoutId : ApplicationUnprocessableException(
    code = "$LIST_CONTRACTING_FILES_ERROR_CODE_PREFIX-003",
    i18nMessage = I18nMessage("$LIST_CONTRACTING_FILES_ERROR_KEY_PREFIX.invalid.search.partner.doc.without.partnerId"),
    message = "You can't filter partner documents without partner Id",
)
