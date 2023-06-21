package io.cloudflight.jems.server.project.service.report.partner.control.file

import io.cloudflight.jems.server.project.service.report.model.partner.control.file.PartnerReportControlFile
import java.time.ZonedDateTime

fun validateCertificateFileAttachment(
    file: PartnerReportControlFile,
    lastControlReopening: ZonedDateTime?,
    exceptionResolver: () -> Exception
) {
    if (file.hasAttachment() && file.isGeneratedBefore(lastControlReopening))
        throw exceptionResolver.invoke()
}

private fun PartnerReportControlFile.hasAttachment() = signedFile != null
private fun PartnerReportControlFile.isGeneratedBefore(dateTime: ZonedDateTime?) =
    if(dateTime == null) false else generatedFile.uploaded.isBefore(dateTime)
