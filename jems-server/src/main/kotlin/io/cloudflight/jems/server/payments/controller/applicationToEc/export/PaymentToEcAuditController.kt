package io.cloudflight.jems.server.payments.controller.applicationToEc.export

import io.cloudflight.jems.api.payments.applicationToEc.PaymentToEcAuditApi
import io.cloudflight.jems.api.payments.dto.export.PaymentToEcExportMetadataDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.server.payments.service.audit.export.downloadPaymentApplicationToEcAuditExport.DownloadPaymentApplicationToEcAuditExportInteractor
import io.cloudflight.jems.server.payments.service.audit.export.generatePaymentApplicationToEcAuditExport.GeneratePaymentApplicationToEcAuditExportInteractor
import io.cloudflight.jems.server.payments.service.audit.export.listPaymentApplicationToEcAuditExport.ListPaymentApplicationToEcAuditExportInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate.GenerateReportControlCertificateInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentToEcAuditController(
    private val generatePaymentApplicationToEcAuditExportInteractor: GeneratePaymentApplicationToEcAuditExportInteractor,
    private val listPaymentApplicationToEcAuditExportInteractor: ListPaymentApplicationToEcAuditExportInteractor,
    private val downloadPaymentApplicationToEcAuditExportInteractor: DownloadPaymentApplicationToEcAuditExportInteractor
): PaymentToEcAuditApi {

    override fun export(pluginKey: String, accountingYear: Short?, programmeFundType: ProgrammeFundTypeDTO?) {
        generatePaymentApplicationToEcAuditExportInteractor.export(pluginKey, accountingYear, programmeFundType)
}

    override fun list(): List<PaymentToEcExportMetadataDTO> =
        listPaymentApplicationToEcAuditExportInteractor.list().content.map { it.toDto() }

    override fun download(fileId: Long): ResponseEntity<ByteArrayResource> {
        return with(downloadPaymentApplicationToEcAuditExportInteractor.download(
            fileId = fileId
        )) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
        }
    }
}
