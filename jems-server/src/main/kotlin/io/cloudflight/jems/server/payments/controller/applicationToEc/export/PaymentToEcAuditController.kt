package io.cloudflight.jems.server.payments.controller.applicationToEc.export

import io.cloudflight.jems.api.payments.applicationToEc.PaymentToEcAuditApi
import io.cloudflight.jems.api.payments.dto.export.PaymentToEcExportMetadataDTO
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.payments.service.audit.export.downloadPaymentApplicationToEcAuditExport.DownloadPaymentApplicationToEcAuditExportInteractor
import io.cloudflight.jems.server.payments.service.audit.export.generatePaymentApplicationToEcAuditExport.GeneratePaymentApplicationToEcAuditExportInteractor
import io.cloudflight.jems.server.payments.service.audit.export.listPaymentApplicationToEcAuditExport.ListPaymentApplicationToEcAuditExportInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentToEcAuditController(
    private val generatePaymentApplicationToEcAuditExportInteractor: GeneratePaymentApplicationToEcAuditExportInteractor,
    private val listPaymentApplicationToEcAuditExportInteractor: ListPaymentApplicationToEcAuditExportInteractor,
    private val downloadPaymentApplicationToEcAuditExportInteractor: DownloadPaymentApplicationToEcAuditExportInteractor
) : PaymentToEcAuditApi {

    override fun export(pluginKey: String, programmeFundId: Long?, accountingYearId: Long?) {
        generatePaymentApplicationToEcAuditExportInteractor.export(pluginKey, programmeFundId, accountingYearId)
    }

    override fun list(): List<PaymentToEcExportMetadataDTO> =
        listPaymentApplicationToEcAuditExportInteractor.list().content.map { it.toDto() }

    override fun download(fileId: Long, pluginKey: String): ResponseEntity<ByteArrayResource> {
        return downloadPaymentApplicationToEcAuditExportInteractor.download(
            fileId = fileId,
            pluginKey
        ).toResponseEntity()
    }
}
