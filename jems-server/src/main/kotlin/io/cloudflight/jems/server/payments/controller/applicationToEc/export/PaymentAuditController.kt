package io.cloudflight.jems.server.payments.controller.applicationToEc.export

import io.cloudflight.jems.api.payments.applicationToEc.PaymentAuditApi
import io.cloudflight.jems.api.payments.dto.export.PaymentToEcExportMetadataDTO
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.payments.service.audit.export.downloadPaymentAuditExport.DownloadPaymentAuditExportInteractor
import io.cloudflight.jems.server.payments.service.audit.export.generatePaymentAuditExport.GeneratePaymentAuditExportInteractor
import io.cloudflight.jems.server.payments.service.audit.export.listPaymentApplicationToEcAuditExport.ListPaymentAuditExportInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentAuditController(
    private val generatePaymentAuditExportInteractor: GeneratePaymentAuditExportInteractor,
    private val listPaymentAuditExportInteractor: ListPaymentAuditExportInteractor,
    private val downloadPaymentAuditExportInteractor: DownloadPaymentAuditExportInteractor
) : PaymentAuditApi {

    override fun export(pluginKey: String, programmeFundId: Long?, accountingYearId: Long?) {
        generatePaymentAuditExportInteractor.export(pluginKey, programmeFundId, accountingYearId)
    }

    override fun list(): List<PaymentToEcExportMetadataDTO> =
        listPaymentAuditExportInteractor.list().content.map { it.toDto() }

    override fun download(fileId: Long, pluginKey: String): ResponseEntity<ByteArrayResource> {
        return downloadPaymentAuditExportInteractor.download(
            fileId = fileId,
            pluginKey
        ).toResponseEntity()
    }
}
